package cn.spider.framework.domain.sdk.data;

import java.io.Serializable;

public class DataFlowAnalysisModel implements Serializable {
    private Integer flowDataId;

    private String domainInfoResult;

    private String flowDataResult;

    public Integer getFlowDataId() {
        return flowDataId;
    }

    public void setFlowDataId(Integer flowDataId) {
        this.flowDataId = flowDataId;
    }

    public String getDomainInfoResult() {
        return domainInfoResult;
    }

    public void setDomainInfoResult(String domainInfoResult) {
        this.domainInfoResult = domainInfoResult;
    }

    public String getFlowDataResult() {
        return flowDataResult;
    }

    public void setFlowDataResult(String flowDataResult) {
        this.flowDataResult = flowDataResult;
    }
}
