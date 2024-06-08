package cn.spider.framework.domain.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * 版本管理-sdk
 */
@ProxyGen
@VertxGen
public interface VersionInterface {
    String ADDRESS = "AREA_VERSION";
    // 新增功能
    static VersionInterface createProxy(Vertx vertx, String address) {
        return new VersionInterfaceVertxEBProxy(vertx, address);
    }
    // 新增版本
    Future<Void> insertVersion(JsonObject data);
    // 更新版本
    Future<Void> updateVersion(JsonObject data);

    // 启停版本
    Future<Void> startOrStopVersion(JsonObject data);

    // 刷新bpmn
    Future<Void> refreshVersion(JsonObject data);
    // 查询版本
    Future<JsonObject> queryVersion(JsonObject data);

    // 查询bpmn-url
    Future<JsonObject> queryBpmnUrl();

    // 根据功能id查询版本信息
    Future<JsonObject> queryVersionByFunctionId(JsonObject data);
}
