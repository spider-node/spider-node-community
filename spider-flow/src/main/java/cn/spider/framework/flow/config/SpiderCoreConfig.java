package cn.spider.framework.flow.config;

import cn.spider.framework.common.event.EventConfig;
import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.container.sdk.interfaces.BusinessService;
import cn.spider.framework.container.sdk.interfaces.ContainerService;
import cn.spider.framework.controller.sdk.interfaces.LeaderHeartService;
import cn.spider.framework.controller.sdk.interfaces.RoleService;
import cn.spider.framework.controller.sdk.interfaces.RoleServiceVertxEBProxy;
import cn.spider.framework.db.config.DbRocksConfig;
import cn.spider.framework.db.config.MysqlConfig;
import cn.spider.framework.db.config.RedissonConfig;
import cn.spider.framework.db.rocksdb.RocksdbKeyManager;
import cn.spider.framework.db.util.RocksdbUtil;
import cn.spider.framework.domain.sdk.interfaces.AreaInterface;
import cn.spider.framework.domain.sdk.interfaces.FunctionInterface;
import cn.spider.framework.domain.sdk.interfaces.VersionInterface;
import cn.spider.framework.flow.business.BusinessManager;
import cn.spider.framework.flow.consumer.business.EndFlowDeleteRocksdbHandler;
import cn.spider.framework.flow.consumer.business.FlowExampleDelayHandler;
import cn.spider.framework.flow.consumer.business.FlowExampleRemoveDelayHandler;
import cn.spider.framework.flow.consumer.system.*;
import cn.spider.framework.flow.container.component.TaskComponentManager;
import cn.spider.framework.flow.container.component.TaskContainer;
import cn.spider.framework.flow.delayQueue.DelayQueueManager;
import cn.spider.framework.flow.delayQueue.RedisDelayQueueUtil;
import cn.spider.framework.flow.delayQueue.handler.FlowExampleNodeDelayHandler;
import cn.spider.framework.flow.delayQueue.handler.FlowExampleRemoveHandler;
import cn.spider.framework.flow.engine.StoryEngine;
import cn.spider.framework.flow.engine.example.ExampleDestroyManager;
import cn.spider.framework.flow.engine.scheduler.SchedulerManager;
import cn.spider.framework.flow.funtion.InitLoaderClassService;
import cn.spider.framework.flow.load.loader.ClassLoaderManager;
import cn.spider.framework.flow.load.loader.HotClassLoader;
import cn.spider.framework.flow.SpiderCoreVerticle;
import cn.spider.framework.flow.resource.factory.StartEventFactory;
import cn.spider.framework.flow.sync.Publish;
import cn.spider.framework.flow.sync.SyncBusinessRecord;
import cn.spider.framework.flow.timer.SpiderTimer;
import cn.spider.framework.flow.timer.SystemTimer;
import cn.spider.framework.flow.transcript.TranscriptManager;
import cn.spider.framework.linker.sdk.interfaces.LinkerService;
import cn.spider.framework.param.sdk.interfaces.ParamInterface;
import cn.spider.framework.transaction.sdk.interfaces.TransactionInterface;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.eventbus.EventBus;
import io.vertx.mysqlclient.MySQLPool;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.config
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-15  13:01
 * @Description: spring-配置类
 * @Version: 1.0
 */
@Configuration
@ComponentScan(basePackages = {"cn.spider.framework.flow.*"})
@Import({DbRocksConfig.class, EventConfig.class, MysqlConfig.class, RedissonConfig.class})
@Order(-1)
public class SpiderCoreConfig {
    @Bean
    public Vertx buildVertx() {
        return SpiderCoreVerticle.clusterVertx;
    }

    /**
     * 构造vertx的线程池
     *
     * @param vertx
     * @return
     */
    @Bean("businessExecute")
    public WorkerExecutor buildWorkerExecutor(Vertx vertx) {
        int poolSize = 20;
        // 2 minutes
        long maxExecuteTime = 2;
        TimeUnit maxExecuteTimeUnit = TimeUnit.MINUTES;
        WorkerExecutor executor = vertx.createSharedWorkerExecutor("flow-worker-pool", poolSize, maxExecuteTime, maxExecuteTimeUnit);
        return executor;
    }

