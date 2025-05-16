package cn.spider.framework.domain.area.config;

import cn.spider.framework.common.event.EventConfig;
import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.container.sdk.interfaces.ContainerService;
import cn.spider.framework.container.sdk.interfaces.FlowService;
import cn.spider.framework.db.config.MysqlConfig;
import cn.spider.framework.domain.area.AreaManger;
import cn.spider.framework.domain.area.AreaVerticle;
import cn.spider.framework.domain.area.agent.AgentVertxClient;
import cn.spider.framework.domain.area.datasource.DatasourceManager;
import cn.spider.framework.domain.area.datasource.service.IAreaDatasourceInfoService;
import cn.spider.framework.domain.area.domain.service.ISpiderAreaService;
import cn.spider.framework.domain.area.domain.service.ISpiderDomainObjectService;
import cn.spider.framework.domain.area.flowdata.service.ISpiderDataFlowService;
import cn.spider.framework.domain.area.function.FunctionManger;
import cn.spider.framework.domain.area.function.service.ISpiderBusinessFunctionService;
import cn.spider.framework.domain.area.function.service.ISpiderBusinessFunctionVersionService;
import cn.spider.framework.domain.area.function.version.VersionManager;
import cn.spider.framework.domain.area.handler.BizUninstallHandler;
import cn.spider.framework.domain.area.http.HttpFunctionImpl;
import cn.spider.framework.domain.area.http.service.ISpiderToolHttpService;
import cn.spider.framework.domain.area.impl.*;
import cn.spider.framework.domain.area.node.NodeManger;
import cn.spider.framework.domain.area.node.service.ISpiderAreaFunctionService;
import cn.spider.framework.domain.area.node.service.ISpiderAreaFunctionVersionService;
import cn.spider.framework.domain.area.plugin.ApplicationPluginManager;
import cn.spider.framework.domain.area.sondomain.service.IAreaDomainBaseInfoService;
import cn.spider.framework.domain.area.sondomain.service.ISpiderSonAreaService;
import cn.spider.framework.domain.area.task.AiTaskInterfaceImpl;
import cn.spider.framework.domain.area.task.TaskManager;
import cn.spider.framework.domain.area.task.service.ISpiderDomainFunctionAiCoderStepService;
import cn.spider.framework.domain.area.task.service.ISpiderDomainFunctionTaskService;
import cn.spider.framework.domain.area.task.service.ISpiderTaskTestInfoService;
import cn.spider.framework.domain.area.timer.CoderTimer;
import cn.spider.framework.domain.area.tool.FrameworkInterfaceImpl;
import cn.spider.framework.domain.area.tool.service.ISpiderToolFrameworkService;
import cn.spider.framework.domain.area.util.LockManager;
import cn.spider.framework.domain.area.util.OkHttpUtil;
import cn.spider.framework.domain.area.worker.WorkerImpl;
import cn.spider.framework.domain.sdk.interfaces.*;
import cn.spider.framework.log.sdk.interfaces.LogInterface;
import cn.spider.framework.param.sdk.interfaces.ParamInterface;
import cn.spider.node.host.plugin.center.sdk.interfaces.HostPluginInterface;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.client.WebClient;
import io.vertx.mysqlclient.MySQLPool;
import okhttp3.OkHttpClient;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.config
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-15  21:59
 * @Description: 组件配置类
 * @Version: 1.0
 */
@Configuration
@Import({EventConfig.class, MysqlConfig.class})
@ComponentScan(basePackages = {"cn.spider.framework.domain.area.*"})
@MapperScan(value = {"cn.spider.framework.domain.area.flowdata.mapper",
        "cn.spider.framework.domain.area.sondomain.mapper",
        "cn.spider.framework.domain.area.datasource.mapper",
        "cn.spider.framework.domain.area.function.mapper",
        "cn.spider.framework.domain.area.node.mapper",
        "cn.spider.framework.domain.area.task.mapper",
        "cn.spider.framework.domain.area.domain.mapper",
        "cn.spider.framework.domain.area.tool.mapper",
        "cn.spider.framework.domain.area.http.mapper","cn.spider.framework.domain.area.domain.mapper"})
public class DomainConfig {

    @Bean
    public Vertx getVertx() {
        return AreaVerticle.clusterVertx;
    }

    @Bean
    public FunctionManger buildFunctionManger(MySQLPool client, EventManager eventManager, VersionManager versionManager, ISpiderBusinessFunctionService spiderBusinessFunctionService, ISpiderAreaFunctionVersionService spiderAreaFunctionVersionService) {
        return new FunctionManger(client, eventManager, versionManager, spiderBusinessFunctionService, spiderAreaFunctionVersionService);
    }

