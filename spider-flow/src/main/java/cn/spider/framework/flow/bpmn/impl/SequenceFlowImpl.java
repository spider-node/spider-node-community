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

import cn.spider.framework.flow.bpmn.FlowElement;
import cn.spider.framework.flow.bpmn.SequenceFlow;
import cn.spider.framework.flow.bpmn.enums.BpmnTypeEnum;
import cn.spider.framework.flow.component.expression.ConditionExpression;
import cn.spider.framework.flow.component.expression.Expression;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.util.AssertUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.Ordered;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * SequenceFlowImpl
 */
public class SequenceFlowImpl extends FlowElementImpl implements SequenceFlow {

    /**
     * Boolean 表达式
     */
    private Expression expression;

    /**
     * 经过该 SequenceFlow 的聚合节点（InclusiveGateway、ParallelGateway、EndEvent）
     */
    private List<FlowElement> endElementList = Lists.newArrayList();

    /**
     * 不可变标识
     */
    private boolean immutable = false;

    @Override
    public BpmnTypeEnum getElementType() {
        return BpmnTypeEnum.SEQUENCE_FLOW;
    }

    @Override
    public Optional<ConditionExpression> getConditionExpression() {
        return Optional.ofNullable(this.expression).flatMap(Expression::getConditionExpression);
    }

    @Override
    public int getOrder() {
        return getConditionExpression().map(ConditionExpression::getOrder).orElse(Ordered.LOWEST_PRECEDENCE);
    }

    /**
     * 设置表达式
     *
     * @param expression 表达式
     */
    public void setExpression(Expression expression) {
        AssertUtil.notTrue(immutable, ExceptionEnum.COMPONENT_IMMUTABLE_ERROR, "FlowElement is not modifiable.");
        this.expression = expression;
    }

    @Override
    public void addEndElementList(List<FlowElement> endElementList) {
        AssertUtil.notTrue(immutable, ExceptionEnum.COMPONENT_IMMUTABLE_ERROR, "FlowElement is not modifiable.");
        if (CollectionUtils.isEmpty(endElementList)) {
            return;
        }
        endElementList.forEach(endElement -> {
            if (this.endElementList.contains(endElement)) {
                return;
            }
            this.endElementList.add(endElement);
        });
    }

    @Override
    public List<FlowElement> getEndElementList() {
        return this.endElementList;
    }

    @Override
    public void immutableEndElement() {
        this.endElementList = Collections.unmodifiableList(this.endElementList);
        immutable = true;
    }
}
