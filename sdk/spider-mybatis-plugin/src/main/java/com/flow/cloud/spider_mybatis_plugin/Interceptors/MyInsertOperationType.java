package com.flow.cloud.spider_mybatis_plugin.Interceptors;

import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;

import java.util.List;

/**
 * @program: transactiona-test
 * @description:
 * @author: dds
 * @create: 2022-08-04 15:59
 */
public class MyInsertOperationType extends MyDefaultOperationType{
    @Override
    public String handle(MappedStatement mystatement, List<ParameterMapping> parameterMappings, MapperMethod.ParamMap parameterObject, String sql) {
        try {
            //解析sql语句
            Statement statement = CCJSqlParserUtil.parse(sql);
            //强制转换成insert对象
            Insert insert = (Insert) statement;
            //从insert中获取字段名
            List<Column> columns = insert.getColumns();
            ExpressionList list = (ExpressionList) insert.getItemsList();
            Column miwen = new Column("commit_status");
            columns.add(miwen);
            // 新增一个占位符
            list.getExpressions().add(new JdbcParameter());

            ParameterMapping parameterMapping = new ParameterMapping.Builder(mystatement.getConfiguration(),"commit_status",String.class).build();
            if(isNotIn(parameterMapping,parameterMappings)) {
                parameterMappings.add(parameterMapping);
            }
            parameterObject.put("commit_status", "1");
            insert.setItemsList(list);
            System.out.println("修改后的sql语句是"+insert.toString());
            return insert.toString();
        }catch (Exception e){
            throw new RuntimeException("解析sql异常",e);
        }
    }
}
