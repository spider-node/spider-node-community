package cn.spider.framework.spider.param.data;
import io.vertx.core.json.JsonObject;
import lombok.Data;

@Data
public class NodeParamMapping {
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
     * task-组件
     */
    private String taskComponent;

    /**
     * task-service
     */
    private String taskService;

    /**
     * 工作服务的id
     */
    private String workerId;
}
