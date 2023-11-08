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

    private String branchId;

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

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }
}
