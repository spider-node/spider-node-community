package cn.spider.framework.domain.area.plugin.data;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 需要提前删除数据
 */
@Data
public class SpiderSonArea {

    private int id;

    /**
     * 领域id
     */
    private String areaId;

    /**
     * 领域名称
     */
    private String areaName;

    /**
     * 子域名称
     */
    private String sonAreaName;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 数据源
     */
    private String datasource;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
