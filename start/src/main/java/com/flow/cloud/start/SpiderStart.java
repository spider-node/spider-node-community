package com.flow.cloud.start;
import com.flow.cloud.start.role.RoleManager;
import com.flow.cloud.start.util.ConfigUtil;
import com.hazelcast.config.Config;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.Objects;
import static com.flow.cloud.start.util.BannerHelper.banner;

/**
 * @Classname FlowStart
 * @Description spider-统一启动类
 * @Date 2021/10/22 23:55
 * @Created dds
 */
@Slf4j
public class SpiderStart {
    public static Vertx vertxNew;

    public static void main(String[] args) throws Exception {
        // 设置启动图案
        banner(1);
        ClusterManager clusterManager = buildCluster();
        if(Objects.isNull(clusterManager)){
            throw new Exception("请配置的集群模式");
        }
        // 设置集群类型
        VertxOptions options = new VertxOptions().setClusterManager(clusterManager);
        options.setWorkerPoolSize(20);
        log.info("spider-start");
        // 加入集群
        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                RoleManager roleManager = new RoleManager(vertx);
                roleManager.start();
                // 蒋装载的信息放入 sharedData
                log.info("spider-suss");
            } else {
                // failed!
                log.info("spider-fail");
            }
        });
    }

    private static ClusterManager buildCluster() {
        Map<String, String> config = ConfigUtil.queryZkAddr();
        if(config.get("cluster-type").equals("zk")){
            return buildZk(config);
        }else if(config.get("cluster-type").equals("hazelcast")){
            return buildHazelcast();
        }
        return null;
    }

    private static ClusterManager buildZk(Map<String, String> config) {
        // 获取集群方式
        JsonObject zkConfig = new JsonObject();
        zkConfig.put("zookeeperHosts", config.get("zk-addr"));
        zkConfig.put("rootPath", "spider.node");
        zkConfig.put("retry", new JsonObject()
                .put("initialSleepTime", 3000)
                .put("maxTimes", 3));

        return new ZookeeperClusterManager(zkConfig);
    }

    private static ClusterManager buildHazelcast(){
        Config hazelcastConfig = Config.load();
        // 设置集群类型
        return new HazelcastClusterManager(hazelcastConfig);
    }
}