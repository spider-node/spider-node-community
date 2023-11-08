package cn.spider.framework.db.config;

import cn.spider.framework.db.rocksdb.RocksdbKeyManager;
import cn.spider.framework.db.util.RocksdbUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.db.config
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-15  13:37
 * @Description: TODO
 * @Version: 1.0
 */
@Configuration
public class DbRocksConfig {
    @Bean
    public RocksdbUtil buildRocksdbUtil() {
        return RocksdbUtil.getInstance();
    }

    @Bean
    public RocksdbKeyManager buildRocksdbKeyManager(){
        return new RocksdbKeyManager();
    }

}
