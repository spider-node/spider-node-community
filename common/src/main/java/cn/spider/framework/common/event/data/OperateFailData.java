package cn.spider.framework.common.event.data;

public class OperateFailData {
    private String functionVersionId;

    private String error;

    public OperateFailData(String functionVersionId, String error) {
        this.functionVersionId = functionVersionId;
        this.error = error;
    }

    public OperateFailData() {
    }

    public String getFunctionVersionId() {
        return functionVersionId;
    }

    public void setFunctionVersionId(String functionVersionId) {
        this.functionVersionId = functionVersionId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
