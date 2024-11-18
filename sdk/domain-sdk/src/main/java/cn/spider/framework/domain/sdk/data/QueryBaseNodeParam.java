package cn.spider.framework.domain.sdk.data;

public class QueryBaseNodeParam {
    /**
     * task-组件
     */
    private String taskComponent;

    /**
     * task-service
     */
    private String taskService;

    /**
     * 版本
     */
    private String version;

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
}
