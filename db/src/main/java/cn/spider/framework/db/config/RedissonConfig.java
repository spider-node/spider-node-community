package cn.spider.framework.db.config;

import cn.spider.framework.db.util.RedissonClientUtil;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.MasterSlaveServersConfig;
import org.redisson.config.SentinelServersConfig;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.db.config
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-04  18:52
 * @Description: TODO
 * @Version: 1.0
 */
public class RedissonConfig {

    @Bean
    public RedissonClient redisson(Vertx vertx) throws IOException {
        return RedissonClientUtil.getRedissonClient(vertx);
    }
}
