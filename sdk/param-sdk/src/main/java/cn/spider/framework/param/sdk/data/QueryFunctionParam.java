package cn.spider.framework.param.sdk.data;

import java.util.Map;

public class QueryFunctionParam {
    private Map<String,String> params;

    private String requestId;

    public QueryFunctionParam(Map<String, String> params, String requestId) {
        this.params = params;
        this.requestId = requestId;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
