package cn.spider.framework.linker.sdk.data;

/**
 * @program: spider-node
 * @description: 事务请求
 * @author: dds
 * @create: 2023-03-02 13:14
 */
public class TransactionalRequest {
    // 相当于-xid
    private String transactionId;

    private TransactionalType transactionalType;

    private Long branchId;

    private String resourceId;

    /**
     * 事务操作的workerName
     */
    private String workerName;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionalType getTransactionalType() {
        return transactionalType;
    }

    public void setTransactionalType(TransactionalType transactionalType) {
        this.transactionalType = transactionalType;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
