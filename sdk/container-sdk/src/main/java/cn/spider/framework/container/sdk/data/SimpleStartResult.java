package cn.spider.framework.container.sdk.data;

import io.vertx.core.json.JsonObject;

public class SimpleStartResult {
    private Boolean runStatus;

    private String error;

    private JsonObject resultObject;

    public Boolean getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(Boolean runStatus) {
        this.runStatus = runStatus;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public JsonObject getResultObject() {
        return resultObject;
    }

    public void setResultObject(JsonObject resultObject) {
        this.resultObject = resultObject;
    }
}
