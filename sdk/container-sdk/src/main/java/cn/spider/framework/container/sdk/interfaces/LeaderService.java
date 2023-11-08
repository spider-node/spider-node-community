package cn.spider.framework.container.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.container.sdk.interfaces
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-21  17:58
 * @Description: 通知follower晋升为leader
 * @Version: 1.0
 */
@ProxyGen
@VertxGen
public interface LeaderService {

    String ADDRESS = "LEADER_SERVICE";

    static LeaderService createProxy(Vertx vertx, String address) {
        return new LeaderServiceVertxEBProxy(vertx, address);
    }

    Future<Void> upgrade();

    Future<Void> relegation();

}
