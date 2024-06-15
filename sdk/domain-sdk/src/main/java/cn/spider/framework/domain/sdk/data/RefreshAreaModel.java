package cn.spider.framework.domain.sdk.data;

import java.util.Map;

public class RefreshAreaModel {
    /**
     * 组件名称
     */
    private String taskComponent;

    /**
     * 组件方法
     */
    private String taskService;

    /**
     * 组件出入参数
     */
    private Map<String,Object> parmMap;

    /**
     * 提供能力的worker
     */
    private String worker;

    /**
     * 提供该能力的方法
     */
    private String method;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 功能描述
     */
    private String desc;

    /**
     * 领域id
     */
    private String areaId;

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
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

    public Map<String, Object> getParmMap() {
        return parmMap;
    }

    public void setParmMap(Map<String, Object> parmMap) {
        this.parmMap = parmMap;
    }
}
