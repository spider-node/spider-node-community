package cn.spider.framework.domain.area.sondomain.entity;

import java.util.Set;

public class QuerySonAreaInfoParam {

    private Integer id;

    private Set<Integer> ids;
    /**
     * 子域的名称
     */
    private String sonAreaName;

    private String areaName;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 数据源
     */
    private String datasource;

    private String areaId;

    private Set<String> areaIds;

    private Long page;

    private Long size;

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getSonAreaName() {
        return sonAreaName;
    }

    public void setSonAreaName(String sonAreaName) {
        this.sonAreaName = sonAreaName;
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

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Set<Integer> getIds() {
        return ids;
    }

    public void setIds(Set<Integer> ids) {
        this.ids = ids;
    }

    public Set<String> getAreaIds() {
        return areaIds;
    }

    public void setAreaIds(Set<String> areaIds) {
        this.areaIds = areaIds;
    }
}
