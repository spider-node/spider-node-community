package cn.spider.node.host.plugin.center.application.http;

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
public class HostApplicationClient {
    private WebClient webClient;

    // 部署的url
    private String installPlugin;

    // 卸载的url
    private String unInstallPlugin;

    private Integer hostApplicationPort;

    public HostApplicationClient(WebClient webClient,Integer hostApplicationPort) {
        this.webClient = webClient;
        this.installPlugin = "/installBiz";
        this.unInstallPlugin = "/uninstallBiz";
        this.hostApplicationPort = hostApplicationPort;
    }

    public Future<Void> installPlugin(String applicationIp, JsonObject pluginParam) {
        // 获取所有的future
        log.info("部署插件ip {} 插件信息 {}", applicationIp, pluginParam.toString());
        return sendHostApplication(applicationIp, this.installPlugin, hostApplicationPort, pluginParam);
    }

    public Future<Void> unInstall(String applicationIp, JsonObject pluginParam) {
        return sendHostApplication(applicationIp, this.unInstallPlugin, hostApplicationPort, pluginParam);
    }

    private Future<Void> sendHostApplication(String host, String url, int port, JsonObject param) {
        Promise<Void> promise = Promise.promise();

        webClient.post(port, host, url)
                .putHeader("Content-Type", "application/json")
                .sendJsonObject(param)
                .onSuccess(res -> {
                    try {
                        JsonObject body = res.bodyAsJsonObject();
                        if (body.getString("code").equals("SUCCESS")) {
                            promise.complete();
                        } else {
                            log.info("执行失败的异常数据 {}", res.bodyAsJsonObject().toString());
                            promise.fail("对插件操作失败" + res.bodyAsJsonObject().toString());
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
