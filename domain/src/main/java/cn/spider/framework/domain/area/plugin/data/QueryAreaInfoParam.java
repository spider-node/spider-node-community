package cn.spider.framework.domain.area.plugin.data;

import lombok.Data;

@Data
public class QueryAreaInfoParam {
    /**
     * 子域名称
     */
    private String sonAreaName;

    /**
     * 父域名称
     */
    private String AreaName;

    /**
     * 数据源
     */
    private String datasource;

    /**
     * 页数
     */
    private Integer page;

    /**
     * 大小
     */
    private Integer size;
}
