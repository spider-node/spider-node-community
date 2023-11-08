package cn.spider.framework.domain.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.sdk.interfaces
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-25  15:24
 * @Description: 节点-管理接口类
 * @Version: 1.0
 */
@ProxyGen
@VertxGen
public interface NodeInterface {

    String ADDRESS = "AREA_NODE";
    // 新增功能
    static NodeInterface createProxy(Vertx vertx, String address) {
        return new NodeInterfaceVertxEBProxy(vertx, address);
    }

    // 新增节点
    Future<Void> insertNode(JsonObject data);
    // 编辑节点
    Future<Void> updateNode(JsonObject data);
    // 下发节点
    Future<Void> distributeNode(JsonObject data);
    // 更新节点输入字段
    Future<Void> updateParam(JsonObject data);
    // 查询节点
    Future<JsonObject> queryJsonObject(JsonObject data);
}
