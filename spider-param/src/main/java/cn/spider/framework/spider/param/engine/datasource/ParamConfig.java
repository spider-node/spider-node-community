package cn.spider.framework.spider.param.engine.datasource;

import cn.spider.framework.spider.param.engine.enums.Type;

import java.util.Map;

public class ParamConfig {
    private String table;

    private String version;

    private Map<String,String> fields;

    private Type type;

    private String sourceFiled;

    private String unique;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getSourceFiled() {
        return sourceFiled;
    }

    public void setSourceFiled(String sourceFiled) {
        this.sourceFiled = sourceFiled;
    }

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }
}
