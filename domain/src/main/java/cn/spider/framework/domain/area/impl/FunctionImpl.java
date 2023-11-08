package cn.spider.framework.domain.area.impl;

import cn.spider.framework.domain.area.function.FunctionManger;
import cn.spider.framework.domain.area.function.data.*;
import cn.spider.framework.domain.sdk.interfaces.FunctionInterface;
import com.google.common.collect.Lists;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.impl
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-26  23:52
 * @Description: 功能的实现类
 * @Version: 1.0
 */

public class FunctionImpl implements FunctionInterface {


    private FunctionManger functionManger;

    public FunctionImpl(FunctionManger functionManger) {
        this.functionManger = functionManger;
    }

    /**
     * 新增功能
     *
     * @param data
     * @return
     */
    @Override
    public Future<Void> insertFunction(JsonObject data) {
        FunctionModel functionModel = data.mapTo(FunctionModel.class);
        return functionManger.increaseFunctionManger(functionModel);
    }

    @Override
    public Future<Void> updateFunction(JsonObject data) {
        FunctionModel functionModel = data.mapTo(FunctionModel.class);
        return functionManger.updateFunctionManger(functionModel);
    }

    @Override
    public Future<JsonObject> queryFunction(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        QueryFunctionParam param = data.mapTo(QueryFunctionParam.class);
        functionManger.selectFunction(param).onSuccess(suss -> {
            List<FunctionModel> functionModels = suss;
            List<JsonObject> functions = Lists.newArrayList();
            functionModels.forEach(item -> {
                functions.add(JsonObject.mapFrom(item));
            });
            JsonObject result = new JsonObject();
            result.put("functions", functions);
            promise.complete(result);
        }).onFailure(fail -> {
            promise.fail(fail);
        });
        return promise.future();
    }

    @Override
    public Future<JsonObject> findExecuteFunction(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        // 查询当前执行的版本
        QueryExecuteFunctionInfo param = data.mapTo(QueryExecuteFunctionInfo.class);
        functionManger.queryFunctionInfo(param).onSuccess(suss -> {
            ExecuteFunctionInfo functionInfo = suss;
            promise.complete(JsonObject.mapFrom(functionInfo));
        }).onFailure(fail -> {
            promise.fail(fail);
        });
        return promise.future();
    }

    @Override
    public Future<Void> startStopFunction(JsonObject model) {
        FunctionStartStopModel functionStartStopModel = model.mapTo(FunctionStartStopModel.class);
        return functionManger.startStopFunction(functionStartStopModel);
    }
}
