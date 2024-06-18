package cn.spider.framework.domain.area.impl;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.domain.area.function.FunctionManger;
import cn.spider.framework.domain.area.function.data.*;
import cn.spider.framework.domain.sdk.data.FlowElementModel;
import cn.spider.framework.domain.sdk.data.FlowExampleModel;
import cn.spider.framework.domain.sdk.interfaces.FunctionInterface;
import cn.spider.framework.log.sdk.data.FlowExample;
import cn.spider.framework.log.sdk.data.QueryFlowElementExample;
import cn.spider.framework.log.sdk.data.QueryFlowElementExampleResponse;
import cn.spider.framework.log.sdk.data.QueryFlowExampleResponse;
import cn.spider.framework.log.sdk.interfaces.LogInterface;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private LogInterface logInterface;

    public FunctionImpl(FunctionManger functionManger,LogInterface logInterface) {
        this.functionManger = functionManger;
        this.logInterface = logInterface;
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

    // 根据requestId 获取 -- 日志整个链路的数据
    public Future<JsonObject> queryRunHistoryData(JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();
        logInterface.queryFlowExample(param)
                .onSuccess(example -> {
                    //获取实例
                    QueryFlowExampleResponse queryFlowExampleResponse = example.mapTo(QueryFlowExampleResponse.class);
                    if (CollectionUtils.isEmpty(queryFlowExampleResponse.getFlowExampleList())) {
                        promise.fail("没有找流程实例");
                        return;
                    }
                    FlowExampleModel flowExampleModel = new FlowExampleModel();
                    Optional<FlowExample> flowExampleOptional = queryFlowExampleResponse.getFlowExampleList().stream().filter(item -> StringUtils.isNotEmpty(item.getRequestParam())).findFirst();
                    FlowExample flowExample = flowExampleOptional.get();
                    flowExampleModel.setId(flowExample.getId());
                    flowExampleModel.setRequestParam(flowExample.getRequestParam());
                    flowExampleModel.setFunctionId(flowExample.getFunctionId());
                    flowExampleModel.setFunctionName(flowExampleModel.getFunctionName());
                    promise.complete(JsonObject.mapFrom(flowExampleModel));
                }).onFailure(fail -> {
                    promise.fail(fail);
                });
        return promise.future();
    }

    @Override
    public Future<JsonObject> queryRunHistoryElementData(JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();
        QueryFlowElementExample queryFlowElementExample = new QueryFlowElementExample();
        queryFlowElementExample.setSize(100);
        queryFlowElementExample.setPage(1);
        queryFlowElementExample.setRequestId(param.getString(Constant.REQUEST_ID));
        logInterface.queryElementExample(JsonObject.mapFrom(queryFlowElementExample))
                .onSuccess(elementExample -> {
                    QueryFlowElementExampleResponse response = elementExample.mapTo(QueryFlowElementExampleResponse.class);
                    if (CollectionUtils.isEmpty(response.getElementExampleList())) {
                        promise.fail("没有找到实例节点");
                        return;
                    }
                    FlowExampleModel flowExampleModel = new FlowExampleModel();
                    List<FlowElementModel> flowElementModels = response.getElementExampleList()
                            .stream()
                            .map(item -> {
                                FlowElementModel flowElementModel = new FlowElementModel();
                                BeanUtils.copyProperties(item, flowElementModel);
                                return flowElementModel;
                            }).collect(Collectors.toList());
                    flowExampleModel.setFlowElementModelList(flowElementModels);
                    promise.complete(JsonObject.mapFrom(flowExampleModel));
                }).onFailure(fail -> {
                    promise.fail(fail);
                });
        return promise.future();
    }
}
