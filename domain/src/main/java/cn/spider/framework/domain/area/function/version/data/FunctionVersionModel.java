package cn.spider.framework.domain.area.function.version.data;

import cn.spider.framework.domain.area.function.version.data.enums.VersionStatus;
import lombok.Data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.function.version.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-17  22:25
 * @Description: 功能版本模型
 * @Version: 1.0
 */
@Data
public class FunctionVersionModel {

    /**
     * id
     */
    private String id;

    /**
     * 功能id
     */
    private String functionId;

    /**
     * 版本
     */
    private String version;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 状态
     */
    private VersionStatus status;

    /**
     * bpmn-地址路径
     */
    private String bpmnUrl;

    /**
     * 起始id
     */
    private String startEventId;

    /**
     * bpmn名称
     */
    private String bpmnName;

    /**
     * 描述
     */
    private String desc;

    /**
     * 请求参数的启动类
     */
    private String reqParamClassType;

    /**
     * bpmn状态
     */
    private String bpmnStatus;

}
