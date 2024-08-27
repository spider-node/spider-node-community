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

    private String buildPluginUrl;

    private String queryAllAreaInfo;

    private String initAreaBaseUrl;

    private String agentHost;

    private Integer agentPort;

    private String aiCodeHost;

    private Integer aiCodePort;

    private String initAiRagUrl;

    public AgentVertxClient(WebClient webClient, String agentPrefix,String aiCodePrefix) {

        this.webClient = webClient;
        this.queryAllAreaInfo = "/areaDomain/query_all_area_info";
        this.buildPluginUrl = "/code_agent/build_area_plugin";
        this.initAreaBaseUrl = "/code_agent/init_area_base";
        this.initAiRagUrl = "/insert/doc";

        String ipWithPort = agentPrefix.replace("http://", "");
        // 然后我们使用":"作为分隔符进行分割
        String[] parts = ipWithPort.split(":", 2); // 2表示最多分割一次
        this.agentHost = parts[0];
        this.agentPort = Integer.parseInt(parts[1]);

        String ipWithPortAi = aiCodePrefix.replace("http://", "");
        // 然后我们使用":"作为分隔符进行分割
        String[] aiParts = ipWithPortAi.split(":", 2);
        this.aiCodeHost = aiParts[0];
        this.aiCodePort = Integer.parseInt(aiParts[1]);
    }

    public Future<JsonObject> buildPlugin(JsonObject param) {
        return send(param, this.buildPluginUrl,this.agentPort,this.agentHost);
    }

    public Future<JsonObject> queryAllArea(JsonObject param) {
        return send(param, this.queryAllAreaInfo,this.agentPort,this.agentHost);
    }

    public Future<JsonObject> initAreaBase(JsonObject param){
        return send(param, this.initAreaBaseUrl,this.agentPort,this.agentHost);
    }

    public Future<JsonObject> initAiRag(JsonObject param){
        return send(param, this.initAiRagUrl,this.aiCodePort,this.aiCodeHost);
    }


    private Future<JsonObject> send(JsonObject param, String url,int port,String host) {
        Promise<JsonObject> promise = Promise.promise();

        webClient.post(port, host, url)
                .putHeader("Content-Type", "application/json")
                .sendJsonObject(param)
                .onSuccess(res -> {
                    try {
                        JsonObject body = res.bodyAsJsonObject();
                        if (body.getInteger("status") == 200) {
                            log.info("请求成功的参数为 {}", param.toString());
                            JsonObject result  = body.getJsonObject("result");
                            promise.complete(result);
                        } else {
                            log.info("执行失败的异常数据 {}", res.bodyAsJsonObject().toString());
                            promise.fail("执行失败" + res.bodyAsJsonObject().toString());
                        }
                    } catch (Exception e) {
                        promise.fail(e);
                    }

                })
                .onFailure(fail -> {
                    log.error("请求的功能参数为 {} 执行失败的信息为 {}", param.toString(), ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }




}


