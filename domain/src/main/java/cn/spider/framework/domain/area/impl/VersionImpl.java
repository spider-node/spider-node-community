package cn.spider.framework.domain.area.impl;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.domain.area.function.entity.SpiderBusinessFunctionVersion;
import cn.spider.framework.domain.area.function.version.data.QueryFunctionVersionResult;
import cn.spider.framework.domain.sdk.data.QueryBpmnUrlResult;
import cn.spider.framework.domain.area.function.version.VersionManager;
import cn.spider.framework.domain.area.function.version.data.FunctionVersionModel;
import cn.spider.framework.domain.area.function.version.data.QueryVersionFunctionParam;
import cn.spider.framework.domain.area.function.version.data.VersionStopStartParam;
import cn.spider.framework.domain.sdk.data.RefreshBpmnParam;
import cn.spider.framework.domain.sdk.interfaces.VersionInterface;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.impl
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-26  23:52
 * @Description: 版本的管理实现类
 * @Version: 1.0
 */
@Slf4j
public class VersionImpl implements VersionInterface {

    private VersionManager versionManager;

    private Executor spiderBusinessPool;

    public VersionImpl(VersionManager versionManager, Executor spiderBusinessPool) {
        this.versionManager = versionManager;
        this.spiderBusinessPool = spiderBusinessPool;
    }

    @Override
    public Future<Void> insertVersion(JsonObject data) {
        return versionManager.createFunctionVersion(JSON.parseObject(data.toString(), FunctionVersionModel.class));
    }

    @Override
    public Future<Void> insertVersionV2(JsonObject data) {
        Promise<Void> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            try {
                FunctionVersionModel functionVersionModel = JSON.parseObject(data.toString(), FunctionVersionModel.class);
                SpiderBusinessFunctionVersion functionVersion = new SpiderBusinessFunctionVersion();
                BeanUtils.copyProperties(functionVersion, functionVersionModel);
                versionManager.addVersion(functionVersion);
                promise.complete();
            } catch (Exception e) {
                log.error("新增版本失败 {}", ExceptionMessage.getStackTrace(e));
                promise.fail(e);
            }
        });
        return promise.future();
    }

    @Override
    public Future<Void> updateVersion(JsonObject data) {
        return versionManager.updateFunctionVersion(JSON.parseObject(data.toString(), FunctionVersionModel.class));
    }

    @Override
    public Future<Void> startOrStopVersion(JsonObject data) {
        VersionStopStartParam versionStopStartParam = JSON.parseObject(data.toString(), VersionStopStartParam.class);
        return versionManager.startStop(versionStopStartParam);
    }

    @Override
    public Future<Void> refreshVersion(JsonObject data) {
        return versionManager.refreshBpmn(data.mapTo(RefreshBpmnParam.class));
    }

    @Override
    public Future<JsonObject> queryVersion(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        versionManager.selectVersion(data.mapTo(QueryVersionFunctionParam.class)).onSuccess(suss -> {
            promise.complete(new JsonObject().put("versions", new JsonArray(JSON.toJSONString(suss))));
        }).onFailure(fail -> {
            promise.fail(fail);
        });
        return promise.future();
    }

    @Override
    public Future<JsonObject> queryBpmnUrl() {
        Promise<JsonObject> promise = Promise.promise();
        versionManager.getBpmnUrl().onSuccess(suss -> {
            Set<String> bpmnUrls = suss;
            QueryBpmnUrlResult queryBpmnUrlResult = new QueryBpmnUrlResult();
            queryBpmnUrlResult.setBpmnUrls(bpmnUrls);
            promise.complete(JsonObject.mapFrom(queryBpmnUrlResult));
        }).onFailure(fail -> {
            promise.fail(fail);
        });
        return promise.future();
    }

    @Override
    public Future<JsonObject> queryVersionByFunctionId(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            try {
                QueryFunctionVersionResult queryFunctionVersionResult = versionManager.queryVersion(data.mapTo(QueryVersionFunctionParam.class));
                promise.complete(JsonObject.mapFrom(queryFunctionVersionResult));
            } catch (Exception e) {
                log.error("queryFunctionVersionError {}", ExceptionMessage.getStackTrace(e));
                promise.fail(e);
            }
        });
        return promise.future();
    }


}
