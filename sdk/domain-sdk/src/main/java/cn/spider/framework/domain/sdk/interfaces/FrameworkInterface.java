package cn.spider.framework.domain.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@ProxyGen
@VertxGen
public interface FrameworkInterface {
    String ADDRESS = "AREA_FRAMEWORK";

    static FrameworkInterface createProxy(Vertx vertx, String address) {
        return new FrameworkInterfaceVertxEBProxy(vertx, address);
    }

    Future<JsonObject> queryFramework(JsonObject data);

    /**
     * 新增
     */
    Future<Void> insertFramework(JsonObject data);

    /**
     * 修改
     */
    Future<Void> updateFramework(JsonObject data);
}
