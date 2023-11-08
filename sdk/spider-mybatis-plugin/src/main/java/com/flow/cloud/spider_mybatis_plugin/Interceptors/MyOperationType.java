package com.flow.cloud.spider_mybatis_plugin.Interceptors;

import net.sf.jsqlparser.schema.Column;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface MyOperationType {

    String handle(MappedStatement statement, List<ParameterMapping> parameterMappings, MapperMethod.ParamMap parameterObject, String sql);

    default boolean isNotIn(ParameterMapping parameterMapping, List<ParameterMapping> parameterMappings){
        if(parameterMappings!=null){
            for(ParameterMapping it:parameterMappings){
                if(it.getProperty().equals(parameterMapping.getProperty())){
                    return false;
                }
            }
        }
        return true;
    }

    default List<String> encList(String tableName,List<Column> columns){
        List<String> result = new ArrayList<>();
        List<String> list = getEncMap().get(tableName);
        if(list!=null&&columns!=null){
            for(Column col:columns){
                if(list.contains(col.getColumnName())){
                    result.add(col.getColumnName());
                }
            }
        }
        return result;
    }

    default String getColumnValue(MapperMethod.ParamMap parameterObject,List<ParameterMapping> parameterMappings,List<Column> columns,String column){
        if(columns!=null){
            for(int i=0;i<columns.size();i++){
                Column col = columns.get(i);
                if(column.equals(col.getColumnName())){
                    ParameterMapping parameterMapping = parameterMappings.get(i);
                    String property = parameterMapping.getProperty();
                    Object result = parameterObject.get(property);
                    return (String)result;
                }
            }
        }
        return null;
    }
    Map<String,List<String>> getEncMap();
}
