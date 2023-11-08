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

import cn.spider.framework.flow.bpmn.ParallelGateway;
import cn.spider.framework.flow.bpmn.enums.BpmnTypeEnum;
import org.apache.commons.lang3.BooleanUtils;

/**
 * ParallelGatewayImpl
 */
public class ParallelGatewayImpl extends GatewayImpl implements ParallelGateway {

    /**
     * 支持异步流程
     */
    private final BasicAsyncFlowElement asyncFlowElement;

    /**
     * 控制严格模式，默认情况下是严格模式
     * 非严格模式下，并行网关允许无效的入度
     */
    private Boolean strictMode;

    public ParallelGatewayImpl() {
        this.asyncFlowElement = new BasicAsyncFlowElement();
    }

    @Override
    public BpmnTypeEnum getElementType() {
        return BpmnTypeEnum.PARALLEL_GATEWAY;
    }

    public void setOpenAsync(boolean openAsync) {
        this.asyncFlowElement.setOpenAsync(openAsync);
    }

    @Override
    public Boolean openAsync() {
        return asyncFlowElement.openAsync();
    }

    @Override
    public boolean isStrictMode() {
        return BooleanUtils.isNotFalse(strictMode);
    }

    /**
     * 设置严格模式
     *
     * @param strictMode 严格模式是否开启
     */
    public void setStrictMode(Boolean strictMode) {
        this.strictMode = strictMode;
    }
}