    @Bean
    public VersionManager buildVersionManager(MySQLPool client, ContainerService containerService, ISpiderBusinessFunctionVersionService spiderBusinessFunctionVersionService, LockManager lockManager, AgentVertxClient agentVertxClient, HostPluginInterface hostPluginInterface,
                                              ISpiderDataFlowService spiderDataFlowService, ISpiderToolHttpService spiderToolHttpService,TaskManager taskManager) {
        return new VersionManager(client, containerService, spiderBusinessFunctionVersionService, lockManager, agentVertxClient, hostPluginInterface, spiderDataFlowService, spiderToolHttpService,taskManager);
    }

    @Bean
    public AreaManger buildAreaManger(MySQLPool client) {
        return new AreaManger(client);
    }

    @Bean
    public NodeManger buildNodeManger(MySQLPool client, AreaManger areaManger, ISpiderAreaFunctionService spiderAreaFunctionService, ISpiderAreaFunctionVersionService spiderAreaFunctionVersionService, ISpiderDomainFunctionTaskService spiderDomainFunctionTaskService, HostPluginInterface hostPluginInterface) {
        return new NodeManger(client, areaManger, spiderAreaFunctionService, spiderAreaFunctionVersionService, spiderDomainFunctionTaskService, hostPluginInterface);
    }

    @Bean
    public WorkerImpl buildWorkerImpl(MySQLPool client) {
        return new WorkerImpl(client);
    }

    @Bean
    public ContainerService buildContainerService(Vertx vertx) {
        return ContainerService.createProxy(vertx, ContainerService.ADDRESS);
    }

    @Bean
    public AreaInterface buildAreaImpl(AreaManger areaManger, ISpiderSonAreaService spiderSonAreaService, DatasourceManager datasourceManager, Executor spiderBusinessPool, ISpiderAreaService spiderAreaService, IAreaDomainBaseInfoService areaDomainBaseInfoService) {
        return new AreaImpl(areaManger, spiderSonAreaService, datasourceManager, spiderBusinessPool, spiderAreaService, areaDomainBaseInfoService);
    }

    @Bean
    public FunctionInterface buildFunctionImpl(FunctionManger functionManger, LogInterface logInterface, Executor spiderBusinessPool) {
        return new FunctionImpl(functionManger, logInterface, spiderBusinessPool);
    }

    @Bean
    public LogInterface buildLogInterface(Vertx vertx) {
        return LogInterface.createProxy(vertx, LogInterface.ADDRESS);
    }


    @Bean
    public ApplicationPluginManager buildApplicationPluginManager(AgentVertxClient agentClient, AreaManger areaManger) {
        return new ApplicationPluginManager(agentClient, areaManger);
    }

    @Bean
    public NodeInterface buildNodeInterface(NodeManger nodeManger, ApplicationPluginManager pluginManager, HostPluginInterface hostPluginInterface, Executor spiderBusinessPool, ISpiderAreaFunctionVersionService spiderAreaFunctionVersionService, ISpiderDataFlowService spiderDataFlowService,
                                            IAreaDomainBaseInfoService areaDomainBaseInfoService,
                                            AgentVertxClient agentVertxClient) {
        return new NodeInterfaceImpl(nodeManger, pluginManager, hostPluginInterface, spiderBusinessPool, spiderAreaFunctionVersionService, spiderDataFlowService, areaDomainBaseInfoService, agentVertxClient);
    }

    @Bean
    public HostPluginInterface buildHostPluginInterface(Vertx vertx) {
        return HostPluginInterface.createProxy(vertx, HostPluginInterface.ADDRESS);
    }

    @Bean
    public ParamInterface buildParamInterface(Vertx vertx) {
        String addr = BrokerInfoUtil.queryBrokerName(vertx) + ParamInterface.ADDRESS;
        return ParamInterface.createProxy(vertx, addr);
    }

    @Bean
    public VersionInterface buildVersionImpl(VersionManager versionManager, Executor spiderBusinessPool, ParamInterface paramInterface) {
        return new VersionImpl(versionManager, spiderBusinessPool, paramInterface);
    }

    @Bean
    public DataFlowInterface buildDataFlowInterface(ISpiderDataFlowService spiderDataFlowService, AgentVertxClient agentVertxClient, TaskManager taskManager) {
        return new DataFlowInterfaceImpl(spiderDataFlowService, agentVertxClient, taskManager);
    }

    @Bean
    public FrameworkInterface buildFrameworkInterface(ISpiderToolFrameworkService spiderToolFrameworkService, Executor spiderBusinessPool) {
        return new FrameworkInterfaceImpl(spiderToolFrameworkService, spiderBusinessPool);
    }

    @Bean
    public OkHttpClient buildHttp() throws NoSuchAlgorithmException, KeyManagementException {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .sslSocketFactory(OkHttpUtil.getIgnoreInitedSslContext().getSocketFactory(), OkHttpUtil.IGNORE_SSL_TRUST_MANAGER_X509)
                .hostnameVerifier(OkHttpUtil.getIgnoreSslHostnameVerifier())
                .build();
    }

