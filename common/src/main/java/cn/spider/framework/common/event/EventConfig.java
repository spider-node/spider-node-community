package cn.spider.framework.common.event;

import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfig {

    @Bean
    public EventManager buildEventManager(Vertx vertx){
        return new EventManager(vertx);
    }
}
