package cn.spider.framework.domain.area.sondomain.entity;

import cn.spider.framework.domain.area.util.TableInfo;

import java.util.List;

public class QuerySonAreaBaseModel {
    private Integer id;

    private String tableName;

    private String datasource;

    private String version;

    private List<TableInfo> tableInfo;

    public QuerySonAreaBaseModel(Integer id, String tableName, String datasource, String version, List<TableInfo> tableInfo) {
        this.id = id;
        this.tableName = tableName;
        this.datasource = datasource;
        this.version = version;
        this.tableInfo = tableInfo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<TableInfo> getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(List<TableInfo> tableInfo) {
        this.tableInfo = tableInfo;
    }
}