    @Bean
    public HttpFunctionInterface buildHttpFunctionInterface(ISpiderToolHttpService spiderToolHttpService, Executor spiderBusinessPool) {
        return new HttpFunctionImpl(spiderToolHttpService, spiderBusinessPool);
    }

    @Bean
    public WebClient buildWebClient(Vertx vertx) {
        return WebClient.create(vertx);
    }

    @Bean
    public AgentVertxClient buildAgentVertxClient(WebClient webClient, Vertx vertx) {
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");
        String agentPrefix = localMap.get("spider_agent_url_host");
        String aiCodePrefix = localMap.get("spider_code_ai_url");
        return new AgentVertxClient(webClient, agentPrefix, aiCodePrefix);
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "transactionTemplate")
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, PaginationInterceptor interceptor) throws Exception {

        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPlugins(interceptor);
        // 可以在这里配置其他属性，如mapperLocations、configuration等
        return factory.getObject();
    }

    @Bean
    public FlowService buildFlowService(Vertx vertx) {
        return FlowService.createProxy(vertx, FlowService.ADDRESS);
    }

    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor buildPaginationInterceptor() {
        return new PaginationInterceptor();
    }

    @Bean
    public DataSource dataSource(Vertx vertx) {
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(localMap.get("mysql-url"));
        dataSource.setUsername(localMap.get("mysql-user"));
        dataSource.setPassword(localMap.get("mysql-password"));
        dataSource.setDriverClassName(localMap.get("mysql-driver-class-name"));
        // 配置初始化大小、最小、最大
        dataSource.setInitialSize(Integer.parseInt(localMap.get("mysql-init-size")));
        dataSource.setMinIdle(Integer.parseInt(localMap.get("mysql-min-idle")));
        dataSource.setMaxActive(6000);

        // 配置获取连接等待超时的时间
        dataSource.setMaxWait(60000);

        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        dataSource.setTimeBetweenEvictionRunsMillis(60000);

        // 配置一个连接在池中最小生存的时间，单位是毫秒
        dataSource.setMinEvictableIdleTimeMillis(300000);

        // 用来测试连接是否有效的SQL
        dataSource.setValidationQuery("SELECT 1");

        // 建议配置为true，不影响性能，并且保证安全性
        dataSource.setTestWhileIdle(true);

        // 配置监控统计拦截的filters
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);

        // 打开PSCache，并且指定每个连接上PSCache的大小
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);

        return dataSource;
    }

    @Bean(name = "spiderParamPool")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池大小
        executor.setCorePoolSize(1);
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

    @Bean(name = "spiderBusinessPool")
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
    public DatasourceManager buildDatasourceManager(IAreaDatasourceInfoService datasourceInfoService, Vertx vertx) {
        return new DatasourceManager(datasourceInfoService, vertx);
    }

    @Bean
    public AiTaskInterfaceImpl buildAiTask(TaskManager taskManager, Executor spiderBusinessPool,
                                           ISpiderTaskTestInfoService spiderTaskTestInfoService,
                                           DatasourceManager datasourceManager) {
        return new AiTaskInterfaceImpl(taskManager, spiderBusinessPool, spiderTaskTestInfoService, datasourceManager);
    }

    @Bean
    public TaskManager buildTaskManager(ISpiderAreaFunctionVersionService spiderAreaFunctionVersionService,
                                        ISpiderAreaFunctionService spiderAreaFunctionService,
                                        HostPluginInterface hostPluginInterface,
                                        IAreaDomainBaseInfoService baseInfoService,
                                        ISpiderDomainFunctionTaskService spiderDomainFunctionTaskService,
                                        AgentVertxClient agentVertxClient, ISpiderDomainFunctionAiCoderStepService stepService, ISpiderDataFlowService dataFlowService, LockManager lockManager, DatasourceManager datasourceManager) {
        return new TaskManager(spiderAreaFunctionVersionService, spiderAreaFunctionService, baseInfoService, spiderDomainFunctionTaskService, agentVertxClient, stepService, dataFlowService, hostPluginInterface, lockManager, datasourceManager);
    }

    @Bean
    public CoderTimer buildCoderTimer(Vertx vertx, ISpiderTaskTestInfoService taskTestInfoService) {
        return new CoderTimer(vertx, taskTestInfoService);
    }

    @Bean
    public LockManager buildLockManager(CoderTimer coderTimer) {
        return new LockManager(coderTimer);
    }

    @Bean
    public BizUninstallHandler buildBizUninstallHandler(AgentVertxClient agentVertxClient, Vertx vertx) {
        return new BizUninstallHandler(agentVertxClient, vertx);
    }

    @Bean
    public DomainObjectInterface buildDomainObject(ISpiderDomainObjectService spiderDomainObjectService) {
        return new DomainObjectInterfaceImpl(spiderDomainObjectService);
    }

}
