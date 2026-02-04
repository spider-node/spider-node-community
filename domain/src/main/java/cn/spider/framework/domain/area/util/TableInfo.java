package cn.spider.framework.domain.area.util;

public class TableInfo {

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表描述
     */
    private String desc;

    /**
     * 数据源
     */
    private String datasource;

    public TableInfo(String tableName, String tableDesc) {
        this.tableName = tableName;
        this.desc = tableDesc;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }
}
