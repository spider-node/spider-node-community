package cn.spider.framework.common.event;

import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.common.event
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-19  15:19
 * @Description: TODO
 * @Version: 1.0
 */
@Configuration
public class EventConfig {

    @Bean
    public EventManager buildEventManager(Vertx vertx){
        return new EventManager(vertx);
    }
}
