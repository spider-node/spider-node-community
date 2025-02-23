package cn.spider.node.host.plugin.center.sdk.data;

public class QueryFunctionVersionsParam {

    private String domainFunctionVersionId;

    public QueryFunctionVersionsParam(String domainFunctionVersionId) {
        this.domainFunctionVersionId = domainFunctionVersionId;
    }

    public QueryFunctionVersionsParam() {
    }

    public String getDomainFunctionVersionId() {
        return domainFunctionVersionId;
    }

    public void setDomainFunctionVersionId(String domainFunctionVersionId) {
        this.domainFunctionVersionId = domainFunctionVersionId;
    }
}
