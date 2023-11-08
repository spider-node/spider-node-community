package cn.spider.framework.transaction.server.config;

import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.db.config.DbRocksConfig;
import cn.spider.framework.db.config.RedissonConfig;
import cn.spider.framework.db.util.RocksdbUtil;
import cn.spider.framework.linker.sdk.interfaces.LinkerService;
import cn.spider.framework.transaction.server.TransactionManager;
import cn.spider.framework.transaction.server.TransactionServerVerticle;
import cn.spider.framework.transaction.server.transcript.TranscriptManager;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.eventbus.EventBus;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.transaction.server.config
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-07  19:20
 * @Description: TODO
 * @Version: 1.0
 */
@Import({RedissonConfig.class, DbRocksConfig.class})
@ComponentScan(basePackages = {"cn.spider.framework.transaction.server.*"})
@Configuration
public class TransactionConfig {

    @Bean
    public Vertx buildVertx() {
        return TransactionServerVerticle.clusterVertx;
    }

    @Bean
    public TransactionManager buildTransactionTransactionManager(LinkerService linkerService,
                                                                 WorkerExecutor workerExecutor,
                                                                 RocksdbUtil rocksdbUtil,
                                                                 EventManager eventManager,
                                                                 RedissonClient redissonClient) {
        return new TransactionManager(workerExecutor, linkerService, rocksdbUtil,eventManager,redissonClient);
    }

    @Bean
    public LinkerService buildLinkerService(Vertx vertx) {
        return LinkerService.createProxy(vertx, BrokerInfoUtil.queryBrokerName(vertx) + LinkerService.ADDRESS);
    }

    @Bean("businessExecute")
    public WorkerExecutor buildWorkerExecutor(Vertx vertx) {
        int poolSize = 10;
        // 2 minutes
        long maxExecuteTime = 2;
        TimeUnit maxExecuteTimeUnit = TimeUnit.MINUTES;
        WorkerExecutor executor = vertx.createSharedWorkerExecutor("my-worker-pool", poolSize, maxExecuteTime, maxExecuteTimeUnit);
        return executor;
    }

    @Bean
    public TranscriptManager buildTranscriptManager(){
        return new TranscriptManager();
    }

    @Bean
    public EventManager buildEventManager(Vertx vertx){
        return new EventManager(vertx);
    }

    @Bean
    public EventBus buildEventBus(Vertx vertx){
        return vertx.eventBus();
    }

}
