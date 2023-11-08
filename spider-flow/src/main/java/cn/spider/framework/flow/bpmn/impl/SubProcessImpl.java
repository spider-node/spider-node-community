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
package cn.spider.framework.flow.bpmn.impl;

import cn.spider.framework.flow.bpmn.StartEvent;
import cn.spider.framework.flow.bpmn.SubProcess;
import cn.spider.framework.flow.bpmn.enums.BpmnTypeEnum;
import cn.spider.framework.flow.resource.config.ConfigResource;
import cn.spider.framework.flow.util.AssertUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.function.Function;

/**
 * SubProcessImpl
 */
public class SubProcessImpl extends TaskImpl implements SubProcess {

    /**
     * 开始节点
     */
    private StartEvent startEvent;

    /**
     * 开始节点延迟生成器
     */
    private Function<Map<String, SubProcess>, StartEvent> startEventBuilder;

    /**
     * 资源配置信息
     */
    private ConfigResource config;

    public SubProcessImpl() {

    }

    public SubProcessImpl(Function<Map<String, SubProcess>, StartEvent> startEventBuilder) {
        AssertUtil.notNull(startEventBuilder);
        this.startEventBuilder = startEventBuilder;
    }

    @Override
    public StartEvent getStartEvent() {
        return startEvent;
    }

    @Override
    public BpmnTypeEnum getElementType() {
        return BpmnTypeEnum.SUB_PROCESS;
    }

    public void cloneSubProcess(Map<String, SubProcess> allSubProcess, SubProcessImpl targetSubProcess) {
        if (StringUtils.isBlank(targetSubProcess.getName())) {
            targetSubProcess.setName(getName());
        }
        if (targetSubProcess.startEventBuilder == null) {
            targetSubProcess.startEventBuilder = this.startEventBuilder;
        }
        if (targetSubProcess.strictMode == null) {
            targetSubProcess.strictMode = this.strictMode;
        }
        if (targetSubProcess.timeout == null) {
            targetSubProcess.timeout = this.timeout;
        }
        if (targetSubProcess.startEvent == null) {
            AssertUtil.notNull(startEventBuilder);
            targetSubProcess.startEvent = startEventBuilder.apply(allSubProcess);
        }
        if (targetSubProcess.elementIterable == null && this.elementIterable != null) {
            targetSubProcess.mergeElementIterable(this.elementIterable);
        }
    }

    public ConfigResource getConfig() {
        return config;
    }

    public void setConfig(ConfigResource resource) {
        this.config = resource;
        if (startEvent != null) {
            this.startEvent.setConfig(resource);
        }
    }
}
