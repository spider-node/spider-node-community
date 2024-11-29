package cn.spider.node.host.plugin.center.sdk.interfaces;


import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@ProxyGen
@VertxGen
public interface HostPluginInterface {
    String ADDRESS = "HOST_PLUGIN-SERVICE";

    static HostPluginInterface createProxy(Vertx vertx, String address) {
        return new HostPluginInterfaceVertxEBProxy(vertx, address);
    }

    /**
     * 上线 宿主机
     * @param data
     * @return
     */
    Future<Void> hostOnline(JsonObject data);

    /**
     * 下线 宿主机
     * @param data
     * @return
     */
    Future<Void> hostOffline(JsonObject data);

    /**
     * 插件下线
     */
    Future<Void> pluginOffline(JsonObject data);

    /**
     * 插件 上线
     */
    Future<Void> pluginOnline(JsonObject data);

    /**
     *
     * @param data 宿主应用的信息
     * @return 返回 宿主应用中的插件信息
     */
    Future<JsonObject> queryHostPluginInfo(JsonObject data);

    /**
     *
     * @param data task_component与task_service信息
     * @return 功能信息
     */
    Future<JsonObject> queryFunctionVersion(JsonObject data);

    Future<JsonObject> queryDeployInfo(JsonObject data);

    // 校验是否有部署,如果没有，发起部署
    Future<Void> checkDeployInfo(JsonObject data);
}
