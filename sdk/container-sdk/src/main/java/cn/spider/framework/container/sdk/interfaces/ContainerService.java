package cn.spider.framework.container.sdk.interfaces;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @program: spider-node
 * @description: ContainerService操作service
 * @author: dds
 * @create: 2023-02-22 22:06
 */
@ProxyGen
@VertxGen
public interface ContainerService {

    String ADDRESS = "CONTAINER_SERVICE";

    static ContainerService createProxy(Vertx vertx, String address) {
        return new ContainerServiceVertxEBProxy(vertx, address);
    }
    // 注册任务信息
    Future<Void> deployBpmn(JsonObject data);

    /**
     * 刷新bpmn
     * @param data
     * @return
     */
    Future<Void> refreshBpmn(JsonObject data);

    Future<Void> destroyBpmn(JsonObject data);

    Future<Void> startBpmn(JsonObject data);
    // 销毁
    Future<Void> unloadBpmn(JsonObject data);



    // 加载 class部署具体的接口信息
    Future<Void> loaderClass(JsonObject data);

    Future<Void> startClass(JsonObject data);

    /**
     *
     * @param data
     * @return
     */
    Future<Void> refreshSdk(JsonObject data);

    Future<Void> unloadSdk(JsonObject data);

    Future<Void> destroyClass(JsonObject data);

    /**
     * 查询 bpmn
     * @param data
     * @return
     */
    Future<JsonObject> queryBpmn(JsonObject data);

    /**
     * 查询sdk
     * @param data
     * @return
     */
    Future<JsonObject> querySdk(JsonObject data);


}
