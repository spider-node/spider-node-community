package cn.spider.framework.controller.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.sdk.interfaces
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-18  21:45
 * @Description: 获取broker的信息
 * @Version: 1.0
 */
@ProxyGen
@VertxGen
public interface BrokerInfoService {

    String ADDRESS = "BROKER_INFO";

    static BrokerInfoService createProxy(Vertx vertx, String address) {
        return new BrokerInfoServiceVertxEBProxy(vertx, address);
    }

    Future<JsonObject> queryBrokerInfo();

}
