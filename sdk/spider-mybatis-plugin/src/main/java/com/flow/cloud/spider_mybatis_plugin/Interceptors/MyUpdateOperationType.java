package com.flow.cloud.spider_mybatis_plugin.Interceptors;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;

import java.util.List;

/**
 * @program: transactiona-test
 * @description: 修改默认加条件，当修改时，commit_status 必须要大于1
 * @author: dds
 * @create: 2022-08-04 16:02
 */
public class MyUpdateOperationType extends MyDefaultOperationType {
    @Override
    public String handle(MappedStatement mystatement, List<ParameterMapping> parameterMappings, MapperMethod.ParamMap parameterObject, String sql) {
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            Update update = (Update)statement;
            Table table = update.getTable();
            Expression where = update.getWhere();
            EqualsTo status = new EqualsTo();
            status.setLeftExpression(new Column(table, "commit_status"));
            StringValue stringValue = new StringValue("1");
            status.setRightExpression(stringValue);
            if(where!=null) {
                AndExpression lastwhere = new AndExpression(where, status);
                update.setWhere(lastwhere);
            }else{
                update.setWhere(status);
            }
            System.out.println("修改后的sql语句是"+update.toString());

            return update.toString();
        }catch (Exception e){
            throw new RuntimeException("解析sql异常",e);
        }
    }
}