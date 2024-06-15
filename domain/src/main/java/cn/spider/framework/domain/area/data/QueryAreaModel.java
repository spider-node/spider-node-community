package cn.spider.framework.domain.area.data;

import lombok.Data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-19  21:51
 * @Description: 查询域
 * @Version: 1.0
 */
@Data
public class QueryAreaModel {

    /**
     * 域名称
     */
    private String areaName;

    private String id;

    private String classPath;

    private String sdkUrl;

    private Integer page;

    private Integer size;
}
