package cn.spider.framework.linker.client.data;

import com.alibaba.fastjson.JSON;
import io.vertx.core.json.JsonObject;

/**
 * @BelongsProject: bms_middle_platform
 * @BelongsPackage: com.hope.saas.bms.middle.platform.spider.function
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-13  23:16
 * @Description: TODO
 * @Version: 1.0
 */
public class SpiderFunctionParam {
    private String functionId;

    private JsonObject request;

    private String requestClassType;

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public JsonObject getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = new JsonObject(JSON.toJSONString(request));
    }

    public String getRequestClassType() {
        return requestClassType;
    }

    public void setRequestClassType(String requestClassType) {
        this.requestClassType = requestClassType;
    }
}
