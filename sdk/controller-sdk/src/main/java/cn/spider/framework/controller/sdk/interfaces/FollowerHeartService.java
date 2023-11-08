package cn.spider.framework.controller.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.sdk.interfaces
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-22  16:45
 * @Description: 检测某个
 * @Version: 1.0
 */
@ProxyGen
@VertxGen
public interface FollowerHeartService {

    String ADDRESS = "FOLLOWER_HEART_SERVICE";

    static FollowerHeartService createProxy(Vertx vertx, String address) {
        return new FollowerHeartServiceVertxEBProxy(vertx, address);
    }

    Future<Void> detection();

    Future<Void> reconnectLeader();
}
