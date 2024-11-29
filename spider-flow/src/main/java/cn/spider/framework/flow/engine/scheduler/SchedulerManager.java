package cn.spider.framework.flow.engine.scheduler;

import cn.spider.framework.annotation.TaskService;
import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.EndElementExampleData;
import cn.spider.framework.common.event.enums.ElementStatus;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.container.sdk.data.SimpleStartResult;
import cn.spider.framework.flow.bpmn.ServiceTask;
import cn.spider.framework.flow.engine.example.data.FlowExample;
import cn.spider.framework.linker.sdk.data.*;
import cn.spider.framework.linker.sdk.interfaces.LinkerService;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.engine.scheduler
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-31  18:29
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
public class SchedulerManager {
    /**
     * 跟服务段交互service
     */
    private LinkerService linkerService;
    /**
     * 目标对象与组件的映射关心
     */
    private Map<String, String> workerMap;

    private EventManager eventManager;

    public void addClass(String component, String workerName) {
        workerMap.put(component, workerName);
    }

    public void remove(Object target) {

    }

    public SchedulerManager(LinkerService linkerService, EventManager eventManager) {
        this.linkerService = linkerService;
        this.workerMap = new HashMap<>();
        this.eventManager = eventManager;

    }

    public void invokeNew(Map<String, Object> paramMap, ServiceTask serviceTask, String workerName, String methodName, FlowExample example, Promise<Object> promise, String requestId) {
        EndElementExampleData elementExampleData = EndElementExampleData.builder()
                .requestParam(JSON.toJSONString(paramMap))
                .requestId(requestId)
                .flowElementId(serviceTask.getId())
                .status(ElementStatus.SUSS)
                .build();
        // 因为异步，直接告诉流程，可以进行下一步操作
        if (serviceTask.queryIsAsync()) {
            eventManager.sendMessage(EventType.ELEMENT_END, elementExampleData);
            promise.complete();
        }
        LinkerServerRequest linkerServerRequest = buildRequestEntityNew(paramMap, serviceTask, workerName, methodName, example);
        JsonObject request = JsonObject.mapFrom(linkerServerRequest);
        // 提交执行请求
        Future<JsonObject> result = linkerService.submittals(request);
        result.onSuccess(suss -> {
            LinkerServerResponse linkerServerResponse = JSON.parseObject(suss.getJsonObject(Constant.DATA).toString(), LinkerServerResponse.class);
            // 校验返回的code
            if (linkerServerResponse.getResultCode().equals(ResultCode.SUSS)) {
                JsonObject resultObject = new JsonObject(linkerServerResponse.getResultData().toString());
                if (!serviceTask.queryIsAsync()) {
                    elementExampleData.setReturnParam(resultObject);
                    eventManager.sendMessage(EventType.ELEMENT_END, elementExampleData);
                    promise.complete(resultObject);
                }
            } else {
                if (!serviceTask.queryIsAsync()) {
                    log.error("执行失败原因为 {}", linkerServerResponse.getExceptional());
                    elementExampleData.setStatus(ElementStatus.FAIL);
                    elementExampleData.setException(linkerServerResponse.getExceptional());
                    eventManager.sendMessage(EventType.ELEMENT_END, elementExampleData);
                    promise.fail(new Exception(linkerServerResponse.getExceptional()));
                }
            }

        }).onFailure(fail -> {
            // 通知失败
            if (!serviceTask.queryIsAsync()) {
                promise.fail(fail);
                log.error("执行失败原因为 {}", ExceptionMessage.getStackTrace(fail));
                elementExampleData.setStatus(ElementStatus.FAIL);
                elementExampleData.setException(ExceptionMessage.getStackTrace(fail));
                // 发送执行失败的数据
                eventManager.sendMessage(EventType.ELEMENT_END, elementExampleData);
            }
        });
    }



