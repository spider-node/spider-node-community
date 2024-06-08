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
 * @CreateTime: 2023-08-25  15:19
 * @Description: function-功能的接口
 * @Version: 1.0
 */
@ProxyGen
@VertxGen
public interface FunctionInterface {
    String ADDRESS = "AREA_FUNCTION";
    // 新增功能
    static FunctionInterface createProxy(Vertx vertx, String address) {
        return new FunctionInterfaceVertxEBProxy(vertx, address);
    }

    // 执行功能
    Future<Void> insertFunction(JsonObject data);
    // 修改功能
    Future<Void> updateFunction(JsonObject data);
    // 查询功能
    Future<JsonObject> queryFunction(JsonObject data);
    // 查询执行的功能节点
    Future<JsonObject> findExecuteFunction(JsonObject data);

    // 启动功能
    Future<Void> startStopFunction(JsonObject model);

    /**
     * 基于requestId
     * @param param
     * @return
     */
    Future<JsonObject> queryRunHistoryData(JsonObject param);


    /**
     * 基于requestId
     * @param param
     * @return
     */
    Future<JsonObject> queryRunHistoryElementData(JsonObject param);


}
