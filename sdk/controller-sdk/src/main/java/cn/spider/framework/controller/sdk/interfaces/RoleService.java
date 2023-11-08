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
 * @CreateTime: 2023-05-23  17:19
 * @Description: TODO
 * @Version: 1.0
 */
@ProxyGen
@VertxGen
public interface RoleService {
    String ADDRESS = "CONTROLLER_ROLE_SERVICE";

    static RoleService createProxy(Vertx vertx, String address) {
        return new RoleServiceVertxEBProxy(vertx, address);
    }

    Future<JsonObject> queryRole();
}
