package cn.spider.framework.domain.area.agent;

import cn.spider.framework.common.utils.ExceptionMessage;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    private String querySonAreaBaseInfo;

    // 部署的url
    private String installPlugin;

    // 卸载的url
    private String unInstallPlugin;

    private String coderApi;

    private String updatePluginUrl;

    private String analysisDataFlowUrl;

    private String analysisParamInfo;

    private String analysisDemandInfo;

    private String jsonToJavaEntity;

    private String buildNodeParam;

    public AgentVertxClient(WebClient webClient, String agentPrefix, String aiCodePrefix) {

        this.webClient = webClient;
        this.queryAllAreaInfo = "/areaDomain/query_all_area_info";
        this.buildPluginUrl = "/code_agent/build_area_plugin";
        this.initAreaBaseUrl = "/code_agent/init_area_base";
        this.updatePluginUrl = "/code_agent/update_function_coder";
        this.initAiRagUrl = "/insert/doc";
        this.querySonAreaBaseInfo = "/areaDomain/query_base_info";
        this.installPlugin = "/installBiz";
        this.unInstallPlugin = "/uninstallBiz";
        this.coderApi = "/ai_code_automatic";
        this.analysisDataFlowUrl = "/analysis_domain_info";
        this.analysisParamInfo = "/analysis_param";
        this.analysisDemandInfo = "/demand_analysis";
        this.jsonToJavaEntity = "/json_to_entity";
        this.buildNodeParam = "/build_node_param";


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
        return send(param, this.buildPluginUrl, this.agentPort, this.agentHost);
    }

    public Future<JsonObject> queryAllArea(JsonObject param) {
        return send(param, this.queryAllAreaInfo, this.agentPort, this.agentHost);
    }

    public Future<JsonObject> querySonBaseInfo(JsonObject param) {
        return send(param, this.querySonAreaBaseInfo, this.agentPort, this.agentHost);
    }

    public Future<JsonObject> initAreaBase(JsonObject param) {
        return send(param, this.initAreaBaseUrl, this.agentPort, this.agentHost);
    }

    public Future<JsonObject> initAiRag(JsonObject param) {
        return send(param, this.initAiRagUrl, this.aiCodePort, this.aiCodeHost);
    }

    public Future<JsonObject> updatePlugin(JsonObject param) {
        return send(param, this.updatePluginUrl, this.agentPort, this.agentHost);
    }

    public Future<JsonObject> createCoder(JsonObject param) {
        return send(param, this.coderApi, this.aiCodePort, this.aiCodeHost);
    }

    public Future<JsonObject> analysisDataFlow(JsonObject param) {
        return send(param, this.analysisDataFlowUrl, this.aiCodePort, this.aiCodeHost);
    }

    public Future<JsonObject> analysisParam(JsonObject param) {
        return send(param, this.analysisParamInfo, this.aiCodePort, this.aiCodeHost);
    }

    /**
     * uninstallBiz
     */
    public Future<JsonObject> uninstallBiz(JsonObject param, String ip, Integer port) {
        return sendHostApplication(ip, this.unInstallPlugin, port, param);
    }

    /**
     * 解析 需求
     *
     * @return
     */
    public Future<JsonObject> analysisDemand(JsonObject param) {
        return send(param, this.analysisDemandInfo, this.aiCodePort, this.aiCodeHost);
    }

    public Future<JsonObject> jsonToJavaEntity(JsonObject param) {
        return send(param, this.jsonToJavaEntity, this.aiCodePort, this.aiCodeHost);
    }

    public Future<JsonObject> buildNodeParam(JsonObject param) {
        return send(param, this.buildNodeParam, this.aiCodePort, this.aiCodeHost);
    }


    public Future<Void> installPlugin(Set<String> applicationIps, JsonObject pluginParam) {
        Promise<Void> promise = Promise.promise();
        // 获取所有的future
        List<Future> needFutures = new ArrayList<>();
        for (String ip : applicationIps) {
            log.info("开始安装插件 {}", ip);
            Future<JsonObject> installFuture = sendHostApplication(ip, this.installPlugin, 1238, pluginParam);
            needFutures.add(installFuture);
        }
        CompositeFuture.all(needFutures)
                .onSuccess(suss -> {
                    promise.complete();
                })
                .onFailure(fail -> {
                    promise.fail(fail);
                });
        return promise.future();
    }

    public Future<Void> unInstall(Set<String> applicationIps, JsonObject pluginParam) {
        Promise<Void> promise = Promise.promise();
        // 获取所有的future
        List<Future> needFutures = new ArrayList<>();
        for (String ip : applicationIps) {
            Future<JsonObject> unInstallFuture = sendHostApplication(ip, this.unInstallPlugin, 1238, pluginParam);
            needFutures.add(unInstallFuture);
        }
        CompositeFuture.all(needFutures)
                .onSuccess(suss -> {
                    promise.complete();
                })
                .onFailure(fail -> {
                    promise.fail(fail);
                });
        return promise.future();
    }

    private Future<JsonObject> send(JsonObject param, String url, int port, String host) {
        Promise<JsonObject> promise = Promise.promise();

        webClient.post(port, host, url)
                .putHeader("Content-Type", "application/json")
                .sendJsonObject(param)
                .onSuccess(res -> {
                    try {
                        JsonObject body = res.bodyAsJsonObject();
                        if (body.getInteger("status") == 200) {
                            log.info("请求成功的参数为 {}", param.toString());
                            JsonObject result = body.getJsonObject("result");
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

    private Future<JsonObject> sendHostApplication(String host, String url, int port, JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();

        webClient.post(port, host, url)
                .putHeader("Content-Type", "application/json")
                .sendJsonObject(param)
                .onSuccess(res -> {
                    try {
                        JsonObject body = res.bodyAsJsonObject();
                        if (body.getString("code").equals("SUCCESS")) {
                            log.info("请求成功的参数为-HostApplication {}", param.toString());
                            JsonObject result = body.getJsonObject("data");
                            promise.complete(result);
                        } else {
                            log.info("执行失败的异常数据-HostApplication {}", res.bodyAsJsonObject().toString());
                            promise.fail("对插件操作失败-HostApplication" + res.bodyAsJsonObject().toString());
                        }
                    } catch (Exception e) {
                        promise.fail(e);
                    }

                })
                .onFailure(fail -> {
                    log.error("对插件操作失败 {} 执行失败的信息为 {}", param.toString(), ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

}


