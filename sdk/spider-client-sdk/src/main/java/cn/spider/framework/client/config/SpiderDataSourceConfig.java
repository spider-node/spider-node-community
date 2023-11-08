package cn.spider.framework.client.config;

import cn.spider.framework.transaction.sdk.datasource.DataSourceProxy;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

/**
 * @program: spider-datasource配置
 * @description: 数据源datasource
 * @author: dds
 * @create: 2022-07-26 18:40
 */

public class SpiderDataSourceConfig {

    @Value("${spider.dataSource.url}")
    private String url;
    @Value("${spider.dataSource.userName}")
    private String userName;
    @Value("${spider.dataSource.password}")
    private String password;
    @Value("${spider.dataSource.maxActive}")
    private int maxActive;
    @Value("${spider.dataSource.initialSize}")
    private int initialSize;

    @Bean
    public DataSource dataSource(){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setValidationQuery("SELECT 1");//用来检测连接是否有效
        dataSource.setTestOnBorrow(false);//借用连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        dataSource.setTestOnReturn(false);//归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        dataSource.setTestWhileIdle(true);//如果检测失败，则连接将被从池中去除
        dataSource.setTimeBetweenEvictionRunsMillis(60000);//1分钟
        dataSource.setMaxActive(maxActive);
        dataSource.setInitialSize(initialSize);
        return new DataSourceProxy(dataSource);
    }

}