    @Bean
    public ClassLoaderManager buildClassLoaderManager(TaskContainer container, SchedulerManager schedulerManager) {
        ClassLoaderManager classLoaderManager = new ClassLoaderManager();
        classLoaderManager.init((TaskComponentManager) container, schedulerManager);
        return classLoaderManager;
    }

    /**
     * 跟worker通信 接口
     *
     * @param vertx
     * @return
     */
    @Bean
    public LinkerService buildLinkerService(Vertx vertx) {
        return LinkerService.createProxy(vertx, BrokerInfoUtil.queryBrokerName(vertx) + LinkerService.ADDRESS);
    }

    @Bean
    public TransactionInterface buildTransactionInterface(Vertx vertx){
        return TransactionInterface.createProxy(vertx,BrokerInfoUtil.queryBrokerName(vertx) + TransactionInterface.ADDRESS);
    }

    @Bean
    /** 配置成原型（多例），主要是为了更新jar时，使用新的类加载器实例去加载*/
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HotClassLoader hotClassLoader() {
        return new HotClassLoader(this.getClass().getClassLoader());
    }

    @Bean
    public SchedulerManager buildSchedulerManager(LinkerService linkerService, EventManager eventManager) {
        return new SchedulerManager(linkerService,eventManager);
    }

    @Bean("classLoaderMap")
    public Map<String, ClassLoader> buildClassLoaderMap() {
        return new HashMap<>();
    }

    /**
     * 业务管理器-- 存功能版本信息
     *
     * @param
     * @return
     */
    @Bean
    public BusinessManager buildBusinessManager(MySQLPool client, FunctionInterface functionInterface) {
        return new BusinessManager(client,functionInterface);
    }

    @Bean
    public FunctionInterface buildFunctionInterface(Vertx vertx){
        return FunctionInterface.createProxy(vertx,FunctionInterface.ADDRESS);
    }

    /**
     * 把同步数据的对象加入容器
     *
     * @param vertx
     * @param businessService
     * @param containerService
     * @return
     */
    @Bean
    public SyncBusinessRecord buildSyncManager(Vertx vertx, BusinessService businessService, ContainerService containerService) {
        return new SyncBusinessRecord(vertx, businessService, containerService);
    }

    @Bean
    public Publish buildPublish(Vertx vertx) {
        return new Publish(vertx);
    }

    @Bean
    public TranscriptManager buildTranscriptManager(EventManager eventManager,
                                                    Vertx vertx,
                                                    TransactionInterface transactionInterface,
                                                    SpiderTimer timer){

        return new TranscriptManager(eventManager,vertx,transactionInterface,timer);
    }

    @Bean
    public EventBus buildEventBus(Vertx vertx){
        return vertx.eventBus();
    }

    @Bean
    public RoleService buildRoleService(Vertx vertx){
        String addr = BrokerInfoUtil.queryBrokerName(vertx)+ LeaderHeartService.ADDRESS;
        return new RoleServiceVertxEBProxy(vertx,addr);
    }

    @Bean
    public ParamInterface buildParamInterface(Vertx vertx){
        String addr = BrokerInfoUtil.queryBrokerName(vertx)+ ParamInterface.ADDRESS;
        return ParamInterface.createProxy(vertx,addr);
    }

    @Bean
    public EndFlowDeleteRocksdbHandler buildEndFlowDeleteRocksdbHandler(RocksdbUtil rocksdbUtil, Vertx vertx, EventBus eventBus, Executor spiderDeleterRocksdbPool, RocksdbKeyManager rocksdbKeyManager){
        return new EndFlowDeleteRocksdbHandler(rocksdbUtil,vertx,eventBus,spiderDeleterRocksdbPool,rocksdbKeyManager);
    }

    @Bean(name = "spiderDeleterRocksdbPool")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池大小
        executor.setCorePoolSize(8);
        //最大线程数
        executor.setMaxPoolSize(16);
        //队列容量
        executor.setQueueCapacity(200);
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
    public AsyncDestroyBpmnHandler buildAsyncDestroyBpmnHandler(EventBus eventBus, Vertx vertx, StartEventFactory startEventFactory){
        return new AsyncDestroyBpmnHandler(eventBus,vertx,startEventFactory);
    }

