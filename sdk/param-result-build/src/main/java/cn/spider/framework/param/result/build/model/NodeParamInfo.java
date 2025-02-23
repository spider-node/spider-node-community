package cn.spider.framework.param.result.build.model;

import cn.spider.framework.param.result.build.NodeField;

import java.lang.reflect.Method;
import java.util.List;

public class NodeParamInfo {

    /**
     * 提供能力的宿主应用
     */
    private String worker;

    /**
     * 提供能力方法
     */
    private String method;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 功能名称
     */
    private String desc;

    /**
     * 领域id
     */
    private String areaId;

    /**
     * 子域id
     */
    private String sonAreaId;

    /**
     * 版本
     */
    private String version;

    /**
     * 组件名
     */
    private String taskComponent;

    /**
     * 任务标识
     */
    private String taskService;

    /**
     * 任务id
     */
    private String taskId;

    public NodeParamInfo(String worker, String method, String functionName, String desc, String subDomainId) {
        this.worker = worker;
        this.method = method;
        this.functionName = functionName;
        this.desc = desc;
        this.sonAreaId = subDomainId;
    }

    public NodeParamInfo() {

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

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public String getSonAreaId() {
        return sonAreaId;
    }

    public void setSonAreaId(String sonAreaId) {
        this.sonAreaId = sonAreaId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
