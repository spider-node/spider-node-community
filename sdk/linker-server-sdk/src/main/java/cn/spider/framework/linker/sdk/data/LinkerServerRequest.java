package cn.spider.framework.linker.sdk.data;

/**
 * @program: spider-node
 * @description: 请求执行的参数类
 * @author: dds
 * @create: 2023-03-02 13:04
 */
public class LinkerServerRequest {
    /**
     * 请求类型
     */
    private ExecutionType executionType;
    /**
     * 功能请求参数
     */
    private FunctionRequest functionRequest;
    /**
     * 事务请求参数
     */
    private TransactionalRequest transactionalRequest;

    /**
     * 重试的实例请求id
     */
    private String parentRequestId;

    /**
     * 请求重试的类型
     */
    private String retryType;

    /**
     * 重试的节点id
     */
    private String retryNodeId;

    /**
     * 当前节点
     */
    private String nowNodeId;

    public String getNowNodeId() {
        return nowNodeId;
    }

    public void setNowNodeId(String nowNodeId) {
        this.nowNodeId = nowNodeId;
    }

    public String getParentRequestId() {
        return parentRequestId;
    }

    public void setParentRequestId(String parentRequestId) {
        this.parentRequestId = parentRequestId;
    }

    public String getRetryType() {
        return retryType;
    }

    public void setRetryType(String retryType) {
        this.retryType = retryType;
    }

    public String getRetryNodeId() {
        return retryNodeId;
    }

    public void setRetryNodeId(String retryNodeId) {
        this.retryNodeId = retryNodeId;
    }

    public ExecutionType getExecutionType() {
        return executionType;
    }

    public void setExecutionType(ExecutionType executionType) {
        this.executionType = executionType;
    }

    public FunctionRequest getFunctionRequest() {
        return functionRequest;
    }

    public void setFunctionRequest(FunctionRequest functionRequest) {
        this.functionRequest = functionRequest;
    }

    public TransactionalRequest getTransactionalRequest() {
        return transactionalRequest;
    }

    public void setTransactionalRequest(TransactionalRequest transactionalRequest) {
        this.transactionalRequest = transactionalRequest;
    }
}
