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
