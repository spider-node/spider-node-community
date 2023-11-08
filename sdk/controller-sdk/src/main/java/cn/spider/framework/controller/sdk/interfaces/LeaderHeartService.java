package cn.spider.framework.controller.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * 跟leader心跳检测-判断leader是否存活
 */
@ProxyGen
@VertxGen
public interface LeaderHeartService {
    String ADDRESS = "LEADER_HEART_SERVICE";

    static LeaderHeartService createProxy(Vertx vertx, String address) {
        return new LeaderHeartServiceVertxEBProxy(vertx, address);
    }

    Future<Void> detection();

    Future<JsonObject> queryLeaderInfo();

    Future<JsonObject> querySpiderInfo();

    Future<Void> escalationFollowerInfo(JsonObject param);
}
