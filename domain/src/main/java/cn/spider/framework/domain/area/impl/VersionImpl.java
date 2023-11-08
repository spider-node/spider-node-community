package cn.spider.framework.domain.area.impl;

import cn.spider.framework.domain.area.function.version.VersionManager;
import cn.spider.framework.domain.area.function.version.data.FunctionVersionModel;
import cn.spider.framework.domain.area.function.version.data.QueryVersionFunctionParam;
import cn.spider.framework.domain.sdk.data.UploadBpmnParam;
import cn.spider.framework.domain.sdk.interfaces.VersionInterface;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

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

    @Override
    public Future<Void> insertVersion(JsonObject data) {
        return versionManager.createFunctionVersion(data.mapTo(FunctionVersionModel.class));
    }

    @Override
    public Future<Void> updateVersion(JsonObject data) {
        return versionManager.updateFunctionVersion(data.mapTo(FunctionVersionModel.class));
    }

    @Override
    public Future<Void> refreshVersion(JsonObject data) {
        return versionManager.refreshBpmn(data.mapTo(UploadBpmnParam.class));
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
}
