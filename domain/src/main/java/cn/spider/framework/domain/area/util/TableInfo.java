package cn.spider.framework.domain.area.util;

import sun.dc.pr.PRError;

public class TableInfo {

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表描述
     */
    private String name;

    /**
     * 数据源
     */
    private String datasource;

    public TableInfo(String tableName, String tableDesc) {
        this.tableName = tableName;
        this.name = tableDesc;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }
}
