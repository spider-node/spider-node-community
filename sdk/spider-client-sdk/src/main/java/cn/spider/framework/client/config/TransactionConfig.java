package cn.spider.framework.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.linker.client.config
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-09  19:40
 * @Description: 事务配置
 * @Version: 1.0
 */
public class TransactionConfig {
    @Bean
    public TransactionDefinition transactionDefinition(){
        return new DefaultTransactionDefinition();
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource){
        PlatformTransactionManager platformTransactionManager = new DataSourceTransactionManager(dataSource);
        return platformTransactionManager;
    }
}
