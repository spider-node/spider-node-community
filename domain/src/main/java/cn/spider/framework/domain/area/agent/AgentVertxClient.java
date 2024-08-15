package cn.spider.framework.domain.area.agent;

import cn.spider.framework.common.utils.ExceptionMessage;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AgentVertxClient {
    private WebClient webClient;

    private String deployPluginUrl;

    private String querySonAreaInfoUrl;

    private String host;

    private Integer port;

    public AgentVertxClient(WebClient webClient, String agentPrefix) {

        this.webClient = webClient;
        this.querySonAreaInfoUrl = "/areaDomain/query_son_area_info";
        this.deployPluginUrl = "/code_agent/deploy";
        String ipWithPort = agentPrefix.replace("http://", "");
        // 然后我们使用":"作为分隔符进行分割
        String[] parts = ipWithPort.split(":", 2); // 2表示最多分割一次
        this.host = parts[0];
        this.port = Integer.parseInt(parts[1]);
    }

    public Future<JsonObject> deployCode(JsonObject param) {
        return send(param, this.deployPluginUrl);
    }

    public Future<JsonObject> querySon(JsonObject param) {
        return send(param, this.querySonAreaInfoUrl);
    }


    private Future<JsonObject> send(JsonObject param, String url) {
        Promise<JsonObject> promise = Promise.promise();

        webClient.post(port, host, url)
                .putHeader("Content-Type", "application/json")
                .sendJsonObject(param)
                .onSuccess(res -> {
                    JsonObject result = null;
                    try {
                        result = res.bodyAsJsonObject();
                        if (result.getInteger("code") == 0) {
                            log.info("请求成功的参数为 {}", param.toString());
                        } else {
                            log.info("执行失败的异常数据 {}", res.bodyAsJsonObject().toString());
                            promise.fail("执行失败" + res.bodyAsJsonObject().toString());
                        }
                    } catch (Exception e) {
                        result = new JsonObject();
                    }
                    promise.complete(result);
                })
                .onFailure(fail -> {
                    log.error("请求的功能参数为 {} 执行失败的信息为 {}", param.toString(), ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }


}


