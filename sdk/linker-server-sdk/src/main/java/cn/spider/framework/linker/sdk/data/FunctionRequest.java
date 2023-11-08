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

    private String branchId;

    /**
     * 请求参数
     */
    private Map<String,Object> param;

    private String version;

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
}
