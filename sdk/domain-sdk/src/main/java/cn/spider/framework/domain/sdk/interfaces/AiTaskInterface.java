package cn.spider.framework.domain.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@ProxyGen
@VertxGen
public interface AiTaskInterface {
    String ADDRESS = "AI_TASK";

    static AiTaskInterface createProxy(Vertx vertx, String address) {
        return new AiTaskInterfaceVertxEBProxy(vertx, address);
    }

    Future<Void> createCoder(JsonObject param);


}
