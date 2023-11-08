package cn.spider.framework.transaction.sdk.data;

import cn.spider.framework.transaction.sdk.data.enums.TransactionStatus;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.transaction.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-11  13:15
 * @Description:
 * @Version: 1.0
 */
public class TransactionOperateStatus {
    /**
     * 请求id
     */
    private String requestId;

    /**
     * 事务状态
     */
    private TransactionStatus transactionStatus;

    /**
     * 事务组id --相当于xid
     */
    private String transactionGroupId;

    /**
     * taskId
     */
    private String taskId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionGroupId() {
        return transactionGroupId;
    }

    public void setTransactionGroupId(String transactionGroupId) {
        this.transactionGroupId = transactionGroupId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
