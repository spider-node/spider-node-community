package cn.spider.framework.domain.area.function.data;

import lombok.Data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.function.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-15  21:35
 * @Description: 功能实体类
 * @Version: 1.0
 */
@Data
public class FunctionModel {

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 功能描述
     */
    private String desc;

    /**
     * 负责人
     */
    private String director;

    /**
     * 状态
     */
    private FunctionStatus status;

    /**
     * 功能id
     */
    private String id;

    /**
     * 域id
     */
    private String areaId;
}
