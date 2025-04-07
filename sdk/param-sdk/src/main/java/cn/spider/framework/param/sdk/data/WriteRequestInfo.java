package cn.spider.framework.param.sdk.data;

import io.vertx.core.json.JsonObject;

public class WriteRequestInfo {
    private String requestId;

    private Object request;

    public WriteRequestInfo(String requestId, Object request) {
        this.requestId = requestId;
        this.request = request;
    }

    public WriteRequestInfo() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = request;
    }
}
