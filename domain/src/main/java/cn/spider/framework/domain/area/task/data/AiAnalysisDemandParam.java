package cn.spider.framework.domain.area.task.data;

import java.util.List;
import java.util.Map;

public class AiAnalysisDemandParam {

    private Map<String, Object> dataFlowInfo;

    private List<String> originalDemands;

    private String domainFunctionVersionId;

    public AiAnalysisDemandParam(Map<String, Object> dataFlowInfo, List<String> originalDemands, String domainFunctionVersionId) {
        this.originalDemands = originalDemands;
        this.domainFunctionVersionId = domainFunctionVersionId;
        this.dataFlowInfo = dataFlowInfo;
    }

    public List<String> getOriginalDemands() {
        return originalDemands;
    }

    public void setOriginalDemands(List<String> originalDemands) {
        this.originalDemands = originalDemands;
    }

    public String getDomainFunctionVersionId() {
        return domainFunctionVersionId;
    }

    public void setDomainFunctionVersionId(String domainFunctionVersionId) {
        this.domainFunctionVersionId = domainFunctionVersionId;
    }

    public Map<String, Object> getDataFlowInfo() {
        return dataFlowInfo;
    }
}
