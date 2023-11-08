package cn.spider.framework.flow.engine;
import cn.spider.framework.flow.bpmn.*;
import cn.spider.framework.flow.bpmn.enums.BpmnTypeEnum;
import cn.spider.framework.flow.bpmn.enums.ServerTaskTypeEnum;
import cn.spider.framework.flow.bus.StoryBus;
import cn.spider.framework.flow.container.component.InvokeProperties;
import cn.spider.framework.flow.container.component.MethodWrapper;
import cn.spider.framework.flow.container.component.TaskServiceDef;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.role.Role;
import cn.spider.framework.flow.util.ExceptionUtil;
import cn.spider.framework.flow.util.GlobalUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

/**
 * 流程任务执行核心
 *
 * @author lykan
 */
public abstract class FlowTaskCore<T> extends BasicTaskCore {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowTaskCore.class);

    public FlowTaskCore(StoryEngineModule engineModule, FlowRegister flowRegister, Role role, StoryBus storyBus) {
        super(engineModule, flowRegister, storyBus, role, GlobalUtil.getTaskName(flowRegister.getStartElement(), flowRegister.getRequestId()));
    }

    public Future<Object> runFlowElement(Role role, FlowElement flowElement, FlowRegister flowRegister) {
        Promise<Object> flowElementPromise = Promise.promise();
        try {
            doInvoke(role, storyBus, flowRegister, flowElement, flowElementPromise);
        } catch (Exception e) {
            flowElementPromise.fail(e);
        }
        return flowElementPromise.future();
    }

    /**
     * 执行-节点核心方法
     * @param role
     * @param storyBus
     * @param flowRegister
     * @param flowElement
     * @param promise
     * @return
     */
    private boolean doInvoke(Role role, StoryBus storyBus, FlowRegister flowRegister, FlowElement flowElement, Promise<Object> promise) {
        if (flowElement.getElementType() != BpmnTypeEnum.SERVICE_TASK) {
            // 通知，执行结束
            promise.complete();
            return true;
        }

        ServiceTask serviceTask = (ServiceTask) flowElement;
        serviceTask.setTaskServicePromise(promise);
        Optional<TaskServiceDef> taskServiceDefOptional = engineModule.getTaskContainer().getTaskServiceDef(serviceTask.getTaskComponent(), serviceTask.getTaskService(), role);
        if ((!taskServiceDefOptional.isPresent() && serviceTask.allowAbsent()) || serviceTask.queryServiceTaskType().equals(ServerTaskTypeEnum.BACK)) {
            // 通知该节点执行结束
            promise.complete();
            return true;
        }

        TaskServiceDef taskServiceDef = taskServiceDefOptional.orElseThrow(() ->
                ExceptionUtil.buildException(null, ExceptionEnum.TASK_SERVICE_MATCH_ERROR, ExceptionEnum.TASK_SERVICE_MATCH_ERROR.getDesc()
                        + GlobalUtil.format(" service task identity: {}", serviceTask.identity())));
        flowRegister.getMonitorTracking().getServiceNodeTracking(flowElement).ifPresent(nodeTracking -> {
            MethodWrapper methodWrapper = taskServiceDef.getMethodWrapper();
            nodeTracking.setThreadId(Thread.currentThread().getName());
            nodeTracking.setMethodName(methodWrapper.getMethod().getName());
            nodeTracking.setAbility(Optional.ofNullable(methodWrapper.getAbility()).filter(StringUtils::isNotBlank).orElse(null));
        });

        try {
            doInvokeMethod(serviceTask, taskServiceDef, storyBus, role);
        } catch (Throwable exception) {
            InvokeProperties invokeProperties = taskServiceDef.getMethodWrapper().getInvokeProperties();
            if (!(serviceTask.strictMode() && invokeProperties.isStrictMode())) {
                LOGGER.info("[{}] Target method execution failure, error is ignored in non-strict mode. exception: {}",
                        ExceptionEnum.SERVICE_INVOKE_ERROR.getExceptionCode(), exception.getMessage(), exception);
                // 通知future 执行失败
                promise.fail(exception);
                return true;
            }
            throw exception;
        }
        return true;
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

    private void retryInvokeMethod(ServiceTask serviceTask, TaskServiceDef taskServiceDef, StoryBus storyBus, Role role) {
        super.doInvokeMethod(serviceTask, taskServiceDef, storyBus, role);
    }
}
