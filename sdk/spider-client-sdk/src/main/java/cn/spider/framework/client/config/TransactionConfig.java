package cn.spider.framework.client.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

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
    public TransactionDefinition transactionDefinition() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("spider-transaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return def;
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        PlatformTransactionManager platformTransactionManager = new DataSourceTransactionManager(dataSource);
        return platformTransactionManager;
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
}
