package cn.spider.framework.linker.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @program: spider-node
 * @description: 跟服务端交互的接口类
 * @author: dds
 * @create: 2023-03-02 12:52
 */
@ProxyGen
@VertxGen
public interface LinkerService {

    String ADDRESS = "LINKER-SERVICE";

    static LinkerService createProxy(Vertx vertx, String address) {
        return new LinkerServiceVertxEBProxy(vertx, address);
    }

    /**
     * 提交执行任务
     * @param data
     * @return
     */
    Future<JsonObject> submittals(JsonObject data);
}
