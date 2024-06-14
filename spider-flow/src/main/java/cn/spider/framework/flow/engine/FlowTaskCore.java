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
import cn.spider.framework.param.sdk.data.QueryRequestParam;
import cn.spider.framework.param.sdk.data.QueryRequestResult;
import cn.spider.framework.param.sdk.interfaces.ParamInterface;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;

/**
 * 流程任务执行核心
 *
 * @author dds
 */
public abstract class FlowTaskCore<T> extends BasicTaskCore {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowTaskCore.class);

    private ParamInterface paramInterface;

    private SchedulerManager schedulerManager;

    public FlowTaskCore(StoryEngineModule engineModule, FlowRegister flowRegister, Role role, StoryBus storyBus) {
        super(engineModule, flowRegister, storyBus, role, GlobalUtil.getTaskName(flowRegister.getStartElement(), flowRegister.getRequestId()));
        this.paramInterface = SpiderCoreVerticle.factory.getBean(ParamInterface.class);
        this.schedulerManager = SpiderCoreVerticle.factory.getBean(SchedulerManager.class);
    }

    public Future<Object>  runFlowElement(FlowElement flowElement, FlowExample example) {
        Promise<Object> flowElementPromise = Promise.promise();
        doInvoke(flowElement, flowElementPromise,example);
        return flowElementPromise.future();
    }

    /**
     * 执行-节点核心方法
     *
     * @param flowElement
     * @param promise
     * @return
     */
    private void doInvoke(FlowElement flowElement, Promise<Object> promise,FlowExample example) {
        if (flowElement.getElementType() != BpmnTypeEnum.SERVICE_TASK) {
            // 通知，执行结束
            promise.complete();
            return;
        }
        // 校验是真实执行，还是虚拟执行（虚拟执行不会真的执行，只是读取日志中执行记过）
        if (StringUtils.isNotEmpty(example.getRetryNodeId()) && example.getRetryNodeId().equals(flowElement.getId())) {
            example.setRunType(Constant.ACTUAL);
        }
        ServiceTask serviceTask = (ServiceTask) flowElement;
        doInvokeMethodNew(serviceTask,example,promise);
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
    private void doInvokeMethodNew(ServiceTask serviceTask,FlowExample example,Promise<Object> promise) {
        // 构造获取方法执行的参数
        QueryRequestParam queryRequestParam = new QueryRequestParam();
        queryRequestParam.setAppointParam(serviceTask.obtainAppointParam());
        queryRequestParam.setParamsMapping(serviceTask.getFiledMapping());
        queryRequestParam.setRequestId(serviceTask.getRequestId());
        queryRequestParam.setTaskComponent(serviceTask.getTaskComponent());
        queryRequestParam.setTaskService(serviceTask.getTaskService());
        // 查询调用该方法需要的参数
        paramInterface.queryRunParam(JsonObject.mapFrom(queryRequestParam))
                .onSuccess(suss -> {
                    QueryRequestResult queryRequestResult = new QueryRequestResult();
                    queryRequestResult.setRunParam(suss.getJsonObject(Constant.RUN_PARAM));
                    queryRequestResult.setTaskMethod(suss.getString(Constant.TASK_METHOD));
                    queryRequestResult.setWorkerId(suss.getString(Constant.WORKER_ID));
                    // 执行调用远端服务执行
                    invokeMethodNew(serviceTask,Objects.isNull(queryRequestResult.getRunParam()) ? new JsonObject() : queryRequestResult.getRunParam(),queryRequestResult.getTaskMethod(),queryRequestResult.getWorkerId(),example,promise,example.getRequestId());
                }).onFailure(fail -> {
                    // 通知执行失败了。
                    promise.fail(fail);
                    LOGGER.info("doInvokeMethodNew_获取参数失败 {}", ExceptionMessage.getStackTrace(fail));
                });
    }

    private void retryInvokeMethod(ServiceTask serviceTask, TaskServiceDef taskServiceDef, StoryBus storyBus, Role role) {
        super.doInvokeMethod(serviceTask, taskServiceDef, storyBus, role);
    }


    public void invokeMethodNew(ServiceTask serviceTask, JsonObject param,String methodName,String workerName,FlowExample example,Promise<Object> promise,String requestId) {
        try {
            schedulerManager.invokeNew(param.getMap(), serviceTask,workerName,methodName,example,promise,requestId);
            // 后续改造- 因为不需要返回数据
        } catch (Throwable e) {
            LOGGER.error("invokeMethod- {}", ExceptionMessage.getStackTrace(e));
            throw e;
        }
    }
}
