package cn.spider.framework.domain.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@ProxyGen
@VertxGen
public interface HttpFunctionInterface {
    String ADDRESS = "AREA_HTTP_FUNCTION";

    static HttpFunctionInterface createProxy(Vertx vertx, String address) {
        return new HttpFunctionInterfaceVertxEBProxy(vertx, address);
    }

    /**
     * 查询功能
     */
    Future<JsonObject> queryHttpFunction(JsonObject data);

    /**
     * 新增修改功能
     */
    Future<Void> upsertHttpFunction(JsonObject data);
}
