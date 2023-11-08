package com.flow.cloud.spider_mybatis_plugin.Interceptors;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * @program: transactiona-test
 * @description: mybatis的扩展
 * @author: dds
 * @create: 2022-08-07 02:01
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
        @Signature(type = Executor.class, method = "query", args={MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class MyBatisInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 拦截sql
        Object[] args = invocation.getArgs();
        MappedStatement statement = (MappedStatement) args[0];

        Object parameterObject = args[1];
        BoundSql boundSql = statement.getBoundSql(parameterObject);
        String sql = boundSql.getSql();
        if (sql == null || "".equals(sql)) {
            return invocation.proceed();
        }
        // 重写sql
        resetSql2Invocation(invocation, sql);
        return invocation.proceed();

    }

    private void resetSql2Invocation(Invocation invocation, String sql) throws SQLException, IllegalAccessException {
        final Object[] args = invocation.getArgs();
        MappedStatement statement = (MappedStatement) args[0];

        // 该参数类型 org.apache.ibatis.binding.MapperMethod$ParamMap
        Object parameterObject = args[1];
        // 获取传递的参数， 先吧参数转成  MapperMethod.ParamMap
        Map<String, Object> objectMap = getObjectToMap(parameterObject);
        //进行强转到数据  MapperMethod.ParamMap
        MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) objectMap;
        final BoundSql boundSql = statement.getBoundSql(parameterObject);
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        //根据不同的sql类型重新构建新的sql语句
        String newsql = "";
        MyOperationType myOperationType = null;
        switch (statement.getSqlCommandType()) {
            case INSERT:
                // 后面改造成单例模式
                myOperationType = new MyInsertOperationType();
                newsql = myOperationType.handle(statement, parameterMappings, paramMap, sql);
                args[1] = paramMap;
                break;
            case UPDATE:
                myOperationType = new MyUpdateOperationType();
                newsql = myOperationType.handle(statement, parameterMappings, paramMap, sql);
                break;
            case DELETE:
                myOperationType = new MyDeleteOperationType();
                newsql = myOperationType.handle(statement, parameterMappings, paramMap, sql);
                break;
            case SELECT:
                myOperationType = new MySelectOperationType();
                newsql = myOperationType.handle(statement, parameterMappings, paramMap, sql);
                break;
            default:
                break;
        }

        // 重新new一个查询语句对像
        BoundSql newBoundSql = new BoundSql(statement.getConfiguration(), newsql, parameterMappings,
                parameterObject);

        //

        // 把新的查询放到statement里
        MappedStatement newStatement = copyFromMappedStatement(statement, new BoundSqlSqlSource(newBoundSql));

        // 重新设置新的参数
        args[0] = newStatement;
        // 注意，这块因为上面进行了转换，对参数做了修改，需要新增部分内容

        System.out.println("sql语句：" + newsql);
    }

    //构造新的statement
    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource,
                ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // TODO Auto-generated method stub

    }

    class BoundSqlSqlSource implements SqlSource {
        private BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

    public Map<String, Object> getObjectToMap(Object obj) throws IllegalAccessException {

        Map<String, Object> map = new MapperMethod.ParamMap();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(obj);
            if (value == null) {
                value = "";
            }
            map.put(fieldName, value);
        }
        return map;
    }
}