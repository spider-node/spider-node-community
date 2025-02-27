package cn.spider.node.host.plugin.center.impl;

import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.DeleteDeployData;
import cn.spider.framework.common.event.data.FunctionDeployData;
import cn.spider.framework.common.event.data.ScaleUpData;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.node.host.plugin.center.application.HostApplicationManager;
import cn.spider.node.host.plugin.center.model.data.QueryDeployInfoResult;
import cn.spider.node.host.plugin.center.model.entity.AreaDomainFunctionInfo;
import cn.spider.node.host.plugin.center.model.entity.SpiderPluginDeployInfo;
import cn.spider.node.host.plugin.center.model.service.IAreaDomainFunctionInfoService;
import cn.spider.node.host.plugin.center.sdk.data.*;
import cn.spider.node.host.plugin.center.sdk.interfaces.HostPluginInterface;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
@Component
public class HostPluginInterfaceImpl implements HostPluginInterface {

    @Autowired
    private HostApplicationManager hostApplicationManager;

    @Autowired
    private Executor spiderBusinessPool;

    @Autowired
    private EventManager eventManager;

    @Autowired
    private IAreaDomainFunctionInfoService areaDomainFunctionInfoService;

    @Override
    public Future<Void> hostOnline(JsonObject data) {
        HostOnlineParam hostOnlineParam = data.mapTo(HostOnlineParam.class);
        try {
           // hostApplicationManager.online(hostOnlineParam.getIp());
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> hostOffline(JsonObject data) {
        HostOfflineParam hostOfflineParam = data.mapTo(HostOfflineParam.class);
        try {
            //hostApplicationManager.offline(hostOfflineParam.getIp());
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> pluginOffline(JsonObject data) {
        PluginOfflineParam pluginOfflineParam = data.mapTo(PluginOfflineParam.class);
        try {
            AreaDomainFunctionInfo areaDomainFunctionInfo = areaDomainFunctionInfoService
                    .lambdaQuery()
                    .eq(AreaDomainFunctionInfo::getDomainFunctionVersionId, pluginOfflineParam.getAreaVersionId())
                    .one();
            String deploymentName = areaDomainFunctionInfo.getFunctionName() + "_" + areaDomainFunctionInfo.getVersion();
            DeleteDeployData hostApplicationOnlineData = new DeleteDeployData(deploymentName, areaDomainFunctionInfo.getDomainFunctionVersionId());
            eventManager.sendMessage(EventType.SCALE_DOWN, hostApplicationOnlineData);
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> pluginOnline(JsonObject data) {
        FunctionPluginOfflineParam param = data.mapTo(FunctionPluginOfflineParam.class);
        try {
            AreaDomainFunctionInfo areaDomainFunctionInfo = areaDomainFunctionInfoService.lambdaQuery().eq(AreaDomainFunctionInfo::getId, param.getFunctionId()).one();
            FunctionDeployData hostApplicationOnlineData = new FunctionDeployData(areaDomainFunctionInfo.getDeployYaml(), areaDomainFunctionInfo.getDomainFunctionVersionId());
            eventManager.sendMessage(EventType.DEPLOY, hostApplicationOnlineData);
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> scalePlugin(JsonObject data) {
        ScalePluginParam scalePluginParam = data.mapTo(ScalePluginParam.class);
        AreaDomainFunctionInfo areaDomainFunctionInfo = areaDomainFunctionInfoService.lambdaQuery().eq(AreaDomainFunctionInfo::getDomainFunctionVersionId, scalePluginParam.getFunctionId()).one();
        ScaleUpData hostApplicationOnlineData = new ScaleUpData(areaDomainFunctionInfo.getDeployYaml(), scalePluginParam.getNum(), areaDomainFunctionInfo.getDomainFunctionVersionId());
        eventManager.sendMessage(EventType.SCALE_UP, hostApplicationOnlineData);
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

    @Override
    public Future<JsonObject> queryVersionParam(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            QueryFunctionVersionsParam queryFunctionVersionParam = data.mapTo(QueryFunctionVersionsParam.class);
            AreaDomainFunctionInfo areaDomainFunctionInfo = areaDomainFunctionInfoService.lambdaQuery()
                    .eq(AreaDomainFunctionInfo::getDomainFunctionVersionId, queryFunctionVersionParam.getDomainFunctionVersionId()).one();
            QueryFunctionVersionResult queryFunctionVersionResult = new QueryFunctionVersionResult();
            queryFunctionVersionResult.setAreaFunctionResultClass(areaDomainFunctionInfo.getAreaFunctionResultClass());
            queryFunctionVersionResult.setAreaFunctionParamClass(areaDomainFunctionInfo.getAreaFunctionParamClass());
            log.info("==查询到的版本信息为 {}", JSON.toJSONString(queryFunctionVersionResult));
            promise.complete(JsonObject.mapFrom(queryFunctionVersionResult));
        });
        return promise.future();
    }

    @Override
    public Future<Void> updateFunctionCode(JsonObject data) {
        UpdateCoderParam updateCoderParam = data.mapTo(UpdateCoderParam.class);
        areaDomainFunctionInfoService.lambdaUpdate()
                .set(AreaDomainFunctionInfo::getAreaFunctionClass, updateCoderParam.getAreaFunctionClass())
                .set(AreaDomainFunctionInfo::getAreaFunctionParamClass, updateCoderParam.getAreaFunctionParamClass())
                .set(AreaDomainFunctionInfo::getAreaFunctionResultClass, updateCoderParam.getAreaFunctionResultClass())
                .eq(AreaDomainFunctionInfo::getId, updateCoderParam.getId())
                .update();
        return Future.succeededFuture();
    }
}
