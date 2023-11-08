package cn.spider.framework.flow.business;

import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.StopFunctionData;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.container.sdk.data.SelectFunctionResponse;
import cn.spider.framework.container.sdk.interfaces.BusinessService;
import cn.spider.framework.flow.business.data.*;
import cn.spider.framework.flow.business.enums.FunctionStatus;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.business
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-12  14:23
 * @Description: 设置业务参数
 * @Version: 1.0
 */
@Slf4j
@Component
public class BusinessServiceImpl implements BusinessService {

    @Resource
    private BusinessManager businessManager;

    @Resource
    private EventManager eventManager;


    @Override
    public Future<JsonObject> registerFunction(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        BusinessFunctions businessFunctions = data.mapTo(BusinessFunctions.class);
        if (!checkRegisterFunction(businessFunctions)) {
            return Future.failedFuture(new Throwable("字段信息不完善"));
        }
        businessFunctions.setCreateTime(LocalDateTime.now());
        Future<String> functionFuture = businessManager.registerBusinessFunction(businessFunctions);
        functionFuture.onSuccess(suss -> {
            promise.complete(new JsonObject().put("functionId", suss));
        }).onFailure(fail -> {
            promise.fail(fail);
        });
        return promise.future();
    }


    // check
    public Boolean checkRegisterFunction(BusinessFunctions businessFunctions) {
        if (StringUtils.isEmpty(businessFunctions.getName())) {
            return false;
        }
        if (StringUtils.isEmpty(businessFunctions.getVersion())) {
            return false;
        }

        if (StringUtils.isEmpty(businessFunctions.getStartId())) {
            return false;
        }

        if (StringUtils.isEmpty(businessFunctions.getBpmnName())) {
            return false;
        }
        return true;
    }

    @Override
    public Future<JsonObject> selectFunction(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        Future<List<BusinessFunctions>> future = businessManager.queryBusinessFunctions(data.getInteger("page"), data.getInteger("size"));
        future.onSuccess(suss -> {
            List<BusinessFunctions> businessFunctions = suss;
            SelectFunctionResponse response = new SelectFunctionResponse();
            List<JsonObject> functionJson = businessFunctions.stream().map(item -> JsonObject.mapFrom(item)).collect(Collectors.toList());
            response.setFunctions(functionJson);
            promise.complete(JsonObject.mapFrom(response));
        }).onFailure(fail -> {
            promise.fail(fail);
        });
        return promise.future();
    }

    @Override
    public Future<Void> configureDerail(JsonObject data) {
        try {
            DerailFunctionVersion derailFunctionVersion = data.mapTo(DerailFunctionVersion.class);
            businessManager.derailFunctionVersion(derailFunctionVersion);
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> configureWeight(JsonObject data) {
        try {
            FunctionWeight functionWeight = data.mapTo(FunctionWeight.class);
            businessManager.functionWeightConfig(functionWeight);
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> deleteFunction(JsonObject data) {
        DeleteBusinessFunctionRequest request = data.mapTo(DeleteBusinessFunctionRequest.class);
        businessManager.deleteFunction(request.getFunctionId());
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> stateChange(JsonObject data) {
        try {
            FunctionStateChangeRequest request = data.mapTo(FunctionStateChangeRequest.class);
            businessManager.updateStatus(request.getFunctionId(), request.getStatus());
            if(request.getStatus().equals(FunctionStatus.STOP)){
                // 发送停止事件
                eventManager.sendMessage(EventType.STOP_FUNCTION, StopFunctionData.builder().functionId(request.getFunctionId()).build());
            }
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> deleteAll() {
        try {
            businessManager.deleteAll();
        } catch (Exception e) {
            log.error("deleteAll fail {}", ExceptionMessage.getStackTrace(e));
            return Future.failedFuture(e);
        }
        return Future.succeededFuture();
    }
}
