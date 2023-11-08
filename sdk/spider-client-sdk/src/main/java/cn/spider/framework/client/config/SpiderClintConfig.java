package cn.spider.framework.client.config;

import cn.spider.framework.client.transaction.SpiderTransactionOperation;
import cn.spider.framework.client.transaction.TransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.client.config
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-27  15:35
 * @Description: TODO
 * @Version: 1.0
 */
public class SpiderClintConfig {
    @Bean
    public SpiderTransactionOperation buildSpiderTransactionOperation(){
        return new SpiderTransactionOperation();
    }

    @Bean("spiderTransactionManager")
    public TransactionManager buildTransactionManager(@Value("${spider.dataSource.url}") String url, SpiderTransactionOperation operation){
        return new TransactionManager(url,operation);
    }
}
