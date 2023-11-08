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
package cn.spider.framework.flow.component.bpmn.builder;

import cn.spider.framework.flow.bpmn.SubProcess;
import cn.spider.framework.flow.bpmn.extend.ElementIterable;
import cn.spider.framework.flow.bpmn.impl.SubProcessImpl;
import cn.spider.framework.flow.component.bpmn.link.BpmnElementDiagramLink;
import cn.spider.framework.flow.component.bpmn.link.ProcessLink;

/**
 * SubProcessBuilder 构建类
 *
 * @author lykan
 */
public class SubProcessBuilder {

    private final ProcessLink processLink;

    private final SubProcessImpl subProcess;

    public SubProcessBuilder(SubProcessImpl subProcess, ProcessLink processLink) {
        this.processLink = processLink;
        this.subProcess = subProcess;
    }

    public SubProcessBuilder notStrictMode() {
        this.subProcess.setStrictMode(false);
        return this;
    }

    public SubProcessBuilder iterable(ElementIterable iterable) {
        this.subProcess.mergeElementIterable(iterable);
        return this;
    }

    public SubProcessBuilder timeout(int timeout) {
        this.subProcess.setTimeout(Math.max(timeout, 0));
        return this;
    }

    public ProcessLink build() {
        return new BpmnElementDiagramLink<SubProcess>(subProcess, processLink);
    }
}
