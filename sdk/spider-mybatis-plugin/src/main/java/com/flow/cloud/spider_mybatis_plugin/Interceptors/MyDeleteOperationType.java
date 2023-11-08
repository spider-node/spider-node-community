package com.flow.cloud.spider_mybatis_plugin.Interceptors;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;

import java.util.List;

/**
 * @program: transactiona-test
 * @description:
 * @author: dds
 * @create: 2022-08-04 16:04
 */
public class MyDeleteOperationType extends MyDefaultOperationType {
    @Override
    public String handle(MappedStatement mystatement, List<ParameterMapping> parameterMappings, MapperMethod.ParamMap parameterObject, String sql) {
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            Delete delete = (Delete) statement;
            Table table = delete.getTable();

            Expression where = delete.getWhere();
            EqualsTo status = new EqualsTo();
            status.setLeftExpression(new Column(table, "commit_status"));
            StringValue stringValue = new StringValue("1");
            status.setRightExpression(stringValue);
            if(where!=null) {
                AndExpression lastwhere = new AndExpression(where, status);
                delete.setWhere(lastwhere);
            }else{
                delete.setWhere(status);
            }

            return delete.toString();
        }catch (Exception e){
            throw new RuntimeException("解析sql异常",e);
        }
    }
}
