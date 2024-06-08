package cn.spider.framework.spider.param.config;
import cn.spider.framework.common.event.EventConfig;
import cn.spider.framework.db.config.DbRocksConfig;
import cn.spider.framework.db.config.MysqlConfig;
import cn.spider.framework.domain.sdk.interfaces.NodeInterface;
import cn.spider.framework.param.sdk.interfaces.ParamInterface;
import cn.spider.framework.spider.param.ParamVerticle;
import cn.spider.framework.spider.param.example.ParamExample;
import cn.spider.framework.spider.param.factory.ScopeDataFactory;
import cn.spider.framework.spider.param.function.ParamFunctionImpl;
import cn.spider.framework.spider.param.manager.ParamExampleManager;
import io.vertx.core.Vertx;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Import({DbRocksConfig.class, EventConfig.class, MysqlConfig.class})
public class ParamConfig {

    @Bean
    public Vertx buildVertx() {
        return ParamVerticle.clusterVertx;
    }

    /**
     * 构建参数的对象池
     * @return
     */
    @Bean
    public GenericObjectPool<ParamExample> buildGenericObjectPoolConfig(){
        // 创建对象池配置
        GenericObjectPoolConfig<ParamExample> poolConfig = new GenericObjectPoolConfig<>();
        // 对象池中最大对象数
        poolConfig.setMaxTotal(1500);
        // 对象池中最小空闲对象数
        poolConfig.setMinIdle(1);
        // 对象池中最大空闲对象数
        poolConfig.setMaxIdle(100);
        // 当对象池耗尽时，是否等待获取对象
        poolConfig.setBlockWhenExhausted(true);
        // 创建对象时是否进行对象有效性检查
        poolConfig.setTestOnCreate(true);
        // 借出对象时是否进行对象有效性检查
        poolConfig.setTestOnBorrow(true);
        // 归还对象时是否进行对象有效性检查
        poolConfig.setTestOnReturn(true);
        // 空闲时是否进行对象有效性检查
        poolConfig.setTestWhileIdle(true);
        // 获取对象最大等待时间 默认 -1 一直等待
        poolConfig.setMaxWait(Duration.ofSeconds(5));
        // 创建对象工厂
        ScopeDataFactory objectFactory = new ScopeDataFactory();
        // 创建对象池
        return new GenericObjectPool<>(objectFactory, poolConfig);
    }

    /**
     * 构建node节点的访问接口
     * @param vertx
     * @return
     */
    @Bean
    public NodeInterface buildNodeInterface(Vertx vertx){
        return NodeInterface.createProxy(vertx,NodeInterface.ADDRESS);
    }

    @Bean
    public ParamExampleManager buildParamExampleManager(NodeInterface nodeInterface){
        return new ParamExampleManager(nodeInterface);
    }

    @Bean
    public ParamInterface buildParamInterface(ParamExampleManager paramExampleManager,Executor spiderParamPool){
        return new ParamFunctionImpl(paramExampleManager,spiderParamPool);
    }

    @Bean(name = "spiderParamPool")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池大小
        executor.setCorePoolSize(6);
        //最大线程数
        executor.setMaxPoolSize(12);
        //队列容量-- 用最大程度的
        executor.setQueueCapacity(20);
        //活跃时间
        executor.setKeepAliveSeconds(200);
        //线程名字前缀
        executor.setThreadNamePrefix("spider-param-pool");
        // 拒绝直接报错
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
}
