/*
 *
 *  * Copyright (c) 2020-2023, Lykan (jiashuomeng@gmail.com).
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package cn.spider.framework.flow.engine.thread;

import cn.spider.framework.flow.bpmn.ServiceTask;
import cn.spider.framework.flow.bus.StoryBus;
import cn.spider.framework.flow.container.component.TaskServiceDef;
import cn.spider.framework.flow.engine.BasicTaskCore;
import cn.spider.framework.flow.engine.FlowRegister;
import cn.spider.framework.flow.engine.StoryEngineModule;
import cn.spider.framework.flow.engine.future.AdminFuture;
import cn.spider.framework.flow.engine.future.InvokeFuture;
import cn.spider.framework.flow.engine.future.MethodInvokeFuture;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.role.Role;
import cn.spider.framework.flow.util.AssertUtil;
import cn.spider.framework.flow.util.GlobalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * 方法调用任务
 *
 * @author dds
 */
public class MethodInvokeTask extends BasicTaskCore implements Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodInvokeTask.class);

    /**
     * ServiceTask
     */
    private final ServiceTask serviceTask;

    /**
     * 服务节点定义
     */
    private final TaskServiceDef taskServiceDef;

    /**
     * 程序执行计步器
     */
    private final MethodInvokePedometer methodInvokePedometer;

    public MethodInvokeTask(MethodInvokePedometer methodInvokePedometer, FlowRegister flowRegister,
                            StoryEngineModule engineModule, ServiceTask serviceTask, TaskServiceDef taskServiceDef, StoryBus storyBus, Role role) {
        super(engineModule, flowRegister, storyBus, role, GlobalUtil.getTaskName(serviceTask, flowRegister.getRequestId()));
        AssertUtil.anyNotNull(flowRegister.getAdminFuture(), serviceTask, taskServiceDef, storyBus, role);
        this.serviceTask = serviceTask;
        this.taskServiceDef = taskServiceDef;
        this.methodInvokePedometer = methodInvokePedometer;
    }


    public InvokeFuture buildTaskFuture(Future<Object> future) {
        return new MethodInvokeFuture(future, getTaskName());
    }


    public Object call() throws Exception {
        AdminFuture adminFuture = flowRegister.getAdminFuture();
        try {
            flowRegister.getMonitorTracking().getServiceNodeTracking(serviceTask).ifPresent(nodeTracking -> nodeTracking.setThreadId(Thread.currentThread().getName()));
            AssertUtil.notTrue(adminFuture.isCancelled(flowRegister.getStartEventId()), ExceptionEnum.ASYNC_TASK_INTERRUPTED,
                    "Task interrupted. Method invoke task was interrupted! taskName: {}", getTaskName());
            doInvokeMethod(serviceTask, taskServiceDef, storyBus, role);
        } catch (Throwable e) {
            if (adminFuture.isCancelled(flowRegister.getStartEventId())) {
                adminFuture.errorNotice(e, flowRegister.getStartEventId());
                throw e;
            }
            // 可重试
            if (methodInvokePedometer.remainRetry > 0) {
                throw e;
            }
            // 可降级
            if (methodInvokePedometer.needDemotionSupplier.get().isPresent() && !methodInvokePedometer.isDemotion) {
                throw e;
            }
            // 非严格模式
            if (!(serviceTask.strictMode() && methodInvokePedometer.strictMode)) {
                LOGGER.warn("[{}] Target method execution failure, error is ignored in non-strict mode. exception: {}",
                        ExceptionEnum.SERVICE_INVOKE_ERROR.getExceptionCode(), e.getMessage(), e);
                throw e;
            }
            adminFuture.errorNotice(e, flowRegister.getStartEventId());
            throw e;
        }
        return null;
    }

    public static class MethodInvokePedometer {

        private final int remainRetry;

        private final Supplier<Optional<TaskServiceDef>> needDemotionSupplier;

        private final boolean isDemotion;

        private final boolean strictMode;

        public MethodInvokePedometer(int remainRetry, Supplier<Optional<TaskServiceDef>> needDemotionSupplier, boolean isDemotion, boolean strictMode) {
            this.remainRetry = remainRetry;
            this.needDemotionSupplier = needDemotionSupplier;
            this.isDemotion = isDemotion;
            this.strictMode = strictMode;
        }

        public boolean isDemotion() {
            return isDemotion;
        }
    }
}
