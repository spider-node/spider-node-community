package cn.spider.framework.transaction.sdk.interfaces;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.transaction.sdk.interfaces
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-07  17:14
 * @Description: 事务操作接口类
 * @Version: 1.0
 */
@ProxyGen
@VertxGen
public interface TransactionInterface {
    String ADDRESS = "TRANSACTION_ELEMENT_SERVICE";

    static TransactionInterface createProxy(Vertx vertx, String address) {
        return new TransactionInterfaceVertxEBProxy(vertx, address);
    }

    Future<JsonObject> registerTransaction(JsonObject data);

    Future<JsonObject> commit(JsonObject data);

    Future<JsonObject> rollBack(JsonObject data);

    Future<Void> replaceTranscripts(JsonObject data);
}
