package cn.spider.framework.container.sdk.data;
import com.alibaba.fastjson.JSON;
import java.util.Objects;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.container.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-26  20:08
 * @Description: 开始流程的
 * @Version: 1.0
 */
public class StartFlowRequest {
    private String functionId;

    private Object request;

    private String variableKey;

    private String requestClassType;

    public String getRequestClassType() {
        return requestClassType;
    }

    public void setRequestClassType(String requestClassType) {
        this.requestClassType = requestClassType;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public String getVariableKey() {
        return variableKey;
    }

    public void setVariableKey(String variableKey) {
        this.variableKey = variableKey;
    }

    public Object getRequest(){
        return this.request;
    }

    public Object getRequest(ClassLoader classLoader) {
        try {
            if(Objects.isNull(classLoader)){
                return this.request;
            }
            Class requestClass = classLoader.loadClass(this.requestClassType);
            // 获取classLoader-》通过classLoader加载该类
            return JSON.parseObject(JSON.toJSONString(this.request),requestClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void setRequest(Object request) {
        this.request = request;
    }
}
