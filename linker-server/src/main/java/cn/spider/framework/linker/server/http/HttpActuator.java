package cn.spider.framework.linker.server.http;

import cn.spider.framework.common.utils.ExceptionMessage;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class HttpActuator {
    private WebClient webClient;

    public HttpActuator(WebClient webClient) {
        this.webClient = webClient;
    }

    public void post(String url, JsonObject param, Promise<JsonObject> promise, Map<String, String> headers) {
        MultiMap multiMap = MultiMap.caseInsensitiveMultiMap();
        multiMap.addAll(headers);
        webClient.postAbs(url).putHeaders(multiMap)
                .sendJsonObject(param)
                .onSuccess(res -> {
                    JsonObject body = res.bodyAsJsonObject();
                    log.info("执行成功的信息为 {}", body.toString());
                    promise.complete(body);
                })
                .onFailure(fail -> {
                    log.error("对插件操作失败 {} 执行失败的信息为 {}", param.toString(), ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
    }

    public void get(String url, JsonObject param, Promise<JsonObject> promise, Map<String, String> headers) {
        MultiMap multiMap = MultiMap.caseInsensitiveMultiMap();
        multiMap.addAll(headers);
        webClient.getAbs(url).putHeaders(multiMap)
                .sendJsonObject(param)
                .onSuccess(res -> {
                    JsonObject body = res.bodyAsJsonObject();
                    log.info("执行成功的信息为 {}", body.toString());
                    promise.complete(body);
                })
                .onFailure(fail -> {
                    log.error("对插件操作失败 {} 执行失败的信息为 {}", param.toString(), ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
    }
}
