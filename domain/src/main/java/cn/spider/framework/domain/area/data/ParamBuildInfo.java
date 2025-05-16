package cn.spider.framework.domain.area.data;

import com.alibaba.fastjson.JSONObject;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Set;

public class ParamBuildInfo {
    private List<AiNodeInfo> aiNodeInfoList;

    private  String bpmnString;

    private JSONObject flowData;

    private String flowDataDesc;

    private String functionVersionId;

    private List<String> businessFunctionParametersClass;

    private List<String> businessFunctionReturnsClass;

    private List<JsonObject> domainInfos;

    private String nodeId;

    private Set<String> paramJsDemand;

    public ParamBuildInfo(List<AiNodeInfo> aiNodeInfoList,
                          String bpmnString,
                          JSONObject flowData,
                          String functionVersionId,
                          String flowDataDesc,
                          List<String> businessFunctionParametersClass,
                          List<String> businessFunctionReturnsClass,List<JsonObject> domainInfos,String nodeId,Set<String> paramJsDemand) {
        this.aiNodeInfoList = aiNodeInfoList;
        this.bpmnString = bpmnString;
        this.flowData = flowData;
        this.functionVersionId = functionVersionId;
        this.flowDataDesc = flowDataDesc;
        this.businessFunctionParametersClass = businessFunctionParametersClass;
        this.businessFunctionReturnsClass = businessFunctionReturnsClass;
        this.nodeId = nodeId;
        this.paramJsDemand = paramJsDemand;
        this.domainInfos = domainInfos;
    }

    public List<AiNodeInfo> getAiNodeInfoList() {
        return aiNodeInfoList;
    }

    public void setAiNodeInfoList(List<AiNodeInfo> aiNodeInfoList) {
        this.aiNodeInfoList = aiNodeInfoList;
    }

    public String getBpmnString() {
        return bpmnString;
    }

    public void setBpmnString(String bpmnString) {
        this.bpmnString = bpmnString;
    }

    public JSONObject getFlowData() {
        return flowData;
    }

    public void setFlowData(JSONObject flowData) {
        this.flowData = flowData;
    }

    public String getFunctionVersionId() {
        return functionVersionId;
    }

    public void setFunctionVersionId(String functionVersionId) {
        this.functionVersionId = functionVersionId;
    }

    public String getFlowDataDesc() {
        return flowDataDesc;
    }

    public void setFlowDataDesc(String flowDataDesc) {
        this.flowDataDesc = flowDataDesc;
    }

    public List<String> getBusinessFunctionParametersClass() {
        return businessFunctionParametersClass;
    }

    public void setBusinessFunctionParametersClass(List<String> businessFunctionParametersClass) {
        this.businessFunctionParametersClass = businessFunctionParametersClass;
    }

    public List<String> getBusinessFunctionReturnsClass() {
        return businessFunctionReturnsClass;
    }

    public void setBusinessFunctionReturnsClass(List<String> businessFunctionReturnsClass) {
        this.businessFunctionReturnsClass = businessFunctionReturnsClass;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Set<String> getParamJsDemand() {
        return paramJsDemand;
    }

    public void setParamJsDemand(Set<String> paramJsDemand) {
        this.paramJsDemand = paramJsDemand;
    }

    public List<JsonObject> getDomainInfos() {
        return domainInfos;
    }

    public void setDomainInfos(List<JsonObject> domainInfos) {
        this.domainInfos = domainInfos;
    }
}
