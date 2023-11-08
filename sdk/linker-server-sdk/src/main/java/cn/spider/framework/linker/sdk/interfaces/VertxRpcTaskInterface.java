package cn.spider.framework.linker.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.linker.sdk.interfaces
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-16  16:56
 * @Description: TODO
 * @Version: 1.0
 */
@ProxyGen
@VertxGen
public interface VertxRpcTaskInterface {
    String ADDRESS = "TASK_SERVICE";

    static VertxRpcTaskInterface createProxy(Vertx vertx, String address) {
        return new VertxRpcTaskInterfaceVertxEBProxy(vertx, address);
    }

    /**
     * 执行业务
     * @param data
     * @return
     */
    Future<JsonObject> run(JsonObject data);

    /**
     * 执行事务操作
     * @param data
     * @return
     */
    Future<JsonObject> transactionOperate(JsonObject data);
}
