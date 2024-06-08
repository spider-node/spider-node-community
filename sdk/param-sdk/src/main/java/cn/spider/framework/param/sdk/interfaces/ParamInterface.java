package cn.spider.framework.param.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@ProxyGen
@VertxGen
public interface ParamInterface {

    String ADDRESS = "PARAM_SERVICE";

    static ParamInterface createProxy(Vertx vertx, String address) {
        return new ParamInterfaceVertxEBProxy(vertx, address);
    }



    /**
     * 查询配置中需要执行的参数
     * @param param
     * @return
     */
    Future<JsonObject> queryRunParam(JsonObject param);

    /**
     * 回写参数
     */
    Future<Void> writeBack(JsonObject param);

    /**
     * 获取参数
     */
    Future<JsonObject> getParamValue(JsonObject param);

    /**
     * notify-requestId
     */
    Future<Void> writeRequestParam(JsonObject param);

    /**
     * 获取返回值
     */
    Future<JsonObject> queryFunctionResult(JsonObject param);
}
