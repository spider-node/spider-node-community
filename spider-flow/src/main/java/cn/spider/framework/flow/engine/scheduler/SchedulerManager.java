package cn.spider.framework.flow.engine.scheduler;

import cn.spider.framework.annotation.TaskService;
import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.EndElementExampleData;
import cn.spider.framework.common.event.enums.ElementStatus;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.flow.bpmn.ServiceTask;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.exception.KstryException;
import cn.spider.framework.linker.sdk.data.*;
import cn.spider.framework.linker.sdk.interfaces.LinkerService;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    public void invoke(Method method, Map<String, Object> paramMap, ServiceTask serviceTask) throws InstantiationException, IllegalAccessException {
        Promise<Object> promise = serviceTask.getPromise();
        if (!workerMap.containsKey(serviceTask.getTaskComponent())) {
            throw new KstryException(ExceptionEnum.TASK_SERVICE_MATCH_ERROR);
        }

        EndElementExampleData elementExampleData = EndElementExampleData.builder()
                .requestParam(JSON.toJSONString(paramMap))
                .requestId(serviceTask.getRequestId())
                .flowElementId(serviceTask.getId())
                .status(ElementStatus.SUSS)
                .returnClassType(method.getReturnType().getTypeName())
                .build();

        // 因为异步，直接告诉流程，可以进行下一步操作
        if (serviceTask.queryIsAsync()) {
            eventManager.sendMessage(EventType.ELEMENT_END, elementExampleData);
            promise.complete();
        }

        LinkerServerRequest linkerServerRequest = buildRequestEntity(paramMap, method, serviceTask, workerMap.get(serviceTask.getTaskComponent()));
        JsonObject request = JsonObject.mapFrom(linkerServerRequest);
        Future<JsonObject> result = linkerService.submittals(request);
        result.onSuccess(suss -> {
            LinkerServerResponse linkerServerResponse = JSON.parseObject(suss.getJsonObject("data").toString(), LinkerServerResponse.class);
            // 校验返回的code
            if (linkerServerResponse.getResultCode().equals(ResultCode.SUSS)) {
                Object resultObject = null;
                Boolean resultConvert = true;
                try {
                    resultObject = JSON.parseObject(linkerServerResponse.getResultData().toString(), method.getReturnType());
                    elementExampleData.setReturnParam(resultObject);
                } catch (Exception e) {
                    resultConvert = false;
                    elementExampleData.setException(ExceptionMessage.getStackTrace(e));
                }
                if (!serviceTask.queryIsAsync()) {
                    if(!resultConvert){
                        log.error("转换失败的数据为 {} return对象为 {}",linkerServerResponse.getResultData().toString(),method.getReturnType());
                        promise.fail("转换参数失败");
                    }else {
                        promise.complete(resultObject);
                    }
                    eventManager.sendMessage(EventType.ELEMENT_END, elementExampleData);
                }
            } else {
                elementExampleData.setStatus(ElementStatus.FAIL);
                elementExampleData.setException(linkerServerResponse.getExceptional());
                if (!serviceTask.queryIsAsync()) {
                    log.error("执行失败原因为 {}",linkerServerResponse.getExceptional());
                    promise.fail(new Exception(linkerServerResponse.getExceptional()));
                    eventManager.sendMessage(EventType.ELEMENT_END, elementExampleData);
                }
            }

        }).onFailure(fail -> {
            // 通知失败
            if (!serviceTask.queryIsAsync()) {
                promise.fail(fail);
                log.error("执行失败原因为 {}",ExceptionMessage.getStackTrace(fail));
                elementExampleData.setStatus(ElementStatus.FAIL);
                elementExampleData.setException(ExceptionMessage.getStackTrace(fail));
                // 发送执行失败的数据
                eventManager.sendMessage(EventType.ELEMENT_END, elementExampleData);
            }
        });
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

    public String queryWorkerName(String componentName) {
        return this.workerMap.get(componentName);
    }


}
