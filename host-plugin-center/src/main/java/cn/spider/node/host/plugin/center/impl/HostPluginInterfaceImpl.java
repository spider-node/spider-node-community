package cn.spider.node.host.plugin.center.impl;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.node.host.plugin.center.application.HostApplicationManager;
import cn.spider.node.host.plugin.center.model.data.QueryDeployInfoResult;
import cn.spider.node.host.plugin.center.model.entity.AreaDomainFunctionInfo;
import cn.spider.node.host.plugin.center.model.entity.SpiderPluginDeployInfo;
import cn.spider.node.host.plugin.center.sdk.data.*;
import cn.spider.node.host.plugin.center.sdk.interfaces.HostPluginInterface;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

@Slf4j
@Component
public class HostPluginInterfaceImpl implements HostPluginInterface {

    @Resource
    private HostApplicationManager hostApplicationManager;

    @Resource
    private Executor spiderBusinessPool;

    @Override
    public Future<Void> hostOnline(JsonObject data) {
        HostOnlineParam hostOnlineParam = data.mapTo(HostOnlineParam.class);
        try {
            hostApplicationManager.online(hostOnlineParam.getIp());
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> hostOffline(JsonObject data) {
        HostOfflineParam hostOfflineParam = data.mapTo(HostOfflineParam.class);
        try {
            hostApplicationManager.offline(hostOfflineParam.getIp());
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> pluginOffline(JsonObject data) {
        FunctionPluginOnlineParam param = data.mapTo(FunctionPluginOnlineParam.class);
        try {
            hostApplicationManager.applyOfflinePlugin(param.getFunctionId());
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> pluginOnline(JsonObject data) {
        FunctionPluginOfflineParam param = data.mapTo(FunctionPluginOfflineParam.class);
        try {
            hostApplicationManager.applyOnlinePlugin(param.getFunctionId());
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
        return Future.succeededFuture();
    }

    @Override
    public Future<JsonObject> queryHostPluginInfo(JsonObject data) {
        // 查询代码信息
        return null;
    }

    @Override
    public Future<JsonObject> queryFunctionVersion(JsonObject data) {
        QueryFunctionInfo queryFunctionInfo = data.mapTo(QueryFunctionInfo.class);
        AreaDomainFunctionInfo functionInfo = hostApplicationManager.queryFunctionInfo(queryFunctionInfo.getTaskComponent(), queryFunctionInfo.getTaskService(), queryFunctionInfo.getDomainFunctionVersionId());
        return Future.succeededFuture(JsonObject.mapFrom(functionInfo));
    }

    @Override
    public Future<JsonObject> queryDeployInfo(JsonObject data) {
        List<SpiderPluginDeployInfo> deployInfos = hostApplicationManager.queryDeployInfo(data.getString("domainFunctionVersionId"));
        return Future.succeededFuture(JsonObject.mapFrom(new QueryDeployInfoResult(deployInfos)));
    }

    @Override
    public Future<Void> checkDeployInfo(JsonObject data) {
        Promise<Void> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            try {
                CheckDeployParam param = data.mapTo(CheckDeployParam.class);
                hostApplicationManager.checkDeployInfo(param);
                promise.complete();
            } catch (Exception e) {
                log.error("check部署失败 {}", ExceptionMessage.getStackTrace(e));
                promise.fail(e);
            }
        });

        return promise.future();
    }
}
