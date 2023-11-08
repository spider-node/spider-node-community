package cn.spider.framework.container.sdk.data;

import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.container.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-08  22:30
 * @Description: TODO
 * @Version: 1.0
 */
public class SelectFunctionResponse {
    private List<JsonObject> functions;

    public List<JsonObject> getFunctions() {
        return functions;
    }

    public void setFunctions(List<JsonObject> functions) {
        this.functions = functions;
    }
}
