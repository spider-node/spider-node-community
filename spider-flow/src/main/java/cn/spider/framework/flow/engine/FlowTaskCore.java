package cn.spider.framework.flow.engine;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.flow.SpiderCoreVerticle;
import cn.spider.framework.flow.bpmn.*;
import cn.spider.framework.flow.bpmn.enums.BpmnTypeEnum;
import cn.spider.framework.flow.bus.StoryBus;
import cn.spider.framework.flow.container.component.TaskServiceDef;
import cn.spider.framework.flow.engine.example.data.FlowExample;
import cn.spider.framework.flow.engine.scheduler.SchedulerManager;
import cn.spider.framework.flow.exception.BusinessException;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.exception.KstryException;
import cn.spider.framework.flow.role.Role;
import cn.spider.framework.flow.util.GlobalUtil;
import cn.spider.framework.flow.util.TaskServiceUtil;
import cn.spider.framework.param.sdk.data.QueryJsRequestParam;
import cn.spider.framework.param.sdk.data.QueryRequestParam;
import cn.spider.framework.param.sdk.data.QueryRequestResult;
import cn.spider.framework.param.sdk.interfaces.ParamInterface;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 流程任务执行核心
 *
 * @author dds
 */
@Slf4j
public abstract class FlowTaskCore<T> extends BasicTaskCore {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowTaskCore.class);

    private static final String RUN_PARAM = "runParam";

    private ParamInterface paramInterface;

    private SchedulerManager schedulerManager;

    public FlowTaskCore(StoryEngineModule engineModule, FlowRegister flowRegister, Role role, StoryBus storyBus) {
        super(engineModule, flowRegister, storyBus, role, GlobalUtil.getTaskName(flowRegister.getStartElement(), flowRegister.getRequestId()));
        this.paramInterface = SpiderCoreVerticle.factory.getBean(ParamInterface.class);
        this.schedulerManager = SpiderCoreVerticle.factory.getBean(SchedulerManager.class);
    }

    public Future<Object> runFlowElement(FlowElement flowElement, FlowExample example) {
        Promise<Object> flowElementPromise = Promise.promise();
        doInvoke(flowElement, flowElementPromise, example);
        return flowElementPromise.future();
    }

    /**
     * 执行-节点核心方法
     *
     * @param flowElement
     * @param promise
     * @return
     */
    private void doInvoke(FlowElement flowElement, Promise<Object> promise, FlowExample example) {
        if (flowElement.getElementType() != BpmnTypeEnum.SERVICE_TASK) {
            // 通知，执行结束
            promise.complete();
            return;
        }
        ServiceTask serviceTask = (ServiceTask) flowElement;
        doInvokeMethodNew(serviceTask, example, promise);
    }

    /**
     * 支持重试、降级调用
     */
    @Override
    protected void doInvokeMethod(ServiceTask serviceTask, TaskServiceDef taskServiceDef, StoryBus storyBus, Role role) {
        if (taskServiceDef.isDemotionNode()) {
            super.doInvokeMethod(serviceTask, taskServiceDef, storyBus, role);
            return;
        }
        retryInvokeMethod(serviceTask, taskServiceDef, storyBus, role);
    }


    /**
     * 支持重试、降级调用
     */
    private void doInvokeMethodNew(ServiceTask serviceTask, FlowExample example, Promise<Object> promise) {
        // 构造获取方法执行的参数
        QueryJsRequestParam queryJsRequestParam = new QueryJsRequestParam(serviceTask.queryJsFunctionName(), serviceTask.queryJsCode(), serviceTask.queryJsParams(), serviceTask.queryJsParamReal(), serviceTask.getId(), example.getRequestId());
        // 查询调用该方法需要的参数
        paramInterface.queryRunParamJs(JsonObject.mapFrom(queryJsRequestParam))
                .onSuccess(suss -> {
                    JsonObject runParam = suss.getJsonObject(RUN_PARAM);
                    // 执行调用远端服务执行 -- 修改后不支持 服务类型的调度
                    invokeMethodNew(serviceTask, Objects.isNull(runParam) ?
                                    new JsonObject() : runParam, StringUtils.EMPTY,
                            StringUtils.EMPTY, example, promise, example.getRequestId());
                }).onFailure(fail -> {
                    // 通知执行失败了。
                    promise.fail(fail);
                    LOGGER.info("doInvokeMethodNew_获取参数失败 {}", ExceptionMessage.getStackTrace(fail));
                });
        //


    }

    private void retryInvokeMethod(ServiceTask serviceTask, TaskServiceDef taskServiceDef, StoryBus storyBus, Role role) {
        super.doInvokeMethod(serviceTask, taskServiceDef, storyBus, role);
    }


    public void invokeMethodNew(ServiceTask serviceTask, JsonObject param, String methodName, String workerName, FlowExample example, Promise<Object> promise, String requestId) {
        try {
            schedulerManager.invokeNew(param, serviceTask, workerName, methodName, example, promise, requestId);
            // 后续改造- 因为不需要返回数据
        } catch (Throwable e) {
            LOGGER.error("invokeMethod- {}", ExceptionMessage.getStackTrace(e));
            throw e;
        }
    }
}
