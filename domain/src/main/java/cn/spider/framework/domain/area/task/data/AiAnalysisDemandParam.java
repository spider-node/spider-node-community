package cn.spider.framework.domain.area.task.data;

import java.util.List;

public class AiAnalysisDemandParam {
    private String dataFlowAnalysis;

    private List<String> originalDemands;

    private String domainFunctionVersionId;

    public AiAnalysisDemandParam(String dataFlowAnalysis, List<String> originalDemands, String domainFunctionVersionId) {
        this.dataFlowAnalysis = dataFlowAnalysis;
        this.originalDemands = originalDemands;
        this.domainFunctionVersionId = domainFunctionVersionId;
    }

    public String getDataFlowAnalysis() {
        return dataFlowAnalysis;
    }

    public void setDataFlowAnalysis(String dataFlowAnalysis) {
        this.dataFlowAnalysis = dataFlowAnalysis;
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
}
