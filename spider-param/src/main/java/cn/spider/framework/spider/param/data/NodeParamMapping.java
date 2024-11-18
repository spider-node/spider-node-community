package cn.spider.framework.spider.param.data;
import cn.spider.framework.domain.sdk.data.ParamPack;
import io.vertx.core.json.JsonObject;
import lombok.Data;

@Data
public class NodeParamMapping {
    /**
     * 返回的字段信息
     */
    private ParamPack resultMapping;

    /**
     * 执行参数
     */
    private ParamPack runMapping;

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
