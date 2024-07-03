package cn.spider.framework.param.sdk.data;

import lombok.Data;

import java.util.Map;

public class QueryRequestParam {
    /**
     * task-组件
     */
    private String taskComponent;

    /**
     * task-service
     */
    private String taskService;
    /**
     * 请求id
     */
    private String requestId;

    /**
     * 转化参数
     */
    private Map<String, String> paramsMapping;

    /**
     * 指定参数
     */
    private Map<String, Object> appointParam;

    /**
     * 转换参数
     */
    private Map<String, Object> conversionParam;

    public Map<String, Object> getConversionParam() {
        return conversionParam;
    }

    public void setConversionParam(Map<String, Object> conversionParam) {
        this.conversionParam = conversionParam;
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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Map<String, String> getParamsMapping() {
        return paramsMapping;
    }

    public void setParamsMapping(Map<String, String> paramsMapping) {
        this.paramsMapping = paramsMapping;
    }

    public Map<String, Object> getAppointParam() {
        return appointParam;
    }

    public void setAppointParam(Map<String, Object> appointParam) {
        this.appointParam = appointParam;
    }
}
