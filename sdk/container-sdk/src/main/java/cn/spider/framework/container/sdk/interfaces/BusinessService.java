package cn.spider.framework.container.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.container.sdk.interfaces
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-12  14:03
 * @Description: TODO
 * @Version: 1.0
 */
@ProxyGen
@VertxGen
public interface BusinessService {
    String ADDRESS = "BUSINESS_SERVICE";

    static BusinessService createProxy(Vertx vertx, String address) {
        return new BusinessServiceVertxEBProxy(vertx, address);
    }

    // 注册功能
    Future<JsonObject> registerFunction(JsonObject data);

    Future<JsonObject> selectFunction(JsonObject data);
    // 设置开关
    Future<Void> configureDerail(JsonObject data);
    // 配置权重
    Future<Void> configureWeight(JsonObject data);

    /**
     * 删除
     * @param data
     * @return
     */
    Future<Void> deleteFunction(JsonObject data);

    /**
     * 状态变化
     * @param data
     * @return
     */
    Future<Void> stateChange(JsonObject data);

    Future<Void> deleteAll();



}
