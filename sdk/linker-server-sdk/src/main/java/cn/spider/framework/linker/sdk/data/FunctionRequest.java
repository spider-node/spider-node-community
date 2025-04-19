package cn.spider.framework.linker.sdk.data;

import java.util.Map;

/**
 * @program: spider-node
 * @description: 功能请求参数
 * @author: dds
 * @create: 2023-03-02 13:09
 */
public class FunctionRequest {
    /**
     * 工作服务名称
     */
    private String workerName;
    /**
     * 组件名称
     */
    private String componentName;
    /**
     * service名称
     */
    private String serviceName;
    /**
     * 方法名称
     */
    private String methodName;
    /**
     * 链路id
     */
    private String requestId;

    private String xid;

    private Long branchId;

    /**
     * 请求参数
     */
    private Map<String, Object> param;

    /**
     * 版本
     */
    private String version;

    // 应用类型
    private ApplicationProviderType providerType;

    private String functionType;

    /**
     * http_url
     */
    private String httpUrl;

    /**
     * http_header
     */
    private Map<String, String> httpHeader;

    /**
     * http/https的类型
     */
    private String httpType;

    /**
     * 功能id
     */
    private String functionVersionId;

    private Boolean https;

    // 宿主机判断如何调用插件
    private String uniqueId;

    public Boolean getHttps() {
        return https;
    }

    public void setHttps(Boolean https) {
        this.https = https;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public ApplicationProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(ApplicationProviderType providerType) {
        this.providerType = providerType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
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

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public Map<String, String> getHttpHeader() {
        return httpHeader;
    }

    public void setHttpHeader(Map<String, String> httpHeader) {
        this.httpHeader = httpHeader;
    }

    public String getHttpType() {
        return httpType;
    }

    public void setHttpType(String httpType) {
        this.httpType = httpType;
    }

    public String getFunctionVersionId() {
        return functionVersionId;
    }

    public void setFunctionVersionId(String functionVersionId) {
        this.functionVersionId = functionVersionId;
    }
}
