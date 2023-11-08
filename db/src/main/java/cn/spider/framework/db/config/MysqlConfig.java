package cn.spider.framework.db.config;

import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import org.springframework.context.annotation.Bean;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.db.config
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-26  17:04
 * @Description: TODO
 * @Version: 1.0
 */
public class MysqlConfig {

    @Bean
    public MySQLPool buildMysqlClient(Vertx vertx) {
        SharedData sharedData = vertx.sharedData();
        LocalMap<String,String> localMap = sharedData.getLocalMap("config");
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setHost(localMap.get("mysql-host"))
                .setPassword(localMap.get("mysql-password"))
                .setUser(localMap.get("mysql-user"))
                .setPort(Integer.parseInt(localMap.get("mysql-port")))
                .setDatabase(localMap.get("mysql-database"));

        // Pool options
        PoolOptions poolOptions = new PoolOptions()
                .setEventLoopSize(10)
                .setIdleTimeout(10 * 1000)
                .setPoolCleanerPeriod(5 * 1000)
                .setMaxSize(5);

        // Create the pooled client
        return MySQLPool.pool(vertx,connectOptions, poolOptions);
    }

}
