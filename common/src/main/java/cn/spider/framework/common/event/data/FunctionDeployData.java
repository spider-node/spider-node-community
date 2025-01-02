package cn.spider.framework.common.event.data;

public class FunctionDeployData extends EventData {
    private String url;

    private String functionVersionId;

    public FunctionDeployData(String url, String functionVersionId) {
        this.url = url;
        this.functionVersionId = functionVersionId;
    }

    public FunctionDeployData() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFunctionVersionId() {
        return functionVersionId;
    }

    public void setFunctionVersionId(String functionVersionId) {
        this.functionVersionId = functionVersionId;
    }
}
