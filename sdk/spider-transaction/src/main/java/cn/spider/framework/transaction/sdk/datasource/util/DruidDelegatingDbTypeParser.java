package cn.spider.framework.transaction.sdk.datasource.util;


import cn.spider.framework.transaction.sdk.loader.LoadLevel;
import cn.spider.framework.transaction.sdk.datasource.loader.DruidIsolationClassLoader;
import cn.spider.framework.transaction.sdk.sqlparser.SqlParserType;
import cn.spider.framework.transaction.sdk.sqlparser.util.DbTypeParser;

import java.lang.reflect.Constructor;

/**
 * @program: flow-cloud
 * @description:
 * @author: dds
 * @create: 2022-07-26 21:03
 */
@LoadLevel(name = SqlParserType.SQL_PARSER_TYPE_DRUID)
public class DruidDelegatingDbTypeParser implements DbTypeParser {
    private DbTypeParser dbTypeParserImpl;

    public DruidDelegatingDbTypeParser() {
        setClassLoader(DruidIsolationClassLoader.get());
    }

    /**
     * Only for unit test
     *
     * @param classLoader classLoader
     */
    void setClassLoader(ClassLoader classLoader) {
        try {
            Class<?> druidDbTypeParserImplClass = classLoader.loadClass("cn.spider.framework.transaction.sdk.sqlparser.druid.DruidDbTypeParserImpl");
            Constructor<?> implConstructor = druidDbTypeParserImplClass.getDeclaredConstructor();
            implConstructor.setAccessible(true);
            try {
                dbTypeParserImpl = (DbTypeParser) implConstructor.newInstance();
            } finally {
                implConstructor.setAccessible(false);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String parseFromJdbcUrl(String jdbcUrl) {
        return dbTypeParserImpl.parseFromJdbcUrl(jdbcUrl);
    }
}
