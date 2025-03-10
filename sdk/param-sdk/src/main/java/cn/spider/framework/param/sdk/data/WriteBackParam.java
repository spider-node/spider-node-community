package cn.spider.framework.param.sdk.data;

import cn.spider.framework.param.sdk.data.enums.FunctionType;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;


public class WriteBackParam {
    /**
     * 请求id
     */
    private String requestId;

    /**
     * task-组件
     */
    private String taskComponent;

    /**
     * task-service
     */
    private String taskService;

    private String version;

    // 节点id
    private String functionVersionId;
    // 节点类型

    private FunctionType functionType;

    /**
     * 返回的参数
     */
    private JsonObject result;

    private BigDecimal costTime;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTaskComponent() {
        return taskComponent;
    }

    public void setTaskComponent(String taskComponent) {
        this.taskComponent = taskComponent;
    }

    public String getTaskService() {
        return taskService;
    }

    public void setTaskService(String taskService) {
        this.taskService = taskService;
    }

    public JsonObject getResult() {
        return result;
    }

    public void setResult(JsonObject result) {
        this.result = result;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFunctionVersionId() {
        return functionVersionId;
    }

    public void setFunctionVersionId(String functionVersionId) {
        this.functionVersionId = functionVersionId;
    }

    public FunctionType getFunctionType() {
        return functionType;
    }

    public void setFunctionType(FunctionType functionType) {
        this.functionType = functionType;
    }
}
