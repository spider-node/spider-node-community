package cn.spider.framework.domain.area.task.data;

import java.util.List;

public class StartTestCaseParam {
    private List<MethodInputModel> methodInputModes;

    private List<CaseSqlModel> caseSql;

    /**
     * 本次任务的id
     */
    private Integer taskId;

    /**
     * 领域功能版本id 用于校验，本版本是否进行了校验
     */
    private String domainFunctionVersionId;

    private String datasource;

    public List<MethodInputModel> getMethodInputModes() {
        return methodInputModes;
    }

    public void setMethodInputModes(List<MethodInputModel> methodInputModes) {
        this.methodInputModes = methodInputModes;
    }

    public List<CaseSqlModel> getCaseSql() {
        return caseSql;
    }

    public void setCaseSql(List<CaseSqlModel> caseSql) {
        this.caseSql = caseSql;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getDomainFunctionVersionId() {
        return domainFunctionVersionId;
    }

    public void setDomainFunctionVersionId(String domainFunctionVersionId) {
        this.domainFunctionVersionId = domainFunctionVersionId;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }
}
