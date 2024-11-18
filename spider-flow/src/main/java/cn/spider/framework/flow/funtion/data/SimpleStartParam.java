package cn.spider.framework.flow.funtion.data;

import java.util.Map;

public class SimpleStartParam {
    private Map<String, Object> paramMap;
    private String method;
    private String taskComponent;
    private String taskService;

    private String version;

    private String workerName;

    /**
     * 工作类型
     */
    private String workerType;



    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getWorkerType() {
        return workerType;
    }

    public void setWorkerType(String workerType) {
        this.workerType = workerType;
    }
}
