package cn.spider.framework.container.sdk.data;

import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.container.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-03  23:12
 * @Description: TODO
 * @Version: 1.0
 */
public class QuerySdkResponse {
    private List<JsonObject> sdk;

    public List<JsonObject> getSdk() {
        return sdk;
    }

    public void setSdk(List<JsonObject> sdk) {
        this.sdk = sdk;
    }
}