    @Bean
    public SyncDeployBpmnHandler buildSyncDeployBpmnHandler(EventBus eventBus, StartEventFactory startEventFactory, Vertx vertx){
        return new SyncDeployBpmnHandler(eventBus,startEventFactory,vertx);
    }

    @Bean
    public AsyncLoaderClassHandler buildAsyncLoaderClassHandler(EventBus eventBus, Vertx vertx, ClassLoaderManager classLoaderManager){
        return new AsyncLoaderClassHandler(eventBus,vertx,classLoaderManager);
    }

    @Bean
    public AsyncDestroyClassHandler buildAsyncDestroyClassHandler(EventBus eventBus, Vertx vertx, ClassLoaderManager classLoaderManager){
        return new AsyncDestroyClassHandler(eventBus,vertx,classLoaderManager);
    }

    @Bean
    public ExampleDestroyManager buildExampleDestroyManager(Vertx vertx, Executor spiderDeleterRocksdbPool, StoryEngine storyEngine,RocksdbKeyManager rocksdbKeyManager){
        return new ExampleDestroyManager(vertx,spiderDeleterRocksdbPool,storyEngine,rocksdbKeyManager);
    }

    @Bean
    public StopFunctionHandler buildStopFunctionHandler(EventBus eventBus, BusinessManager businessManager){
        return new StopFunctionHandler(eventBus,businessManager);
    }

    @Bean("flow_delete")
    public FlowExampleRemoveHandler buildFlowExampleRemoveHandler(EventManager eventManager){
        return new FlowExampleRemoveHandler(eventManager);
    }

    @Bean("flow_delay_example")
    public FlowExampleNodeDelayHandler buildFlowExampleNodeDelayHandler(EventManager eventManager){
        return new FlowExampleNodeDelayHandler(eventManager);
    }

    @Bean
    public RedisDelayQueueUtil buildRedisDelayQueueUtil(RedissonClient redissonClient){
        return new RedisDelayQueueUtil(redissonClient);
    }

    /**
     * 需要延迟进的数据
     * @param vertx
     * @param redisDelayQueueUtil
     * @param businessExecute
     * @return
     */
    @Bean
    public DelayQueueManager buildDelayQueueManager(Vertx vertx, RedisDelayQueueUtil redisDelayQueueUtil, WorkerExecutor businessExecute){
        return new DelayQueueManager(vertx,redisDelayQueueUtil,businessExecute);
    }

    /**
     * 需要延迟执行的数据
     * @param eventBus
     * @param vertx
     * @param storyEngine
     * @return
     */
    @Bean
    public FlowExampleDelayHandler buildFlowExampleDelayHandler(EventBus eventBus, Vertx vertx,StoryEngine storyEngine){
        return new FlowExampleDelayHandler(eventBus,vertx,storyEngine);
    }

    /**
     * 消费 需要延迟删除的数据
     * @param eventBus
     * @param vertx
     * @param exampleDestroyManager
     * @return
     */
    @Bean
    public FlowExampleRemoveDelayHandler buildFlowExampleRemoveDelayHandler(EventBus eventBus, Vertx vertx,ExampleDestroyManager exampleDestroyManager){
        return new FlowExampleRemoveDelayHandler(eventBus,vertx,exampleDestroyManager);
    }

    @Bean
    public AreaInterface buildAreaInterface(Vertx vertx){
        return AreaInterface.createProxy(vertx,AreaInterface.ADDRESS);
    }

    @Bean
    public VersionInterface buildVersionInterface(Vertx vertx){
        return VersionInterface.createProxy(vertx,VersionInterface.ADDRESS);
    }
    @Bean
    public SystemTimer buildSystemTimer(Vertx vertx, StartEventFactory startEventFactory, InitLoaderClassService initLoaderClassService){
        return new SystemTimer(vertx,startEventFactory,initLoaderClassService);
    }

}
