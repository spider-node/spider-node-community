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

import cn.spider.framework.flow.bus.StoryBus;
import cn.spider.framework.flow.engine.FlowRegister;
import cn.spider.framework.flow.engine.StoryEngineModule;
import cn.spider.framework.flow.engine.future.FlowFuture;
import cn.spider.framework.flow.engine.future.FlowTaskFuture;
import cn.spider.framework.flow.enums.AsyncTaskState;
import cn.spider.framework.flow.role.Role;

import java.util.concurrent.Future;

/**
 * 主流程任务
 *
 * @author dds
 */
public class FlowTask extends MainFlowTask {

    public FlowTask(StoryEngineModule engineModule, FlowRegister flowRegister, Role role, StoryBus storyBus) {
        super(engineModule, flowRegister, role, storyBus);
    }

    @Override
    public FlowFuture buildTaskFuture(Future<AsyncTaskState> future) {
        return new FlowTaskFuture(endTaskPedometer, future, getTaskName());
    }
}
