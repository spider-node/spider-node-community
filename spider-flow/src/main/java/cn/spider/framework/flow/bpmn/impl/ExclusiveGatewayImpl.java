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

import cn.spider.framework.flow.bpmn.ExclusiveGateway;
import cn.spider.framework.flow.bpmn.ServiceTask;
import cn.spider.framework.flow.bpmn.enums.BpmnTypeEnum;
import cn.spider.framework.flow.bpmn.extend.ServiceTaskSupport;

import java.util.Optional;

/**
 * ExclusiveGatewayImpl
 */
public class ExclusiveGatewayImpl extends GatewayImpl implements ExclusiveGateway, ServiceTaskSupport {

    /**
     * 支持定义 ServiceTask
     */
    private ServiceTask serviceTask;

    public ExclusiveGatewayImpl() {

    }

    @Override
    public BpmnTypeEnum getElementType() {
        return BpmnTypeEnum.EXCLUSIVE_GATEWAY;
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