    public Future<JsonObject> simpleInvoke(Map<String, Object> paramMap, String workerName, String method, String taskComponent, String taskService, String version) {
        Promise<JsonObject> promise = Promise.promise();
        LinkerServerRequest linkerServerRequest = simpleBuildRequestEntity(paramMap, workerName, method, taskComponent, taskService, version);
        JsonObject request = JsonObject.mapFrom(linkerServerRequest);
        // 提交执行请求
        Future<JsonObject> result = linkerService.submittals(request);
        result.onSuccess(suss->{
            SimpleStartResult runResult = new SimpleStartResult();
            LinkerServerResponse linkerServerResponse = JSON.parseObject(suss.getJsonObject(Constant.DATA).toString(), LinkerServerResponse.class);
            if (linkerServerResponse.getResultCode().equals(ResultCode.SUSS)) {
                JsonObject resultObject = new JsonObject(linkerServerResponse.getResultData().toString());
                runResult.setRunStatus(Boolean.TRUE);
                runResult.setResultObject(resultObject);

            } else {
                runResult.setError(linkerServerResponse.getExceptional());
                runResult.setRunStatus(Boolean.FALSE);
            }
            promise.complete(JsonObject.mapFrom(runResult));
        }).onFailure(fail->{
            promise.fail(fail);
        });
        return promise.future();
    }

    private LinkerServerRequest buildRequestEntity(Map<String, Object> paramMap, Method method, ServiceTask serviceTask, String workerName) {
        // 参数中，移除末尾的 Promise<Object> promise
        String componentName = serviceTask.getTaskComponent();
        LinkerServerRequest linkerServerRequest = new LinkerServerRequest();
        FunctionRequest functionRequest = new FunctionRequest();
        functionRequest.setComponentName(componentName);
        functionRequest.setMethodName(method.getName());
        TaskService annotation = method.getAnnotation(TaskService.class);
        functionRequest.setServiceName(annotation.name());
        functionRequest.setWorkerName(workerName);
        functionRequest.setParam(paramMap);
        functionRequest.setXid(serviceTask.getXid());
        functionRequest.setBranchId(serviceTask.getBranchId());
        linkerServerRequest.setExecutionType(ExecutionType.FUNCTION);
        linkerServerRequest.setFunctionRequest(functionRequest);
        return linkerServerRequest;
    }


    private LinkerServerRequest buildRequestEntityNew(Map<String, Object> paramMap, ServiceTask serviceTask, String workerName, String method, FlowExample example) {
        // 参数中，移除末尾的 Promise<Object> promise
        String componentName = serviceTask.getTaskComponent();
        LinkerServerRequest linkerServerRequest = new LinkerServerRequest();
        FunctionRequest functionRequest = new FunctionRequest();
        functionRequest.setComponentName(componentName);
        functionRequest.setMethodName(method);
        functionRequest.setServiceName(serviceTask.getTaskService());
        functionRequest.setWorkerName(workerName);
        functionRequest.setParam(paramMap);
        functionRequest.setXid(serviceTask.getXid());
        functionRequest.setBranchId(serviceTask.getBranchId());
        functionRequest.setVersion(serviceTask.getVersion());
        functionRequest.setProviderType(serviceTask.queryProviderType());
        linkerServerRequest.setExecutionType(ExecutionType.FUNCTION);
        linkerServerRequest.setFunctionRequest(functionRequest);
        linkerServerRequest.setParentRequestId(example.getParentRequestId());
        linkerServerRequest.setRetryNodeId(example.getRetryNodeId());
        linkerServerRequest.setNowNodeId(serviceTask.getId());
        linkerServerRequest.setRetryType(example.getRunType());
        return linkerServerRequest;
    }

    private LinkerServerRequest simpleBuildRequestEntity(Map<String, Object> paramMap, String workerName, String method, String taskComponent, String taskService, String version) {
        LinkerServerRequest linkerServerRequest = new LinkerServerRequest();
        FunctionRequest functionRequest = new FunctionRequest();
        functionRequest.setComponentName(taskComponent);
        functionRequest.setMethodName(method);
        functionRequest.setServiceName(taskService);
        functionRequest.setWorkerName(workerName);
        functionRequest.setParam(paramMap);
        functionRequest.setVersion(version);
        functionRequest.setProviderType(ApplicationProviderType.SPIDER_HOST_APPLICATION);
        linkerServerRequest.setExecutionType(ExecutionType.FUNCTION);
        linkerServerRequest.setFunctionRequest(functionRequest);
        linkerServerRequest.setRetryType("ACTUAL");
        return linkerServerRequest;

    }

    public String queryWorkerName(String componentName) {
        return this.workerMap.get(componentName);
    }


}
