package cn.spider.framework.param.result.build.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@ProxyGen
@VertxGen
public interface ParamRefreshInterface {
    String ADDRESS = "PARAM_REFRESH";

    static ParamRefreshInterface createProxy(Vertx vertx, String address) {
        return new ParamRefreshInterfaceVertxEBProxy(vertx, address);
    }
    Future<Void> refreshMethod(JsonObject param);
}
