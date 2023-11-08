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
package cn.spider.framework.flow.engine.future;

import cn.spider.framework.flow.component.hook.SimpleHook;
import cn.spider.framework.flow.engine.thread.EndTaskPedometer;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.exception.KstryException;
import cn.spider.framework.flow.util.AssertUtil;
import cn.spider.framework.flow.util.ExceptionUtil;
import cn.spider.framework.flow.util.GlobalUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * TaskFuture 管理类
 *
 * @author lykan
 */
public class AdminTaskFuture implements AdminFuture {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminTaskFuture.class);

    /**
     * 主流程 FlowFuture
     */
    private final MainTaskFuture mainTaskFuture;

    /**
     * 分组管理Future
     * k：流程开始事件id
     * v：被管理的Future列表
     */
    private final Map<String, InFutureList> groupManagedFutureMap = Maps.newHashMap();

    /**
     * 任务异常
     */
    private volatile KstryException exception;

    /**
     * 保存 EndTaskPedometer
     */
    private final Map<String, EndTaskPedometer> endTaskPedometerMap = Maps.newConcurrentMap();

    public AdminTaskFuture(MainTaskFuture mainTaskFuture) {
        AssertUtil.notNull(mainTaskFuture);
        doAddManagedFuture(null, mainTaskFuture, mainTaskFuture.getEndTaskPedometer().getStartEventId());
        this.mainTaskFuture = mainTaskFuture;
    }

    @Override
    public synchronized void addManagedFuture(String parentStartEventId, FragmentFuture future, String startEventId) {
        if (groupManagedFutureMap.get(startEventId) != null && isCancelled(startEventId)) {
            future.cancel(startEventId);
            return;
        }
        doAddManagedFuture(parentStartEventId, future, startEventId);
    }

    @Override
    public synchronized void addManagedFuture(FragmentFuture future, String startEventId) {
        addManagedFuture(null, future, startEventId);
    }

    @Override
    public MainTaskFuture getMainTaskFuture() {
        return mainTaskFuture;
    }

    @Override
    public EndTaskPedometer getEndTaskPedometer(String startEventId) {
        AssertUtil.notBlank(startEventId);
        return GlobalUtil.notNull(endTaskPedometerMap.get(startEventId));
    }

    @Override
    public synchronized boolean cancel(String startEventId) {
        AssertUtil.notBlank(startEventId);
        InFutureList inFutureList = GlobalUtil.notNull(groupManagedFutureMap.get(startEventId));
        if (inFutureList.isCancelled) {
            return false;
        }
        if (inFutureList.strictMode) {
            groupManagedFutureMap.values().forEach(inF -> {
                inF.futureList.forEach(fs -> {
                    if (!fs.isCancelled(startEventId)) {
                        fs.cancel(startEventId);
                    }
                });
                inF.isCancelled = true;
            });
            return true;
        }
        inFutureList.isCancelled = true;
        inFutureList.futureList.forEach(fs -> {
            if (fs instanceof MainTaskFuture) {
                EndTaskPedometer endTaskPedometer = ((MainTaskFuture) fs).getEndTaskPedometer();
                endTaskPedometer.forceOpenLatch();
                cancel(endTaskPedometer.getStartEventId());
            }
            if (!fs.isCancelled(startEventId)) {
                fs.cancel(startEventId);
            }
        });
        return true;
    }

    @Override
    public synchronized boolean isCancelled(String startEventId) {
        AssertUtil.notBlank(startEventId);
        InFutureList inFutureList = groupManagedFutureMap.get(startEventId);
        return inFutureList.isCancelled;
    }

    @Override
    public synchronized void errorNotice(Throwable exception, String startEventId) {
        KstryException ke = ExceptionUtil.buildException(exception, ExceptionEnum.ASYNC_TASK_ERROR, null);
        if (Objects.equals(mainTaskFuture.getEndTaskPedometer().getStartEventId(), startEventId)) {
            errorNotice(ke);
            return;
        }
        InFutureList inFutureList = GlobalUtil.notNull(groupManagedFutureMap.get(startEventId));
        if (inFutureList.strictMode) {
            errorNotice(ke);
            return;
        }
        if (inFutureList.isCancelled) {
            return;
        }
        ke.log(e -> LOGGER.warn("[{}] Task execution fails and exits because an exception is thrown! startEventId: {}", e.getErrorCode(), startEventId, e));
        Optional<FragmentFuture> subProcessFutureOptional = inFutureList.futureList.stream().filter(f -> f instanceof MonoFlowFuture)
                .filter(f -> Objects.equals(GlobalUtil.transferNotEmpty(f, MonoFlowFuture.class).getEndTaskPedometer().getStartEventId(), startEventId)).findFirst();
        subProcessFutureOptional.map(f -> GlobalUtil.transferNotEmpty(f, MonoFlowFuture.class)).ifPresent(mf -> {
            if (!mf.isCancelled(startEventId)) {
                mf.taskExceptionally(exception);
            }
        });
        cancel(startEventId);
    }

    private void errorNotice(KstryException exception) {
        try {
            if (this.exception != null) {
                exception.log(e -> LOGGER.warn("[{}] Task execution fails and exits because an exception that reappears is thrown! mainTaskName: {}",
                        e.getErrorCode(), mainTaskFuture.getTaskName(), e));
                return;
            }
            this.exception = exception;
            exception.log(e -> LOGGER.warn(
                    "[{}] Task execution fails and exits because an exception is thrown! mainTaskName: {}", e.getErrorCode(), mainTaskFuture.getTaskName(), e));
            mainTaskFuture.getEndTaskPedometer().forceOpenLatch();
            if (mainTaskFuture instanceof MonoFlowFuture) {
                ((MonoFlowFuture) mainTaskFuture).taskExceptionally(exception);
            }
        } finally {
            cancel(mainTaskFuture.getEndTaskPedometer().getStartEventId());
        }
    }

    @Override
    public Optional<KstryException> getException() {
        return Optional.ofNullable(exception);
    }

    private synchronized void doAddManagedFuture(String parentStartEventId, FragmentFuture future, String startEventId) {
        AssertUtil.notTrue(future.isCancelled(startEventId));
        AssertUtil.notBlank(startEventId);
        if (future instanceof MonoFlowFuture) {
            MonoFlowFuture monoFlowFuture = GlobalUtil.transferNotEmpty(future, MonoFlowFuture.class);
            SimpleHook<MonoFlowFuture> completableFlowTaskHook = new SimpleHook<>(monoFlowFuture);
            completableFlowTaskHook.hook(this::taskCompleted);
            monoFlowFuture.getEndTaskPedometer().setCompletedHook(completableFlowTaskHook);
        }
        if (future instanceof MainTaskFuture) {
            // EndTaskPedometer 分组保存
            MainTaskFuture mTaskF = GlobalUtil.transferNotEmpty(future, MainTaskFuture.class);
            EndTaskPedometer endTaskPedometer = mTaskF.getEndTaskPedometer();
            AssertUtil.notTrue(endTaskPedometerMap.containsKey(endTaskPedometer.getStartEventId()));
            endTaskPedometerMap.put(endTaskPedometer.getStartEventId(), endTaskPedometer);

            // 子 Future 关联父 Future
            boolean parentStrictMode = true;
            if (StringUtils.isNotBlank(parentStartEventId)) {
                InFutureList parentInFutureList = GlobalUtil.notNull(groupManagedFutureMap.get(parentStartEventId));
                parentInFutureList.futureList.add(future);
                parentStrictMode = parentInFutureList.strictMode;
            }

            // Future 分组保存
            boolean parentStrictModeFinal = parentStrictMode;
            InFutureList inFutureList = groupManagedFutureMap.computeIfAbsent(startEventId,
                    k -> new InFutureList(parentStrictModeFinal && mTaskF.strictMode()));
            inFutureList.futureList.add(future);
        } else {
            InFutureList inFutureList = GlobalUtil.notNull(groupManagedFutureMap.get(startEventId));
            inFutureList.futureList.add(future);
        }
        LOGGER.debug("Successfully create a task and submit it to the manager. taskName: {}", future.getTaskName());
    }

    private synchronized void taskCompleted(MonoFlowFuture f) {
        if (f.isCancelled(null)) {
            return;
        }
        f.taskCompleted();
    }

    private static class InFutureList {

        /**
         * Future 列表
         */
        private final List<FragmentFuture> futureList = Lists.newArrayList();

        /**
         * 严格模式
         */
        private final boolean strictMode;

        /**
         * 是否被取消
         */
        private boolean isCancelled = false;

        public InFutureList(boolean strictMode) {
            this.strictMode = strictMode;
        }
    }
}
