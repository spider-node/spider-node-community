package cn.spider.node.host.plugin.center.config;

import cn.spider.node.host.plugin.center.MainVerticle;
import cn.spider.node.host.plugin.center.application.HostApplicationManager;
import cn.spider.node.host.plugin.center.application.http.HostApplicationClient;
import cn.spider.node.host.plugin.center.event.HostApplicationOfflineHandler;
import cn.spider.node.host.plugin.center.event.HostApplicationOnlineHandler;
import cn.spider.node.host.plugin.center.task.TaskManager;
import cn.spider.node.host.plugin.center.timer.TaskTimer;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.client.WebClient;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = {"cn.spider.node.host.plugin.center.*"})
@MapperScan("cn.spider.node.host.plugin.center.model.mapper")
public class SpringConfig {
    @Bean
    public Vertx buildVertx() {
        return MainVerticle.clusterVertx;
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

    @Bean
    public EventBus buildEventBus(Vertx vertx) {
        return vertx.eventBus();
    }

    @Bean
    public TaskTimer buildTaskTimer(Vertx vertx, TaskManager taskManager) {
        return new TaskTimer(vertx, taskManager);
    }

    @Bean
    public HostApplicationOfflineHandler buildHostApplicationOfflineHandler(EventBus eventBus, HostApplicationManager applicationManager, Vertx vertx) {
        return new HostApplicationOfflineHandler(eventBus, applicationManager, vertx);
    }

    @Bean
    public HostApplicationOnlineHandler buildHostApplicationOnlineHandler(EventBus eventBus, HostApplicationManager applicationManager, Vertx vertx) {
        return new HostApplicationOnlineHandler(eventBus, applicationManager, vertx);
    }

    @Bean
    public WebClient buildWebClient(Vertx vertx){
        return WebClient.create(vertx);
    }

    @Bean
    public HostApplicationClient buildApplicationClient(WebClient webClient,Vertx vertx){
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");
        Integer hostApplicationPort = Integer.parseInt(localMap.get("host_application_port"));
        return new HostApplicationClient(webClient,hostApplicationPort);
    }
}
