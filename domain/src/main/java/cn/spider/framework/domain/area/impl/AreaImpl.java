package cn.spider.framework.domain.area.impl;

import cn.spider.framework.domain.area.AreaManger;
import cn.spider.framework.domain.area.data.AreaModel;
import cn.spider.framework.domain.area.data.QueryAreaModel;
import cn.spider.framework.domain.sdk.data.RefreshSdkParam;
import cn.spider.framework.domain.sdk.data.SdkInfo;
import cn.spider.framework.domain.sdk.data.SdkUrlQueryResult;
import cn.spider.framework.domain.sdk.data.UploadSdkParam;
import cn.spider.framework.domain.sdk.interfaces.AreaInterface;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Set;

public class AreaImpl implements AreaInterface {

    private AreaManger areaManger;

    public AreaImpl(AreaManger areaManger) {
        this.areaManger = areaManger;
    }

    /**
     * 更新sdk的地址以及加载信息
     *
     * @param data
     * @return
     */
    @Override
    public Future<Void> updateSdk(JsonObject data) {
        UploadSdkParam uploadSdkParam = data.mapTo(UploadSdkParam.class);
        return areaManger.uploadSdk(uploadSdkParam);
    }

    /**
     * 刷新sdk-在spider中的class信息
     *
     * @param data
     * @return
     */
    @Override
    public Future<Void> refreshSdk(JsonObject data) {
        RefreshSdkParam refreshSdkParam = data.mapTo(RefreshSdkParam.class);
        return areaManger.refreshSdk(refreshSdkParam);
    }

    /**
     * 插入域信息
     *
     * @param data
     * @return
     */
    @Override
    public Future<Void> insertArea(JsonObject data) {
        return areaManger.createArea(JSON.parseObject(data.toString(),AreaModel.class));
    }

    @Override
    public Future<Void> updateArea(JsonObject data) {
        return areaManger.updateArea(data.mapTo(AreaModel.class));
    }

    @Override
    public Future<JsonObject> queryArea(JsonObject data) {
        Promise<JsonObject> result = Promise.promise();
        areaManger.queryAreaModel(data.mapTo(QueryAreaModel.class)).onSuccess(suss -> {
            List<AreaModel> areaModels = suss;
            result.complete(new JsonObject().put("areaModes", new JsonArray(JSON.toJSONString(areaModels))));
        }).onFailure(fail -> {
            result.fail(fail);
        });
        return result.future();
    }

    @Override
    public Future<JsonObject> queryAreaSdk() {
        Promise<JsonObject> promise = Promise.promise();
        Future<Set<SdkInfo>> sdkUrlFuture = areaManger.querySdkUrl();
        sdkUrlFuture.onSuccess(suss->{
            SdkUrlQueryResult sdkUrlQueryResult = new SdkUrlQueryResult();
            sdkUrlQueryResult.setSdkInfos(Lists.newArrayList(suss));
            promise.complete(JsonObject.mapFrom(sdkUrlQueryResult));
        }).onFailure(fail->{
            promise.fail(fail);
        });
        return promise.future();
    }
}
