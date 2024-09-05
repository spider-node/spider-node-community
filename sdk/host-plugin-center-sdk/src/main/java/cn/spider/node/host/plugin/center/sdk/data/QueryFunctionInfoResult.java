package cn.spider.node.host.plugin.center.sdk.data;

import java.time.LocalDateTime;

public class QueryFunctionInfoResult {
    /**
     * 数据源id
     */
    private Integer datasourceId;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 功能描述
     */
    private String functionDesc;

    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * 业务功能方法提供的类
     */
    private String areaFunctionClass;

    /**
     * 业务方法的入参
     */
    private String areaFunctionParamClass;

    /**
     * 业务方法的出参
     */
    private String areaFunctionResultClass;

    /**
     * 使用的基础版本
     */
    private String baseVersion;

    /**
     * 状态-init,init_fail,init_suss
     */
    private String status;

    /**
     * 版本
     */
    private String version;

    /**
     * pom文件中的group_id
     */
    private String groupId;

    /**
     * pom文件中的artifact_id
     */
    private String artifactId;

    /**
     * 组件名称
     */
    private String taskComponent;

    /**
     * 组件方法
     */
    private String taskService;

    private LocalDateTime createTime;

    private String bizUrl;

    /**
     * 部署的实例数量
     */
    private Integer instanceNum;

    public Integer getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Integer datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionDesc() {
        return functionDesc;
    }

    public void setFunctionDesc(String functionDesc) {
        this.functionDesc = functionDesc;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public String getAreaFunctionClass() {
        return areaFunctionClass;
    }

    public void setAreaFunctionClass(String areaFunctionClass) {
        this.areaFunctionClass = areaFunctionClass;
    }

    public String getAreaFunctionParamClass() {
        return areaFunctionParamClass;
    }

    public void setAreaFunctionParamClass(String areaFunctionParamClass) {
        this.areaFunctionParamClass = areaFunctionParamClass;
    }

    public String getAreaFunctionResultClass() {
        return areaFunctionResultClass;
    }

    public void setAreaFunctionResultClass(String areaFunctionResultClass) {
        this.areaFunctionResultClass = areaFunctionResultClass;
    }

    public String getBaseVersion() {
        return baseVersion;
    }

    public void setBaseVersion(String baseVersion) {
        this.baseVersion = baseVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getBizUrl() {
        return bizUrl;
    }

    public void setBizUrl(String bizUrl) {
        this.bizUrl = bizUrl;
    }

    public Integer getInstanceNum() {
        return instanceNum;
    }

    public void setInstanceNum(Integer instanceNum) {
        this.instanceNum = instanceNum;
    }
}
