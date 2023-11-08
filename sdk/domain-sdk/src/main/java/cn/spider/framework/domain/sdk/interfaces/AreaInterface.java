package cn.spider.framework.domain.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * 域的节点信息
 */
@ProxyGen
@VertxGen
public interface AreaInterface {
    String ADDRESS = "AREA_NEW";

    static AreaInterface createProxy(Vertx vertx, String address) {
        return new AreaInterfaceVertxEBProxy(vertx, address);
    }

    /**
     * 上传sdk
     * @param data
     * @return
     */
    Future<Void> updateSdk(JsonObject data);

    /**
     * 上传sdk
     * @param data
     * @return
     */
    Future<Void> refreshSdk(JsonObject data);


    /**
     * 新增area
     */
    Future<Void> insertArea(JsonObject data);


    /**
     * 新增area
     */
    Future<Void> updateArea(JsonObject data);

    /**
     * 查询领域信息
     * @param data
     * @return
     */
    Future<JsonObject> queryArea(JsonObject data);
}
