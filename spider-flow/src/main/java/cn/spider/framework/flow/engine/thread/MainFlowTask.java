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

import cn.spider.framework.flow.bpmn.EndEvent;
import cn.spider.framework.flow.bpmn.StartEvent;
import cn.spider.framework.flow.bus.StoryBus;
import cn.spider.framework.flow.engine.FlowRegister;
import cn.spider.framework.flow.engine.StoryEngineModule;
import cn.spider.framework.flow.engine.future.AdminFuture;
import cn.spider.framework.flow.engine.future.MainTaskFuture;
import cn.spider.framework.flow.enums.AsyncTaskState;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.role.Role;
import cn.spider.framework.flow.util.AssertUtil;
import cn.spider.framework.flow.util.ExceptionUtil;
import cn.spider.framework.flow.util.GlobalUtil;

import java.util.concurrent.Future;

/**
 * 主流程任务
 *
 * @author lykan
 */
public class MainFlowTask extends FragmentTask {

    /**
     * 结束任务计步器
     */
    protected final EndTaskPedometer endTaskPedometer;

    public MainFlowTask(StoryEngineModule engineModule, FlowRegister flowRegister, Role role, StoryBus storyBus) {
        super(engineModule, flowRegister, role, storyBus);
        StartEvent startEvent = GlobalUtil.transferNotEmpty(flowRegister.getStartElement(), StartEvent.class);
        EndEvent endEvent = GlobalUtil.notNull(startEvent.getEndEvent());
        this.endTaskPedometer = new EndTaskPedometer(startEvent.getId(), endEvent.comingList(), getTaskName());
    }


    public MainTaskFuture buildTaskFuture(Future<AsyncTaskState> future) {
        throw ExceptionUtil.buildException(null, ExceptionEnum.ASYNC_TASK_ERROR, null);
    }

    /**
     * 设置 TaskFuture 管理类
     *
     * @param adminFuture TaskFuture 管理类
     */
    public void setAdminFuture(AdminFuture adminFuture) {
        AssertUtil.isNull(flowRegister.getAdminFuture());
        flowRegister.setAdminFuture(adminFuture);
    }
}
