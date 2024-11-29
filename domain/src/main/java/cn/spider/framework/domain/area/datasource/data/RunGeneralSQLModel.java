package cn.spider.framework.domain.area.datasource.data;

import java.util.Map;

public class RunGeneralSQLModel {
    private String datasource;

    private String sql;

    private Map<String,Object> params;

    public RunGeneralSQLModel(String datasource, String sql, Map<String, Object> params) {
        this.datasource = datasource;
        this.sql = sql;
        this.params = params;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
