package cn.spider.framework.log.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.log.sdk.interfaces
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-18  22:23
 * @Description: TODO
 * @Version: 1.0
 */
@ProxyGen
@VertxGen
public interface LogInterface {

    String ADDRESS = "LOG_SERVICE";

    static LogInterface createProxy(Vertx vertx, String address) {
        return new LogInterfaceVertxEBProxy(vertx, address);
    }

    Future<JsonObject> queryFlowExample(JsonObject param);

    Future<JsonObject> queryExampleExample(JsonObject param);

}
