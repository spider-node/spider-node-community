package cn.spider.framework.spider.log.es.config;
import cn.spider.framework.log.sdk.interfaces.LogInterface;
import cn.spider.framework.spider.log.es.LogVerticle;
import cn.spider.framework.spider.log.es.consumer.LogConsumer;
import cn.spider.framework.spider.log.es.queue.QueueManager;
import cn.spider.framework.spider.log.es.service.SpiderFlowElementExampleService;
import cn.spider.framework.spider.log.es.service.SpiderFlowExampleLogService;
import cn.spider.framework.spider.log.es.service.impl.LogInterfaceImpl;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.spider.log.es.config
 * @Author: dengdongsheng
 * @CreateTime: 2023-10-06  18:33
 * @Description: TODO
 * @Version: 1.0
 */
@Configuration
public class CommonConfig {
    @Bean
    public QueueManager buildQueueManager(Vertx vertx, SpiderFlowElementExampleService spiderFlowElementExampleService, SpiderFlowExampleLogService exampleLogService, Executor spiderLogPool){
        return new QueueManager(vertx,spiderFlowElementExampleService,exampleLogService,spiderLogPool);
    }

    @Bean
    public LogInterface buildLogInterface( SpiderFlowElementExampleService spiderFlowElementExampleService, SpiderFlowExampleLogService exampleLogService){
        return new LogInterfaceImpl(spiderFlowElementExampleService,exampleLogService);
    }

    @Bean
    public Vertx buildVertx(){
        return LogVerticle.clusterVertx;
    }

    @Bean
    public LogConsumer buildLogConsumer(EventBus eventBus, QueueManager queueManager, Vertx vertx){
        return new LogConsumer(queueManager,eventBus,vertx);
    }

    @Bean
    public EventBus buildEventBus(Vertx vertx){
        return vertx.eventBus();
    }

    @Bean(name = "spiderLogPool")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池大小
        executor.setCorePoolSize(4);
        //最大线程数
        executor.setMaxPoolSize(8);
        //队列容量
        executor.setQueueCapacity(100);
        //活跃时间
        executor.setKeepAliveSeconds(200);
        //线程名字前缀
        executor.setThreadNamePrefix("spider-pool-log");
        // 拒绝直接报错
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
}
