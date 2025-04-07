package cn.spider.framework.param.sdk.data;

import java.util.Map;
import java.util.Set;

public class QueryFunctionParam {

    private String requestId;

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

    public QueryFunctionParam(String requestId, String jsFunctionName, String jsFunctionCode, Set<String> jsFunctionParam, Set<String> jsFunctionParamReal) {
        this.requestId = requestId;
        this.jsFunctionName = jsFunctionName;
        this.jsFunctionCode = jsFunctionCode;
        this.jsFunctionParam = jsFunctionParam;
        this.jsFunctionParamReal = jsFunctionParamReal;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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
}
