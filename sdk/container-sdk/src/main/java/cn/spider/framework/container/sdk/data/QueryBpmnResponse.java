package cn.spider.framework.container.sdk.data;

import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.container.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-03  22:28
 * @Description: TODO
 * @Version: 1.0
 */
public class QueryBpmnResponse {
    private List<JsonObject> bpmns;

    public List<JsonObject> getBpmns() {
        return bpmns;
    }

    public void setBpmns(List<JsonObject> bpmns) {
        this.bpmns = bpmns;
    }
}
