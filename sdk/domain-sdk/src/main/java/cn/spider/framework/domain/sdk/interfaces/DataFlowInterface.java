package cn.spider.framework.domain.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@ProxyGen
@VertxGen
public interface DataFlowInterface {
    String ADDRESS = "DATA_FLOW";

    static DataFlowInterface createProxy(Vertx vertx, String address) {
        return new DataFlowInterfaceVertxEBProxy(vertx, address);
    }

    // 查询数据流
    Future<JsonObject> queryDataFlow(JsonObject param);

    // 查询数据流
    Future<JsonObject> queryDataFlowInfos(JsonObject param);
    // 新增/修改数据流
    Future<Void> upsertDataFlow(JsonObject param);

    Future<Void> upsertDataFlowStatus(JsonObject param);
}
