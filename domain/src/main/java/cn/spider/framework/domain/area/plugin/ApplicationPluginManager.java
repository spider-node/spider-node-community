package cn.spider.framework.domain.area.plugin;

import cn.spider.framework.domain.area.agent.AgentOkhttpClient;
import cn.spider.framework.domain.area.agent.AgentVertxClient;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationPluginManager {
    // 加载领域
    private AgentVertxClient agentClient;


    public ApplicationPluginManager(AgentVertxClient agentClient) {
        this.agentClient = agentClient;
    }

    public Future<JsonObject> querySonArea(JsonObject param){
        return agentClient.querySon(param);
    }

    public Future<JsonObject> query(JsonObject param){
        return agentClient.querySon(param);
    }

    public Future<JsonObject> deployCode(JsonObject param){
        return agentClient.deployCode(param);
    }

    // 卸载 功能

    // 升级 版本

}
