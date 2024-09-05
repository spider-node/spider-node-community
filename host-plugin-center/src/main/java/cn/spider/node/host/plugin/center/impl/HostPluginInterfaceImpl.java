package cn.spider.node.host.plugin.center.impl;

import cn.spider.node.host.plugin.center.application.HostApplicationManager;
import cn.spider.node.host.plugin.center.model.entity.AreaDomainFunctionInfo;
import cn.spider.node.host.plugin.center.sdk.data.*;
import cn.spider.node.host.plugin.center.sdk.interfaces.HostPluginInterface;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class HostPluginInterfaceImpl implements HostPluginInterface {

    @Resource
    private HostApplicationManager hostApplicationManager;

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

        return null;
    }

    @Override
    public Future<JsonObject> queryFunctionVersion(JsonObject data) {
        QueryFunctionInfo queryFunctionInfo = new QueryFunctionInfo();
        AreaDomainFunctionInfo functionInfo = hostApplicationManager.queryFunctionInfo(queryFunctionInfo.getTaskComponent(), queryFunctionInfo.getTaskService());
        return Future.succeededFuture(JsonObject.mapFrom(functionInfo));
    }
}
