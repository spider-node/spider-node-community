package cn.spider.framework.domain.area.plugin.data;

import lombok.Data;

@Data
public class InitAreaBaseParam {
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
}
