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

import cn.spider.framework.flow.bpmn.Task;
import cn.spider.framework.flow.bpmn.enums.BpmnTypeEnum;
import cn.spider.framework.flow.bpmn.enums.IterateStrategyEnum;
import cn.spider.framework.flow.bpmn.extend.ElementIterable;
import cn.spider.framework.flow.util.GlobalUtil;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Optional;

/**
 * TaskImpl
 */
public abstract class TaskImpl extends FlowElementImpl implements Task {

    /**
     * 子任务超时时间，为空时使用全局默认超时时间
     */
    protected Integer timeout;

    /**
     * 严格模式，控制子任务执行失败后是否要抛出异常，默认是严格模式，子任务抛出异常后结束整个 Story 流程
     * 关闭严格模式后，子任务抛出异常时忽略继续向下执行
     */
    protected Boolean strictMode;

    /**
     * BPMN元素迭代器
     */
    protected BasicElementIterable elementIterable;

    @Override
    public BpmnTypeEnum getElementType() {
        return BpmnTypeEnum.TASK;
    }

    @Override
    public boolean strictMode() {
        return BooleanUtils.isNotFalse(strictMode);
    }

    public void setStrictMode(Boolean strictMode) {
        this.strictMode = strictMode;
    }

    @Override
    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    @Override
    public String getIteSource() {
        return Optional.ofNullable(elementIterable).map(ElementIterable::getIteSource).orElse(null);
    }

    @Override
    public Boolean openAsync() {
        return Optional.ofNullable(elementIterable).map(ElementIterable::openAsync).orElse(false);
    }

    @Override
    public IterateStrategyEnum getIteStrategy() {
        return Optional.ofNullable(elementIterable).map(ElementIterable::getIteStrategy).orElse(null);
    }

    @Override
    public Integer getStride() {
        return Optional.ofNullable(elementIterable).map(ElementIterable::getStride).orElse(null);
    }

    @Override
    public boolean iterable() {
        return Optional.ofNullable(elementIterable).map(ElementIterable::iterable).orElse(false);
    }

    public void mergeElementIterable(ElementIterable elementIterable) {
        if (elementIterable == null) {
            return;
        }
        if (this.elementIterable != null) {
            this.elementIterable.mergeProperty(elementIterable);
        } else {
            this.elementIterable = GlobalUtil.transferNotEmpty(elementIterable, BasicElementIterable.class);
        }
    }
}
