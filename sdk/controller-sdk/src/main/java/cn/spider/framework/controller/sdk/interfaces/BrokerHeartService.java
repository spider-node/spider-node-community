package cn.spider.framework.controller.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * broker的心跳机制
 */
@ProxyGen
@VertxGen
public interface BrokerHeartService {
    String ADDRESS = "BROKER_HEART_SERVICE";

    static BrokerHeartService createProxy(Vertx vertx, String address) {
        return new BrokerHeartServiceVertxEBProxy(vertx, address);
    }

    Future<Void> detection();
}
