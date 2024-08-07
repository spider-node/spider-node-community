package cn.spider.framework.domain.area.node.data;

import cn.spider.framework.domain.area.node.data.enums.NodeStatus;
import cn.spider.framework.domain.area.node.data.enums.ServiceTaskType;
import io.vertx.core.json.JsonObject;
import lombok.Data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.node.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-09-04  23:01
 * @Description: 节点
 * @Version: 1.0
 */
@Data
public class Node {

    /**
     * 名称
     */
    private String name;

    /**
     * id
     */
    private String id;

    /**
     * 描述
     */
    private String desc;

    /**
     * task-组件
     */
    private String taskComponent;

    /**
     * task-service
     */
    private String taskService;

    /**
     * 是否为异步
     */
    private Boolean async;

    /**
     * 节点类型
     */
    private ServiceTaskType serviceTaskType;

    /**
     * 状态
     */
    private NodeStatus status;

    /**
     * 领域id
     */
    private String areaId;

    /**
     * 领域名称
     */
    private String areaName;

    /**
     * 入参
     */
    private JsonObject paramMapping;

    /**
     * 返回参数
     */
    private JsonObject resultMapping;

    /**
     * 调用远程服务的名称
     */
    private String taskMethod;

    /**
     * 服务标识
     */
    private String workerId;
}
