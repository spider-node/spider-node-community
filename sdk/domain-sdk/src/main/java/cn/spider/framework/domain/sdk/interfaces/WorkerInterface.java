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
 * @CreateTime: 2023-09-08  18:46
 * @Description: 工作域
 * @Version: 1.0
 */
@ProxyGen
@VertxGen
public interface WorkerInterface {

    String ADDRESS = "AREA_WORKER";
    // 新增功能
    static WorkerInterface createProxy(Vertx vertx, String address) {
        return new WorkerInterfaceVertxEBProxy(vertx, address);
    }

    Future<JsonObject> queryWorkerInfo(JsonObject data);


}
