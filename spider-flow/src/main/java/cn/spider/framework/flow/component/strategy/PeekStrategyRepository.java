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
package cn.spider.framework.flow.component.strategy;
import cn.spider.framework.flow.bpmn.*;
import cn.spider.framework.flow.bus.ContextStoryBus;
import cn.spider.framework.flow.bus.StoryBus;
import cn.spider.framework.flow.component.expression.Expression;
import cn.spider.framework.flow.engine.thread.EndTaskPedometer;
import cn.spider.framework.flow.enums.ElementAllowNextEnum;
import cn.spider.framework.flow.util.AssertUtil;
import cn.spider.framework.flow.util.GlobalUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 *
 * @author lykan
 */
public class PeekStrategyRepository {

    private static final List<PeekStrategy> peekStrategyList = Lists.newArrayList(new PeekStrategy() {

        /**
         * ExclusiveGateway
         */
        @Override
        public boolean match(FlowElement flowElement) {
            return flowElement instanceof ExclusiveGateway;
        }

        /**
         * 排他网关：
         *  1> 没有有效出度，抛异常
         *  2> 一个有效出度正常执行，多个有效出度时，按顺序取第一个，其他出度忽略
         *  3> 没有条件表达式的分支，代表 true
         */
        @Override
        public boolean needPeek(FlowElement flowElement, ContextStoryBus contextStoryBus) {
            if (contextStoryBus.getPeekCount() >= 1) {
                return false;
            }
            boolean needPeek = PeekStrategyRepository.needPeek((SequenceFlow) flowElement, contextStoryBus.getStoryBus());
            if (needPeek) {
                contextStoryBus.incrPeekCount();
            }
            return needPeek;
        }
    }, new PeekStrategy() {

        /**
         * 并行网关（ ParallelGateway ）
         *  1> 经过 ParallelGateway 后，几个出度就有几个并行流，Sequence Flow 上的条件表达式会直接被忽略
         *  2> 全部入度到达且执行完成后，ParallelGateway 才会继续向下执行
         *  3> 全部入度都需要且只能被执行一次（一些流程框架是只要入度执行次数与入度数量相同即可向下执行，这点上 Kstry 与其不同）
         */
        @Override
        public boolean match(FlowElement flowElement) {
            return flowElement instanceof ParallelGateway;
        }

        @Override
        public boolean skip(FlowElement flowElement, ContextStoryBus contextStoryBus) {
            return allowDoNext(flowElement, contextStoryBus.getPrevElement(), contextStoryBus, true) != ElementAllowNextEnum.ALLOW_NEX;
        }

        @Override
        public boolean needPeek(FlowElement flowElement, ContextStoryBus contextStoryBus) {
            return true;
        }
    }, new PeekStrategy() {

        /**
         * InclusiveGateway
         */
        @Override
        public boolean match(FlowElement flowElement) {
            return flowElement instanceof InclusiveGateway;
        }

        @Override
        public boolean skip(FlowElement flowElement, ContextStoryBus contextStoryBus) {
            return allowDoNext(flowElement, contextStoryBus.getPrevElement(), contextStoryBus, true) != ElementAllowNextEnum.ALLOW_NEX;
        }

        @Override
        public boolean needPeek(FlowElement flowElement, ContextStoryBus contextStoryBus) {
            return PeekStrategyRepository.needPeek((SequenceFlow) flowElement, contextStoryBus.getStoryBus());
        }
    }, new PeekStrategy() {

        /**
         * SequenceFlow
         */
        @Override
        public boolean match(FlowElement flowElement) {
            return flowElement instanceof SequenceFlow;
        }

        @Override
        public boolean needPeek(FlowElement flowElement, ContextStoryBus contextStoryBus) {
            return true;
        }
    }, new PeekStrategy() {

        /**
         * Task
         */
        @Override
        public boolean match(FlowElement flowElement) {
            return flowElement instanceof Task;
        }

        @Override
        public boolean needPeek(FlowElement flowElement, ContextStoryBus contextStoryBus) {
            return PeekStrategyRepository.needPeek((SequenceFlow) flowElement, contextStoryBus.getStoryBus());
        }
    }, new PeekStrategy() {

        @Override
        public boolean skip(FlowElement flowElement, ContextStoryBus contextStoryBus) {
            if (flowElement instanceof EndEvent) {
                return allowDoNext(flowElement, contextStoryBus.getPrevElement(), contextStoryBus, true) != ElementAllowNextEnum.ALLOW_NEX;
            }
            return false;
        }

        /**
         * Event
         */
        @Override
        public boolean match(FlowElement flowElement) {
            return flowElement instanceof Event;
        }

        @Override
        public boolean needPeek(FlowElement flowElement, ContextStoryBus contextStoryBus) {
            return PeekStrategyRepository.needPeek((SequenceFlow) flowElement, contextStoryBus.getStoryBus());
        }

        @Override
        public boolean allowOutingEmpty(FlowElement flowElement) {
            return flowElement instanceof EndEvent && CollectionUtils.isEmpty(GlobalUtil.transferNotEmpty(flowElement, EndEvent.class).outingList());
        }
    });

    @SuppressWarnings("all")
    public static ElementAllowNextEnum allowDoNext(FlowElement flowElement, FlowElement prevElement, ContextStoryBus contextStoryBus, boolean actualArrive) {
        ConcurrentHashMap<FlowElement, List<ContextStoryBus.ElementArriveRecord>> joinGatewayComingMap = contextStoryBus.getJoinGatewayComingMap();
        List<ContextStoryBus.ElementArriveRecord> expectedComingElement = joinGatewayComingMap.get(flowElement);
        if (expectedComingElement == null) {
            List<ContextStoryBus.ElementArriveRecord> flowElementList =
                    Lists.newArrayList(flowElement.comingList()).stream().map(ContextStoryBus.ElementArriveRecord::new).collect(Collectors.toList());
            expectedComingElement = joinGatewayComingMap.putIfAbsent(flowElement, flowElementList);
            if (expectedComingElement == null) {
                expectedComingElement = flowElementList;
            }
        }
        synchronized (expectedComingElement) {
            AssertUtil.notNull(prevElement);
            boolean arrive = expectedComingElement.stream().anyMatch(e -> e.elementArrive(prevElement, actualArrive));
            if (arrive && flowElement instanceof EndEvent) {
                EndTaskPedometer endTaskPedometer = contextStoryBus.getEndTaskPedometer();
                AssertUtil.notNull(endTaskPedometer);
                endTaskPedometer.elementCompleted(prevElement);
            }

            if (!expectedComingElement.stream().allMatch(ele -> ele.isArrive())) {
                return ElementAllowNextEnum.NOT_ALLOW_NEX;
            }
            if (expectedComingElement.stream().anyMatch(ele -> ele.isActualArrive())) {
                return ElementAllowNextEnum.ALLOW_NEX;
            }
            return ElementAllowNextEnum.NOT_ALLOW_NEX_NEED_COMPENSATE;
        }
    }

    private static boolean needPeek(SequenceFlow sequenceFlow, StoryBus storyBus) {
        return Optional.of(sequenceFlow).flatMap(Expression::getConditionExpression).map(e -> e.condition(storyBus)).orElse(true);
    }

    public static List<PeekStrategy> getPeekStrategy() {
        return peekStrategyList;
    }
}
