package cn.spider.framework.db.util;

import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.MasterSlaveServersConfig;
import org.redisson.config.SentinelServersConfig;

import java.util.Objects;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.db.util
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-21  13:25
 * @Description: TODO
 * @Version: 1.0
 */
public class RedissonClientUtil {
    private static RedissonClient redissonClient;

    public static RedissonClient getRedissonClient(Vertx vertx){
        if(Objects.isNull(redissonClient)){
            SharedData sharedData = vertx.sharedData();
            LocalMap<String, String> localMap = sharedData.getLocalMap("config");
            switch (localMap.get("redis-type")) {
                case "standAlone":
                    redissonClient = standAlone(localMap);
                    break;
                case "sentinel":
                    redissonClient = sentinel(localMap);
                    break;
                case "masterSlave":
                    redissonClient = masterSlave(localMap);
                    break;
                case "cluster":
                    redissonClient = cluster(localMap);

            }
        }
        return redissonClient;
    }

    private static RedissonClient standAlone(LocalMap<String, String> localMap) {
        Config config = new Config();
        String address = new StringBuilder("redis://")
                .append(localMap.get("redis-host-name"))
                .append(":").append(localMap.get("redis-port"))
                .toString();
        config.useSingleServer()
                .setPassword(localMap.get("redis-password"))
                .setAddress(address);
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }

    /**
     * 哨兵模式
     *
     * @return
     */
    private static RedissonClient sentinel(LocalMap<String, String> localMap) {

        String configAddr = localMap.get("redis-host-name");

        Config config = new Config();
        SentinelServersConfig sentinelServersConfig = config.useSentinelServers()
                .setPassword(localMap.get("redis-password"))
                .setMasterConnectionPoolSize(Integer.parseInt(localMap.get("redis-core")))
                .setMasterConnectionMinimumIdleSize(Integer.parseInt(localMap.get("redis-minIdle")))
                .setSlaveConnectionPoolSize(Integer.parseInt(localMap.get("redis-core")))
                .setSlaveConnectionMinimumIdleSize(Integer.parseInt(localMap.get("redis-minIdle")))
                .setConnectTimeout(Integer.parseInt(localMap.get("redis-timeout")))
                .setPingConnectionInterval(Integer.parseInt(localMap.get("redis-pingConnectionInterval")))
                .setMasterName(localMap.get("redis-master-name"));
        String[] redisAddress = configAddr.split(",");

        for (String addr : redisAddress) {
            String address = new StringBuilder("redis://").append(addr).append(":").append(localMap.get("redis-port")).toString();
            sentinelServersConfig.addSentinelAddress(address);
        }
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

    /**
     * 主从模式
     *
     * @return
     */
    private static RedissonClient masterSlave(LocalMap<String, String> localMap) {
        Config config = new Config();
        String masterAddr = localMap.get("redis-master-address");

        String address = new StringBuilder("redis://")
                .append(masterAddr)
                .append(":").append(localMap.get("redis-port"))
                .toString();
        MasterSlaveServersConfig slaveServersConfig = config.useMasterSlaveServers()
                //可以用"rediss://"来启用SSL连接
                .setPassword(localMap.get("redis-password"))
                .setMasterAddress(address);

        String slaveAddress = localMap.get("redis-slave-address");
        String[] slaveAddressList = slaveAddress.split(",");

        for (String slaveAddr : slaveAddressList) {
            String addressSlave = new StringBuilder("redis://")
                    .append(slaveAddr)
                    .append(":").append(localMap.get("redis-port"))
                    .toString();
            slaveServersConfig.addSlaveAddress(addressSlave);
        }
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

    /**
     * 集群模式
     *
     * @param localMap
     * @return
     */
    private static RedissonClient cluster(LocalMap<String, String> localMap) {
        String nodeAddress = localMap.get("redis-node-address");

        String[] nodeAddressList = nodeAddress.split(",");
        Config config = new Config();
        ClusterServersConfig clusterServersConfig = config.useClusterServers()
                .setPassword(localMap.get("redis-password"))
                .setScanInterval(2000);
        for (String addr : nodeAddressList) {
            String addressNode = new StringBuilder("redis://")
                    .append(addr)
                    .append(":").append(localMap.get("redis-port"))
                    .toString();
            clusterServersConfig.addNodeAddress(addressNode);
        }
        RedissonClient redisson = Redisson.create(config);

        return redisson;
    }

}
