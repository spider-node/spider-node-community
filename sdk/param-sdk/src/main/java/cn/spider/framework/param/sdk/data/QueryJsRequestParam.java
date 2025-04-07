package cn.spider.framework.param.sdk.data;

import java.util.Set;

public class QueryJsRequestParam {
    /**
     * js 函数名称
     */
    private String jsFunctionName;

    /**
     * js 函数代码
     */
    private String jsFunctionCode;

    /**
     * js 函数参数
     */
    private Set<String> jsFunctionParam;

    /**
     * js 函数参数真实值
     */
    private Set<String> jsFunctionParamReal;

    private String requestId;

    /**
     * 节点id
     */
    private String nodeId;

    public QueryJsRequestParam(String jsFunctionName, String jsFunctionCode, Set<String> jsFunctionParam, Set<String> jsFunctionParamReal, String nodeId,String requestId) {
        this.jsFunctionName = jsFunctionName;
        this.jsFunctionCode = jsFunctionCode;
        this.jsFunctionParam = jsFunctionParam;
        this.jsFunctionParamReal = jsFunctionParamReal;
        this.nodeId = nodeId;
        this.requestId = requestId;
    }

    public QueryJsRequestParam() {
    }

    public String getJsFunctionName() {
        return jsFunctionName;
    }

    public void setJsFunctionName(String jsFunctionName) {
        this.jsFunctionName = jsFunctionName;
    }

    public String getJsFunctionCode() {
        return jsFunctionCode;
    }

    public void setJsFunctionCode(String jsFunctionCode) {
        this.jsFunctionCode = jsFunctionCode;
    }

    public Set<String> getJsFunctionParam() {
        return jsFunctionParam;
    }

    public void setJsFunctionParam(Set<String> jsFunctionParam) {
        this.jsFunctionParam = jsFunctionParam;
    }

    public Set<String> getJsFunctionParamReal() {
        return jsFunctionParamReal;
    }

    public void setJsFunctionParamReal(Set<String> jsFunctionParamReal) {
        this.jsFunctionParamReal = jsFunctionParamReal;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
