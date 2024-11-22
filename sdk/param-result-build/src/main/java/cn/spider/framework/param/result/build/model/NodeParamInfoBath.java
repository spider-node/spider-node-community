package cn.spider.framework.param.result.build.model;

import cn.spider.framework.param.result.build.model.enums.WorkerType;

import java.util.List;


public class NodeParamInfoBath {
    private List<NodeParamInfo> nodeParamInfoList;

    /**
     * 领域id
     */
    private String areaId;

    /**
     * 子域id
     */
    private String sonAreaId;

    /**
     * 任务id
     */
    private String taskId;

    private String pluginKey;

    private WorkerType workerType;

    public List<NodeParamInfo> getNodeParamInfoList() {
        return nodeParamInfoList;
    }

    public void setNodeParamInfoList(List<NodeParamInfo> nodeParamInfoList) {
        this.nodeParamInfoList = nodeParamInfoList;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
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

    public String getPluginKey() {
        return pluginKey;
    }

    public void setPluginKey(String pluginKey) {
        this.pluginKey = pluginKey;
    }

    public WorkerType getWorkerType() {
        return workerType;
    }

    public void setWorkerType(WorkerType workerType) {
        this.workerType = workerType;
    }
}
