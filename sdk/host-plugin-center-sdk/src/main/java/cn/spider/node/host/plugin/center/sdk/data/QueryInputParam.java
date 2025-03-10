package cn.spider.node.host.plugin.center.sdk.data;

import java.util.Set;

public class QueryInputParam {
    private Set<String> domainFunctionVersionId;

    public Set<String> getDomainFunctionVersionId() {
        return domainFunctionVersionId;
    }

    public void setDomainFunctionVersionId(Set<String> domainFunctionVersionId) {
        this.domainFunctionVersionId = domainFunctionVersionId;
    }

    public QueryInputParam(Set<String> domainFunctionVersionId) {
        this.domainFunctionVersionId = domainFunctionVersionId;
    }

    public QueryInputParam() {
    }
}
