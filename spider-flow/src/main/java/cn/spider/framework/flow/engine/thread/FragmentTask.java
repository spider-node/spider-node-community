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

import cn.spider.framework.flow.bpmn.FlowElement;
import cn.spider.framework.flow.bus.StoryBus;
import cn.spider.framework.flow.engine.FlowRegister;
import cn.spider.framework.flow.engine.FlowTaskCore;
import cn.spider.framework.flow.engine.StoryEngineModule;
import cn.spider.framework.flow.engine.example.data.FlowExample;
import cn.spider.framework.flow.role.Role;
import io.vertx.core.Future;

/**
 * 流程片段执行任务
 *
 * @author lykan
 */
public class FragmentTask extends FlowTaskCore implements Task {

    private FlowElement flowElement;

    private FlowExample example;

    public FragmentTask(StoryEngineModule engineModule, FlowRegister flowRegister, Role role, StoryBus storyBus) {
        super(engineModule, flowRegister, role, storyBus);
    }

    public void init(FlowElement flowElement,FlowExample example){
        this.flowElement = flowElement;
        this.example = example;
    }

    public Future<Object> runPlan() {
        return runFlowElement(example.getRole(), flowElement, example.getFlowRegister());
    }
}