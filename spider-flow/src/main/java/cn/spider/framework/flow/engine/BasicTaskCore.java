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
package cn.spider.framework.flow.engine;
import cn.spider.framework.flow.bpmn.ServiceTask;
import cn.spider.framework.flow.bpmn.enums.IterateStrategyEnum;
import cn.spider.framework.flow.bus.StoryBus;
import cn.spider.framework.flow.component.validator.RequestValidator;
import cn.spider.framework.flow.container.component.MethodWrapper;
import cn.spider.framework.flow.container.component.ParamInjectDef;
import cn.spider.framework.flow.container.component.TaskInstructWrapper;
import cn.spider.framework.flow.container.component.TaskServiceDef;
import cn.spider.framework.flow.container.task.impl.TaskComponentProxy;
import cn.spider.framework.flow.engine.thread.InvokeMethodThreadLocal;
import cn.spider.framework.flow.engine.thread.Task;
import cn.spider.framework.flow.engine.thread.hook.ThreadSwitchHook;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.monitor.MonitorTracking;
import cn.spider.framework.flow.role.Role;
import cn.spider.framework.flow.util.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 方法调用核心
 *
 * @author dds
 */
@Slf4j
public abstract class BasicTaskCore implements Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicTaskCore.class);

    /**
     * StoryEngine 组成模块
     */
    protected final StoryEngineModule engineModule;

    /**
     * 流程寄存器
     */
    protected final FlowRegister flowRegister;

    /**
     * StoryBus
     */
    protected final StoryBus storyBus;

    /**
     * 角色
     */
    protected final Role role;

    /**
     * 削减锁，控制任务提交后不能立刻执行
     */
    protected final CountDownLatch asyncTaskSwitch = new CountDownLatch(1);

    /**
     * 任务名称
     */
    private final String taskName;

    public BasicTaskCore(StoryEngineModule engineModule, FlowRegister flowRegister, StoryBus storyBus, Role role, String taskName) {
        AssertUtil.notBlank(taskName);
        AssertUtil.anyNotNull(engineModule, flowRegister, storyBus, role);
        this.engineModule = engineModule;
        this.flowRegister = flowRegister;
        this.storyBus = storyBus;
        this.role = role;
        this.taskName = taskName;
    }

    @Override
    public void openSwitch() {
        asyncTaskSwitch.countDown();
    }

    @Override
    public String getTaskName() {
        return taskName;
    }

    public FlowRegister getFlowRegister() {
        return flowRegister;
    }

    /**
     * 支持批量调用
     */
    protected void doInvokeMethod(ServiceTask serviceTask, TaskServiceDef taskServiceDef, StoryBus storyBus, Role role) {
        MethodWrapper methodWrapper = taskServiceDef.getMethodWrapper();
        List<ParamInjectDef> paramInjectDefs = methodWrapper.getParamInjectDefs();

        // 降级方法调用时不支持迭代
        if (!serviceTask.iterable() || taskServiceDef.isDemotionNode()) {
            doInvokeMethod(true, serviceTask, storyBus, role, methodWrapper, paramInjectDefs);
        }
        Optional<Object> iteData = storyBus.getScopeDataOperator().getData(serviceTask.getIteSource()).map(d -> {
            if (!d.getClass().isArray()) {
                return d;
            }
            int arrLength = Array.getLength(d);
            if (arrLength == 0) {
                return null;
            }
            Object[] dArray = new Object[arrLength];
            for (int i = 0; i < arrLength; i++) {
                dArray[i] = Array.get(d, i);
            }
            return Stream.of(dArray).filter(Objects::nonNull).collect(Collectors.toList());
        }).filter(d -> d instanceof Iterable);
        if (!iteData.isPresent()) {
            LOGGER.info("[{}] {} identity: {}, source: {}", ExceptionEnum.ITERATE_ITEM_ERROR.getExceptionCode(),
                    "Get the target collection is empty, the component will not perform traversal execution!", serviceTask.identity(), serviceTask.getIteSource());
            return ;
        }
        Iterator<?> iterator = GlobalUtil.transferNotEmpty(iteData.get(), Iterable.class).iterator();
        if (!iterator.hasNext()) {
            LOGGER.info("[{}] {} identity: {}, source: {}", ExceptionEnum.ITERATE_ITEM_ERROR.getExceptionCode(),
                    "Get the target collection is empty, the component will not perform traversal execution!", serviceTask.identity(), serviceTask.getIteSource());
        }
        // 设置为异步执行
    }

    /**
     * 实际调用目标方法
     */
    private void doInvokeMethod(boolean tracking, ServiceTask serviceTask, StoryBus storyBus,
                                  Role role, MethodWrapper methodWrapper, List<ParamInjectDef> paramInjectDefs) {
        try {
            if (CollectionUtils.isEmpty(paramInjectDefs)) {
                ProxyUtil.invokeMethod(storyBus, methodWrapper, serviceTask);
            }

            Function<ParamInjectDef, Object> paramInitStrategy = engineModule.getParamInitStrategy();
            TaskInstructWrapper taskInstructWrapper = methodWrapper.getTaskInstructWrapper().orElse(null);
            // 核心 -获取参数+执行
            //log.info("获取参数-------------构造参数中 {} 时间 {}",serviceTask.getTaskService(),System.currentTimeMillis());
            ProxyUtil.invokeMethod(storyBus, methodWrapper, serviceTask, () -> {
                Map<String,Object> paramMap = TaskServiceUtil.getTaskParams(methodWrapper.isCustomRole(), tracking,
                        serviceTask, storyBus, role, taskInstructWrapper, paramInjectDefs, paramInitStrategy, engineModule.getApplicationContext());
                TaskServiceUtil.fillTaskParams(paramMap, serviceTask.getTaskParams(), paramInjectDefs, paramInitStrategy, storyBus.getScopeDataOperator());
                return paramMap;
            });
        } catch (Throwable e) {
            if (!serviceTask.iterable() || serviceTask.getIteStrategy() == null || serviceTask.getIteStrategy() == IterateStrategyEnum.ALL_SUCCESS) {
                throw e;
            }
            LOGGER.warn("[{}] {} identity: {}", ExceptionEnum.ITERATE_ITEM_ERROR.getExceptionCode(), ExceptionEnum.ITERATE_ITEM_ERROR.getDesc(), serviceTask.identity(), e);
        }
    }


}
