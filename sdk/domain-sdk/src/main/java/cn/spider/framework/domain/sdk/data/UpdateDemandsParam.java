package cn.spider.framework.domain.sdk.data;

import java.util.List;

public class UpdateDemandsParam {
    private String domainFunctionVersionId;

    private List<String> demands;

    public String getDomainFunctionVersionId() {
        return domainFunctionVersionId;
    }

    public void setDomainFunctionVersionId(String domainFunctionVersionId) {
        this.domainFunctionVersionId = domainFunctionVersionId;
    }

    public List<String> getDemands() {
        return demands;
    }

    public void setDemands(List<String> demands) {
        this.demands = demands;
    }
}
