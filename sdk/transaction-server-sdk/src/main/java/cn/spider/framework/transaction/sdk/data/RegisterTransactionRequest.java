package cn.spider.framework.transaction.sdk.data;
/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.transaction.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-07  17:23
 * @Description: 注册请求类
 * @Version: 1.0
 */
public class RegisterTransactionRequest {
    private String workerName;

    private String requestId;

    private String groupId;

    private String taskId;

    private String taskGroupId;

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskGroupId() {
        return taskGroupId;
    }

    public void setTaskGroupId(String taskGroupId) {
        this.taskGroupId = taskGroupId;
    }
}
