package cn.spider.node.host.plugin.center.impl;

import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.BizHostOfflineData;
import cn.spider.framework.common.event.data.DeleteDeployData;
import cn.spider.framework.common.event.data.FunctionDeployData;
import cn.spider.framework.common.event.data.ScaleUpData;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.linker.sdk.data.QueryTaskDeployParam;
import cn.spider.framework.linker.sdk.data.QueryTaskDeployResult;
import cn.spider.framework.linker.sdk.interfaces.LinkerService;
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
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
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

    @Autowired
    private LinkerService linkerService;

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
        Promise<Void> promise = Promise.promise();
        try {
            PluginOfflineParam pluginOfflineParam = data.mapTo(PluginOfflineParam.class);
            AreaDomainFunctionInfo areaDomainFunctionInfo = areaDomainFunctionInfoService
                    .lambdaQuery()
                    .eq(AreaDomainFunctionInfo::getDomainFunctionVersionId, pluginOfflineParam.getAreaVersionId())
                    .one();
            // 发送事件,删除k8s中的deployments-顺带会删除pod
            DeleteDeployData hostApplicationOnlineData = new DeleteDeployData(areaDomainFunctionInfo.getBizName(), areaDomainFunctionInfo.getDomainFunctionVersionId());
            eventManager.sendMessage(EventType.SCALE_DOWN, hostApplicationOnlineData);
            promise.complete();
           /* // 查询该版部署的ip,
            QueryTaskDeployParam queryTaskDeployParam = new QueryTaskDeployParam();
            queryTaskDeployParam.setTaskComponent(areaDomainFunctionInfo.getTaskComponent());
            queryTaskDeployParam.setTaskService(areaDomainFunctionInfo.getTaskService());
            queryTaskDeployParam.setVersion(areaDomainFunctionInfo.getVersion());
            linkerService.queryTaskDeploy(JsonObject.mapFrom(queryTaskDeployParam)).onSuccess(result -> {
                QueryTaskDeployResult queryTaskDeployResult = JSON.parseObject(result.toString(), QueryTaskDeployResult.class);
                if (Objects.isNull(queryTaskDeployResult) || CollectionUtils.isEmpty(queryTaskDeployResult.getIps())) {
                    promise.complete();
                    return;
                }
                for (String ip : queryTaskDeployResult.getIps()) {
                    // 通知基于ip进行下线
                    BizHostOfflineData bizHostOfflineData = new BizHostOfflineData(areaDomainFunctionInfo.getBizName(), areaDomainFunctionInfo.getBizVersion(), ip);
                    eventManager.sendMessage(EventType.SCALE_DOWN_NOTIFY_HOST, bizHostOfflineData);
                }
                promise.complete();
                // 发事件进行通知 对应ip中,需要执行那些model的下线
            }).onFailure(e -> {
                log.error("功能下线失败 {}", ExceptionMessage.getStackTrace(e));
                promise.fail(e);
            });*/
            // 循环进行调用接口卸载
        } catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
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

    /**
     * 需要升本版后,进行扩缩容
     *
     * @param data 宿主应用信息 扩缩容
     * @return
     */
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
            if (Objects.isNull(areaDomainFunctionInfo)) {
                promise.complete(new JsonObject());
                return;
            }
            QueryFunctionVersionResult queryFunctionVersionResult = new QueryFunctionVersionResult();
            queryFunctionVersionResult.setAreaFunctionResultClass(areaDomainFunctionInfo.getAreaFunctionResultClass());
            queryFunctionVersionResult.setAreaFunctionParamClass(areaDomainFunctionInfo.getAreaFunctionParamClass());
            queryFunctionVersionResult.setServiceName(areaDomainFunctionInfo.getFunctionName());
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

    @Override
    public Future<JsonArray> queryInputParam(JsonObject data) {
        QueryInputParam queryInputParam = data.mapTo(QueryInputParam.class);
        List<AreaDomainFunctionInfo> areaDomainFunctionInfos = areaDomainFunctionInfoService
                .lambdaQuery()
                .in(AreaDomainFunctionInfo::getDomainFunctionVersionId, queryInputParam.getDomainFunctionVersionId())
                .list();
        JsonArray result = new JsonArray();
        areaDomainFunctionInfos.forEach(areaDomainFunctionInfo -> {
            QueryDomainFunctionClassInfo queryDomainFunctionClassInfo = new QueryDomainFunctionClassInfo(areaDomainFunctionInfo.getDomainFunctionVersionId(),
                    areaDomainFunctionInfo.getAreaFunctionResultClass(), areaDomainFunctionInfo.getAreaFunctionParamClass(),
                    areaDomainFunctionInfo.getOtherCode());
            result.add(JsonObject.mapFrom(queryDomainFunctionClassInfo));
        });
        return Future.succeededFuture(result);
    }
}
