package cn.spider.framework.flow.business.data;

public class QueryBusinessVersionParam {
    private String functionId;

    private String functionVersionId;

    public QueryBusinessVersionParam(String functionId, String functionVersionId) {
        this.functionId = functionId;
        this.functionVersionId = functionVersionId;
    }

    public QueryBusinessVersionParam() {
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public String getFunctionVersionId() {
        return functionVersionId;
    }

    public void setFunctionVersionId(String functionVersionId) {
        this.functionVersionId = functionVersionId;
    }
}
