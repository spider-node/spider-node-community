package cn.spider.framework.domain.area.node.data;

import cn.spider.framework.domain.area.node.data.enums.NodeStatus;
import cn.spider.framework.domain.area.node.data.enums.ServiceTaskType;
import com.alibaba.fastjson.JSONObject;
import io.vertx.core.json.JsonObject;
import lombok.Data;

@Data
public class CreateNodeModel {
    /**
     * 名称
     */
    private String name;
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
     * 调用远程服务的名称
     */
    private String taskMethod;

    /**
     * 服务标识
     */
    private String workerId;

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
     * 方法参数
     */
    private JSONObject methodParam;
}
