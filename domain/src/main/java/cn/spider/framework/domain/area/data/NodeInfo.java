package cn.spider.framework.domain.area.data;

import cn.spider.framework.domain.area.data.enums.FunctionNodeType;

public class NodeInfo {
    private String domainFunctionDesc;

    private String domainFunctionName;

    private String domainFunctionVersion;

    private String taskComponent;

    private String taskService;

    private String functionVersionId;

    private FunctionNodeType functionType;

    private String id;

    private String name;

    public String getDomainFunctionDesc() {
        return domainFunctionDesc;
    }

    public void setDomainFunctionDesc(String domainFunctionDesc) {
        this.domainFunctionDesc = domainFunctionDesc;
    }

    public String getDomainFunctionName() {
        return domainFunctionName;
    }

    public void setDomainFunctionName(String domainFunctionName) {
        this.domainFunctionName = domainFunctionName;
    }

    public String getDomainFunctionVersion() {
        return domainFunctionVersion;
    }

    public void setDomainFunctionVersion(String domainFunctionVersion) {
        this.domainFunctionVersion = domainFunctionVersion;
    }

    public String getTaskComponent() {
        return taskComponent;
    }

    public void setTaskComponent(String taskComponent) {
        this.taskComponent = taskComponent;
    }

    public String getTaskService() {
        return taskService;
    }

    public void setTaskService(String taskService) {
        this.taskService = taskService;
    }

    public String getFunctionVersionId() {
        return functionVersionId;
    }

    public void setFunctionVersionId(String functionVersionId) {
        this.functionVersionId = functionVersionId;
    }

    public FunctionNodeType getFunctionType() {
        return functionType;
    }

    public void setFunctionType(FunctionNodeType functionType) {
        this.functionType = functionType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
