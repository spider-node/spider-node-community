package cn.spider.framework.param.sdk.data;

import io.vertx.core.json.JsonObject;


public class QueryRequestResult {
    /**
     * 入参
     */
    private JsonObject runParam;

    /**
     * 调用远程服务的方法名称
     */
    private String taskMethod;

    /**
     * 服务id
     */
    private String workerId;

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public JsonObject getRunParam() {
        return runParam;
    }

    public void setRunParam(JsonObject runParam) {
        this.runParam = runParam;
    }

    public String getTaskMethod() {
        return taskMethod;
    }

    public void setTaskMethod(String taskMethod) {
        this.taskMethod = taskMethod;
    }
}
