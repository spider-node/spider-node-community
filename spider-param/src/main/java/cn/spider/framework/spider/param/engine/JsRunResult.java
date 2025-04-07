package cn.spider.framework.spider.param.engine;

import cn.spider.framework.spider.param.engine.enums.JsRunTimeErrorType;

public class JsRunResult {
    private Boolean runStatus;

    private Object result;

    private String errorMsg;

    private String functionName;

    private String nodeId;

    /**
     * 异常类型
     */
    private JsRunTimeErrorType jsRunTimeErrorType;

    public JsRunResult(Boolean runStatus, Object result, String errorMsg,String functionName) {
        this.runStatus = runStatus;
        this.result = result;
        this.errorMsg = errorMsg;
        this.functionName = functionName;
    }

    public JsRunResult() {
    }

    public Boolean getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(Boolean runStatus) {
        this.runStatus = runStatus;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public JsRunTimeErrorType getJsRunTimeErrorType() {
        return jsRunTimeErrorType;
    }

    public void setJsRunTimeErrorType(JsRunTimeErrorType jsRunTimeErrorType) {
        this.jsRunTimeErrorType = jsRunTimeErrorType;
    }
}
