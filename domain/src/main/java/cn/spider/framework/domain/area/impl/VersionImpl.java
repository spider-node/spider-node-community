package cn.spider.framework.domain.area.impl;

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

import java.util.Set;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.impl
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-26  23:52
 * @Description: 版本的管理实现类
 * @Version: 1.0
 */
public class VersionImpl implements VersionInterface {

    private VersionManager versionManager;

    public VersionImpl(VersionManager versionManager) {
        this.versionManager = versionManager;
    }

    @Override
    public Future<Void> insertVersion(JsonObject data) {
        return versionManager.createFunctionVersion(JSON.parseObject(data.toString(),FunctionVersionModel.class));
    }

    @Override
    public Future<Void> updateVersion(JsonObject data) {
        return versionManager.updateFunctionVersion(JSON.parseObject(data.toString(),FunctionVersionModel.class));
    }

    @Override
    public Future<Void> startOrStopVersion(JsonObject data) {
        VersionStopStartParam versionStopStartParam = JSON.parseObject(data.toString(),VersionStopStartParam.class);
       return versionManager.startStop(versionStopStartParam);
    }

    @Override
    public Future<Void> refreshVersion(JsonObject data) {
        return versionManager.refreshBpmn(data.mapTo(RefreshBpmnParam.class));
    }

    @Override
    public Future<JsonObject> queryVersion(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        versionManager.selectVersion(data.mapTo(QueryVersionFunctionParam.class)).onSuccess(suss->{
            promise.complete(new JsonObject().put("versions",new JsonArray(JSON.toJSONString(suss))));
        }).onFailure(fail->{
            promise.fail(fail);
        });
        return promise.future();
    }

    @Override
    public Future<JsonObject> queryBpmnUrl() {
        Promise<JsonObject> promise = Promise.promise();
        versionManager.getBpmnUrl().onSuccess(suss ->{
            Set<String> bpmnUrls = suss;
            QueryBpmnUrlResult queryBpmnUrlResult = new QueryBpmnUrlResult();
            queryBpmnUrlResult.setBpmnUrls(bpmnUrls);
            promise.complete(JsonObject.mapFrom(queryBpmnUrlResult));
        }).onFailure(fail->{
            promise.fail(fail);
        });
        return promise.future();
    }

    @Override
    public Future<JsonObject> queryVersionByFunctionId(JsonObject data) {
        return null;
    }


}
