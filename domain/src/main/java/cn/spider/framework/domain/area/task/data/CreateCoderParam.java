package cn.spider.framework.domain.area.task.data;

import com.alibaba.fastjson.JSONObject;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Set;

public class CreateCoderParam {
    // 项目名称 传入功能的名称+版本
    private String projectName;

    // 本领域的基础信息
    private JsonObject sonDomainInfo;

    // 数据源id
    // 需求
    private List<String> businessRequirements;

    private Integer taskId;

    /**
     * 领域功能版本的id
     */
    private Set<Integer> baseInfoIds;

    /**
     * 领域功能版本的id
     */
    private String domainFunctionVersionId;

    private JSONObject dataFlow;
    // 数据流描述
    private String dataFlowAnalysis;

    private String domainInfoAnalysis;

    private Boolean needDataFlow;

    private String inputParam;

    private String outParam;

    // 采用定制参数
    private Boolean customizedParam;

    private String datasource;

    private String serviceName;

    private Integer aiTaskId;

    public CreateCoderParam(String projectName, JsonObject sonDomainInfo, List<String> businessRequirements) {
        this.projectName = projectName;
        this.sonDomainInfo = sonDomainInfo;
        this.businessRequirements = businessRequirements;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public JsonObject getSonDomainInfo() {
        return sonDomainInfo;
    }

    public void setSonDomainInfo(JsonObject sonDomainInfo) {
        this.sonDomainInfo = sonDomainInfo;
    }

    public List<String> getBusinessRequirements() {
        return businessRequirements;
    }

    public void setBusinessRequirements(List<String> businessRequirements) {
        this.businessRequirements = businessRequirements;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Set<Integer> getBaseInfoIds() {
        return baseInfoIds;
    }

    public void setBaseInfoIds(Set<Integer> baseInfoIds) {
        this.baseInfoIds = baseInfoIds;
    }

    public String getDomainFunctionVersionId() {
        return domainFunctionVersionId;
    }

    public void setDomainFunctionVersionId(String domainFunctionVersionId) {
        this.domainFunctionVersionId = domainFunctionVersionId;
    }

    public JSONObject getDataFlow() {
        return dataFlow;
    }

    public void setDataFlow(JSONObject dataFlow) {
        this.dataFlow = dataFlow;
    }

    public Boolean getNeedDataFlow() {
        return needDataFlow;
    }

    public void setNeedDataFlow(Boolean needDataFlow) {
        this.needDataFlow = needDataFlow;
    }

    public String getDataFlowAnalysis() {
        return dataFlowAnalysis;
    }

    public void setDataFlowAnalysis(String dataFlowAnalysis) {
        this.dataFlowAnalysis = dataFlowAnalysis;
    }

    public String getDomainInfoAnalysis() {
        return domainInfoAnalysis;
    }

    public void setDomainInfoAnalysis(String domainInfoAnalysis) {
        this.domainInfoAnalysis = domainInfoAnalysis;
    }

    public String getInputParam() {
        return inputParam;
    }

    public void setInputParam(String inputParam) {
        this.inputParam = inputParam;
    }

    public String getOutParam() {
        return outParam;
    }

    public void setOutParam(String outParam) {
        this.outParam = outParam;
    }

    public Boolean getCustomizedParam() {
        return customizedParam;
    }

    public void setCustomizedParam(Boolean customizedParam) {
        this.customizedParam = customizedParam;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Integer getAiTaskId() {
        return aiTaskId;
    }

    public void setAiTaskId(Integer aiTaskId) {
        this.aiTaskId = aiTaskId;
    }
}
