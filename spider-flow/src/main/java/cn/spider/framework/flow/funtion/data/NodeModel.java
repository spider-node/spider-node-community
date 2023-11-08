package cn.spider.framework.flow.funtion.data;

import java.time.LocalDateTime;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.funtion.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-07  22:13
 * @Description: 节点的模型实体类
 * @Version: 1.0
 */
public class NodeModel {
    /**
     * 模型名称
     */
    private String name;

    /**
     * 模型对应的task组件
     */
    private String taskComponent;

    /**
     * 模型对应的方法
     */
    private String taskService;

    /**
     * 工作服务
     */
    private String workerService;

    /**
     * 状态
     */
    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public String getWorkerService() {
        return workerService;
    }

    public void setWorkerService(String workerService) {
        this.workerService = workerService;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
