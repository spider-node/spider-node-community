package cn.spider.framework.domain.area.function.data;

import lombok.Data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.function.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-17  22:08
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class QueryFunctionParam {
    /**
     * 页数
     */
    private Integer page;

    /**
     * size
     */
    private Integer size;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 状态
     */
    private String status;

    /**
     * 域id
     */
    private String areaId;
}
