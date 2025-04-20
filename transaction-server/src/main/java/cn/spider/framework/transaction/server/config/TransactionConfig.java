package cn.spider.framework.transaction.server.config;

import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.db.config.DbRocksConfig;
import cn.spider.framework.db.config.RedissonConfig;
import cn.spider.framework.db.util.RocksdbUtil;
import cn.spider.framework.linker.sdk.interfaces.LinkerService;
import cn.spider.framework.transaction.server.TransactionManager;
import cn.spider.framework.transaction.server.TransactionServerVerticle;
import cn.spider.framework.transaction.server.consumer.RegisterTransactionHandlerV2;
import cn.spider.framework.transaction.server.consumer.TaskRunResultBackHandler;
import cn.spider.framework.transaction.server.consumer.TransactionExampleBackHandler;
import cn.spider.framework.transaction.server.queue.TransactionExceptionQueue;
import cn.spider.framework.transaction.server.transcript.TranscriptManager;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.eventbus.EventBus;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.transaction.server.config
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-07  19:20
 * @Description: TODO
 * @Version: 1.0
 */
@Import({DbRocksConfig.class})
@ComponentScan(basePackages = {"cn.spider.framework.transaction.server.*"})
@Configuration
public class TransactionConfig {

    @Bean
    public Vertx buildVertx() {
        return TransactionServerVerticle.clusterVertx;
    }

    @Bean
    public TransactionManager buildTransactionTransactionManager(
            EventManager eventManager, RocksdbUtil rocksdbUtil, LinkerService linkerService, TransactionExceptionQueue transactionExceptionQueue) {
        return new TransactionManager(eventManager, rocksdbUtil, linkerService, transactionExceptionQueue);
    }

    @Bean
    public TransactionExceptionQueue buildTransactionExceptionQueue(
            Vertx vertx, Executor spiderTransactionPool) {
        return new TransactionExceptionQueue(vertx, spiderTransactionPool);
    }

    @Bean
    public LinkerService buildLinkerService(Vertx vertx) {
        return LinkerService.createProxy(vertx, BrokerInfoUtil.queryBrokerName(vertx) + LinkerService.ADDRESS);
    }


    @Bean
    public TranscriptManager buildTranscriptManager() {
        return new TranscriptManager();
    }

    @Bean
    public EventManager buildEventManager(Vertx vertx) {
        return new EventManager(vertx);
    }

    @Bean
    public EventBus buildEventBus(Vertx vertx) {
        return vertx.eventBus();
    }

    @Bean(name = "spiderTransactionPool")
    public Executor businessExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池大小
        executor.setCorePoolSize(2);
        //最大线程数
        executor.setMaxPoolSize(4);
        //队列容量
        executor.setQueueCapacity(20);
        //活跃时间
        executor.setKeepAliveSeconds(200);
        //线程名字前缀
        executor.setThreadNamePrefix("spider-pool-delete-rocksdb-");
        // 拒绝直接报错
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    @Bean
    public RegisterTransactionHandlerV2 buildRegisterTransactionHandlerV2(EventBus eventBus, TransactionManager transactionManager, Vertx vertx) {
        return new RegisterTransactionHandlerV2(eventBus, transactionManager, vertx);
    }

    @Bean
    public TaskRunResultBackHandler buildTaskRunResultBackHandler(EventBus eventBus, TransactionManager transactionManager, Vertx vertx) {
        return new TaskRunResultBackHandler(eventBus, transactionManager, vertx);
    }

    @Bean
    public TransactionExampleBackHandler buildTransactionExampleBackHandler(EventBus eventBus, TransactionManager transactionManager, Vertx vertx) {
        return new TransactionExampleBackHandler(eventBus, transactionManager, vertx);
    }

}
