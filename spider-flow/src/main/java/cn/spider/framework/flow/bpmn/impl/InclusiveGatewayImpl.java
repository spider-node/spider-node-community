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

import cn.spider.framework.flow.bpmn.InclusiveGateway;
import cn.spider.framework.flow.bpmn.ServiceTask;
import cn.spider.framework.flow.bpmn.enums.BpmnTypeEnum;
import cn.spider.framework.flow.bpmn.extend.ServiceTaskSupport;

import java.util.Optional;

/**
 * InclusiveGatewayImpl
 */
public class InclusiveGatewayImpl extends GatewayImpl implements InclusiveGateway, ServiceTaskSupport {

    /**
     * 支持异步流程
     */
    private final BasicAsyncFlowElement asyncFlowElement;

    /**
     * 支持定义 ServiceTask
     */
    private ServiceTask serviceTask;

    public InclusiveGatewayImpl() {
        this.asyncFlowElement = new BasicAsyncFlowElement();
        this.serviceTask = null;
    }

    @Override
    public BpmnTypeEnum getElementType() {
        return BpmnTypeEnum.INCLUSIVE_GATEWAY;
    }

    @Override
    public Boolean openAsync() {
        return asyncFlowElement.openAsync();
    }

    public void setOpenAsync(boolean openAsync) {
        this.asyncFlowElement.setOpenAsync(openAsync);
    }

    public void setServiceTask(ServiceTask serviceTask) {
        if (serviceTask != null && serviceTask.validTask()) {
            this.serviceTask = serviceTask;
        }
    }

    @Override
    public Optional<ServiceTask> getServiceTask() {
        return Optional.ofNullable(serviceTask);
    }
}
