package cn.spider.framework.linker.client.config;

import cn.spider.framework.linker.client.task.TaskManager;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.client.WebClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.linker.client.config
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-16  16:01
 * @Description: TODO
 * @Version: 1.0
 */
public class CommonConfig {

    @Bean(name = "spiderTaskPool")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池大小
        executor.setCorePoolSize(20);
        //最大线程数
        executor.setMaxPoolSize(40);
        //队列容量
        executor.setQueueCapacity(4000);
        //活跃时间
        executor.setKeepAliveSeconds(200);
        //线程名字前缀
        executor.setThreadNamePrefix("spider-pool-");
        // 拒绝直接报错
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    @Bean
    public TaskManager buildTaskManager(ApplicationContext applicationContext,
                                        Executor spiderTaskPool,
                                        PlatformTransactionManager platformTransactionManager,
                                        TransactionDefinition transactionDefinition) {
        return new TaskManager(applicationContext, spiderTaskPool, platformTransactionManager, transactionDefinition);
    }

    @Bean
    public WebClient buildClient(Vertx vertx) {
        return WebClient.create(vertx);
    }
}
