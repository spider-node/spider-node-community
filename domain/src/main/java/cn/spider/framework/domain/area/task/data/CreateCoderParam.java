package cn.spider.framework.domain.area.task.data;

import io.vertx.core.json.JsonObject;

import java.util.List;

public class CreateCoderParam {
    // 项目名称 传入功能的名称+版本
    private String projectName;

    // 本领域的基础信息
    private JsonObject sonDomainInfo;

    // 数据源id
    private String datasource;
    // 需求
    private List<String> businessRequirements;

    private Integer taskId;

    /**
     * 领域功能版本的id
     */
    private Integer baseInfoId;

    /**
     * 领域功能版本的id
     */
    private String domainFunctionVersionId;


    public CreateCoderParam(String projectName, JsonObject sonDomainInfo, String datasourceId, List<String> businessRequirements) {
        this.projectName = projectName;
        this.sonDomainInfo = sonDomainInfo;
        this.datasource = datasourceId;
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

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasourceId) {
        this.datasource = datasourceId;
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

    public Integer getBaseInfoId() {
        return baseInfoId;
    }

    public void setBaseInfoId(Integer baseInfoId) {
        this.baseInfoId = baseInfoId;
    }

    public String getDomainFunctionVersionId() {
        return domainFunctionVersionId;
    }

    public void setDomainFunctionVersionId(String domainFunctionVersionId) {
        this.domainFunctionVersionId = domainFunctionVersionId;
    }
}
