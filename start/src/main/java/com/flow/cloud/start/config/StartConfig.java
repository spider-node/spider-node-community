package com.flow.cloud.start.config;
import cn.spider.framework.db.config.DbRocksConfig;
import com.flow.cloud.start.SpiderStart;
import io.vertx.core.Vertx;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: com.flow.cloud.start.config
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-07  20:35
 * @Description: TODO
 * @Version: 1.0
 */
@Import({DbRocksConfig.class})
@Configuration
public class StartConfig {
    @Bean
    public Vertx buildVertx(){
        return SpiderStart.vertxNew;
    }

}
