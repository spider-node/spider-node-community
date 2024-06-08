package cn.spider.framework.container.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @program: spider-node
 * @description: 节点service,用于通知FlowElement状态等消息
 * @author: dds
 * @create: 2023-02-23 12:54
 */
@ProxyGen
@VertxGen
public interface FlowService {

    String ADDRESS = "FLOW_ELEMENT_SERVICE";

    static FlowService createProxy(Vertx vertx, String address) {
        return new FlowServiceVertxEBProxy(vertx, address);
    }

    // 通知 执行执行状态
    Future<JsonObject> startFlow(JsonObject data);

    // 执行spider中配置的功能
    Future<JsonObject> startFlowV2(JsonObject data);

    // 执行spider中配置的功能
    Future<JsonObject> startFlowRetry(JsonObject data);

    Future<Void> activation(JsonObject data);

    /**
     * 查询当前实例数量
     * @return
     */
    Future<JsonObject> queryRunNumber();

}
