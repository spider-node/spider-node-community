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
package cn.spider.framework.flow.container.processor;

import cn.spider.framework.flow.bpmn.FlowElement;
import cn.spider.framework.flow.bpmn.StartEvent;
import cn.spider.framework.flow.bpmn.SubProcess;
import cn.spider.framework.flow.component.bpmn.DiagramTraverseSupport;

import java.util.Optional;

/**
 * 将元素中的出入度变为不可变对象，防止后面程序修改了出入度导致链路出错
 *
 * @author lykan
 */
public class ImmutablePostProcessor extends DiagramTraverseSupport<Object> implements StartEventPostProcessor {

    @Override
    public Optional<StartEvent> postStartEvent(StartEvent startEvent) {
        traverse(startEvent);
        return Optional.of(startEvent);
    }

    @Override
    public void doPlainElement(Object course, FlowElement node, SubProcess subProcess) {
        node.immutable();
    }

    @Override
    public int getOrder() {
        return 50;
    }
}
