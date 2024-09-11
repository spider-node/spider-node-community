package cn.spider.framework.domain.area.config;

import cn.spider.framework.common.event.EventConfig;
import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.container.sdk.interfaces.ContainerService;
import cn.spider.framework.db.config.MysqlConfig;
import cn.spider.framework.domain.area.AreaManger;
import cn.spider.framework.domain.area.AreaVerticle;
import cn.spider.framework.domain.area.agent.AgentOkhttpClient;
import cn.spider.framework.domain.area.agent.AgentVertxClient;
import cn.spider.framework.domain.area.function.FunctionManger;
import cn.spider.framework.domain.area.function.version.VersionManager;
import cn.spider.framework.domain.area.impl.AreaImpl;
import cn.spider.framework.domain.area.impl.FunctionImpl;
import cn.spider.framework.domain.area.impl.NodeInterfaceImpl;
import cn.spider.framework.domain.area.impl.VersionImpl;
import cn.spider.framework.domain.area.node.NodeManger;
import cn.spider.framework.domain.area.plugin.ApplicationPluginManager;
import cn.spider.framework.domain.area.util.OkHttpUtil;
import cn.spider.framework.domain.area.worker.WorkerImpl;
import cn.spider.framework.domain.sdk.interfaces.AreaInterface;
import cn.spider.framework.domain.sdk.interfaces.FunctionInterface;
import cn.spider.framework.domain.sdk.interfaces.NodeInterface;
import cn.spider.framework.domain.sdk.interfaces.VersionInterface;
import cn.spider.framework.log.sdk.interfaces.LogInterface;
import cn.spider.framework.param.result.build.interfaces.ParamRefreshInterface;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import io.vertx.core.Vertx;
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
@MapperScan("cn.spider.framework.domain.area.*")
public class DomainConfig {

    @Bean
    public Vertx getVertx() {
        return AreaVerticle.clusterVertx;
    }

    @Bean
    public FunctionManger buildFunctionManger(MySQLPool client, EventManager eventManager, VersionManager versionManager) {
        return new FunctionManger(client, eventManager, versionManager);
    }

    @Bean
    public VersionManager buildVersionManager(MySQLPool client, ContainerService containerService) {
        return new VersionManager(client, containerService);
    }

    @Bean
    public AreaManger buildAreaManger(MySQLPool client, ContainerService containerService, ParamRefreshInterface paramRefreshInterface) {
        return new AreaManger(client, containerService, paramRefreshInterface);
    }

    @Bean
    public ParamRefreshInterface buildParamRefreshInterface(Vertx vertx) {
        return ParamRefreshInterface.createProxy(vertx, ParamRefreshInterface.ADDRESS);
    }

    @Bean
    public NodeManger buildNodeManger(MySQLPool client, AreaManger areaManger) {
        return new NodeManger(client, areaManger);
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
    public AreaInterface buildAreaImpl(AreaManger areaManger) {
        return new AreaImpl(areaManger);
    }

    @Bean
    public FunctionInterface buildFunctionImpl(FunctionManger functionManger, LogInterface logInterface) {
        return new FunctionImpl(functionManger, logInterface);
    }

    @Bean
    public LogInterface buildLogInterface(Vertx vertx) {
        return LogInterface.createProxy(vertx, LogInterface.ADDRESS);
    }


    @Bean
    public ApplicationPluginManager buildApplicationPluginManager(AgentVertxClient agentClient,AreaManger areaManger){
        return new ApplicationPluginManager(agentClient,areaManger);
    }

    @Bean
    public NodeInterface buildNodeInterface(NodeManger nodeManger, ApplicationPluginManager pluginManager) {
        return new NodeInterfaceImpl(nodeManger,pluginManager);
    }

    @Bean
    public VersionInterface buildVersionImpl(VersionManager versionManager) {
        return new VersionImpl(versionManager);
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
    public WebClient buildWebClient(Vertx vertx){
        return WebClient.create(vertx);
    }

    @Bean
    public AgentVertxClient buildAgentVertxClient(WebClient webClient,Vertx vertx){
        SharedData sharedData = vertx.sharedData();
        LocalMap<String,String> localMap = sharedData.getLocalMap("config");
        String agentPrefix = localMap.get("spider_agent_url_host");
        String aiCodePrefix = localMap.get("spider_code_ai_url");
        return new AgentVertxClient(webClient,agentPrefix,aiCodePrefix);
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
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {

        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        // 可以在这里配置其他属性，如mapperLocations、configuration等
        return factory.getObject();
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
