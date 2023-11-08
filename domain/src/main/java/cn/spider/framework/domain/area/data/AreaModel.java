package cn.spider.framework.domain.area.data;

import cn.spider.framework.domain.area.data.enums.SdkStatus;
import lombok.Data;
import sun.dc.pr.PRError;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-18  23:40
 * @Description: 域模型
 * @Version: 1.0
 */
@Data
public class AreaModel {
    /**
     * 领域id
     */
    private String id;

    /**
     * 领域名称
     */
    private String areaName;

    /**
     * 领域描述
     */
    private String desc;

    /**
     * 领域sdk的url
     */
    private String sdkUrl;

    /**
     * sdk名称
     */
    private String sdkName;

    /**
     * 扫描包的路径
     */
    private String scanClassPath;

    /**
     * sdk-的status
     */
    private SdkStatus sdkStatus;

}
