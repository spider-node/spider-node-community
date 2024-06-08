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
package cn.spider.framework.flow.engine;

import cn.spider.framework.flow.bpmn.FlowElement;
import cn.spider.framework.flow.bpmn.ParallelGateway;
import cn.spider.framework.flow.bpmn.SequenceFlow;
import cn.spider.framework.flow.bpmn.StartEvent;
import cn.spider.framework.flow.bus.ContextStoryBus;
import cn.spider.framework.flow.component.strategy.NeedResult;
import cn.spider.framework.flow.component.strategy.PeekStrategy;
import cn.spider.framework.flow.component.strategy.PeekStrategyRepository;
import cn.spider.framework.flow.engine.facade.StoryRequest;
import cn.spider.framework.flow.engine.future.AdminFuture;
import cn.spider.framework.flow.enums.ElementAllowNextEnum;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.monitor.MonitorTracking;
import cn.spider.framework.flow.monitor.TrackingStack;
import cn.spider.framework.flow.util.AssertUtil;
import cn.spider.framework.flow.util.ExceptionUtil;
import cn.spider.framework.flow.util.GlobalUtil;
import com.google.common.collect.Lists;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 流程寄存器
 *
 * @author lykan
 */
public class FlowRegister {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowRegister.class);

    /**
     * 请求 ID
     */
    private String requestId;

    /**
     * FlowRegister 开始节点
     */
    private FlowElement startElement;

    /**
     * 上一个执行的 FlowElement
     */
    private FlowElement prevElement;

    /**
     * 执行栈
     */
    private TrackingStack flowElementStack;

    /**
     * 用来保存 并行网关或者包含网关 与 入度的对应关系。
     * - 遇到 并行网关或者包含网关 时，保存对应关系。K：网关  V：网关入度列表
     * - 网关入度到来且执行成功，Set<FlowElement> 移除当前入度。直到Set成为空集，即可继续执行
     */
    private ConcurrentHashMap<FlowElement, List<ContextStoryBus.ElementArriveRecord>> joinGatewayComingMap = new ConcurrentHashMap<>();

    /**
     * 链路追踪器
     */
    private MonitorTracking monitorTracking;

    /**
     * TaskFuture 管理类
     */
    private AdminFuture adminFuture;

    /**
     * 流程开始事件的ID
     */
    private String startEventId;

    /**
     * 主任务开始事件的ID
     */
    private String storyId;


    public FlowRegister(FlowElement startEvent, StoryRequest<?> storyRequest) {
        AssertUtil.isTrue(startEvent instanceof StartEvent);

        // init start event
        startElement = startEvent;
        startEventId = startEvent.getId();
        storyId = startEvent.getId();

        // init requestId
        requestId = GlobalUtil.getOrSetRequestId(storyRequest);

        // init monitor tracking
        monitorTracking = new MonitorTracking(startEvent, storyRequest.getTrackingType());

        // init stack
        flowElementStack = monitorTracking.newTrackingStack();
        flowElementStack.push(null, startEvent);
    }

    public MonitorTracking getMonitorTracking() {
        AssertUtil.notNull(monitorTracking);
        return monitorTracking;
    }

    public String getRequestId() {
        AssertUtil.notBlank(requestId);
        return requestId;
    }

    public AdminFuture getAdminFuture() {
        return adminFuture;
    }

    public FlowElement getStartElement() {
        return startElement;
    }

    public void setAdminFuture(AdminFuture adminFuture) {
        AssertUtil.isNull(this.adminFuture);
        this.adminFuture = adminFuture;
    }

    public String getStoryId() {
        return storyId;
    }

    public String getStartEventId() {
        return startEventId;
    }

    public Optional<FlowElement> nextElement(ContextStoryBus contextStoryBus) {
        return monitorTracking.trackingNextElement(doNextElement(contextStoryBus).orElse(null));
    }

    private Optional<FlowElement> doNextElement(ContextStoryBus contextStoryBus) {

        Optional<FlowElement> elementOptional = flowElementStack.pop();
        if (!elementOptional.isPresent()) {
            return Optional.empty();
        }

        // 获取当前执行节点
        FlowElement currentFlowElement = elementOptional.get();
        monitorTracking.buildNodeTracking(currentFlowElement);

        // 获取执行决策
        PeekStrategy peekStrategy = getPeekStrategy(currentFlowElement);

        // 填充 ContextStoryBus
        contextStoryBus.setPrevElement(prevElement);
        contextStoryBus.setJoinGatewayComingMap(joinGatewayComingMap);

        // 是否跳过当前节点继续执行下一个
        if (peekStrategy.skip(currentFlowElement, contextStoryBus)) {
            prevElement = currentFlowElement;
            return nextElement(new ContextStoryBus(contextStoryBus.getStoryBus()));
        }
        return elementOptional;
    }


    public Future<Void> predictNextElementNew(ContextStoryBus contextStoryBus, FlowElement currentFlowElement) {

        if (contextStoryBus.getEndTaskPedometer() == null) {
            contextStoryBus.setPrevElement(prevElement);
            contextStoryBus.setJoinGatewayComingMap(joinGatewayComingMap);
            // contextStoryBus.setEndTaskPedometer();
        }

        AssertUtil.notNull(currentFlowElement);
        Optional<FlowElement> elementOptional = Optional.of(currentFlowElement);
        // 匹配可参与执行的子分支
        PeekStrategy peekStrategy = getPeekStrategy(currentFlowElement);
        List<FlowElement> flowLists = elementOptional.get().outingList();

        if (!peekStrategy.allowOutingEmpty(currentFlowElement)) {
            AssertUtil.notEmpty(flowLists, ExceptionEnum.STORY_FLOW_ERROR, "Match to the next process node as empty! identity: {}", currentFlowElement.identity());
        }

        Map<String,FlowElement> flowElementMap = flowLists.stream().collect(Collectors.toMap(FlowElement::getId, Function.identity()));
        List<Future> needFutures = new ArrayList<>();
        for (FlowElement flowElement : flowLists) {
            Future<NeedResult> needFuture = peekStrategy.needPeek(flowElement, contextStoryBus);
            needFutures.add(needFuture);
        }
        Promise<Void> promise = Promise.promise();
        CompositeFuture.all(needFutures).onSuccess(suss -> {
            List<FlowElement> flowList = new ArrayList<>();
            int size = suss.size();
            for (int i = 0; i < size; i++) {
                NeedResult need = suss.resultAt(i);
                if (need.getNeed()) {
                    flowList.add(flowElementMap.get(need.getId()));
                }
            }
            // 是否允许节点为空
            if (!peekStrategy.allowOutingEmpty(currentFlowElement)) {
                if(CollectionUtils.isEmpty(flowList)){
                    promise.fail("Match to the next process node as empty! identity: "+ currentFlowElement.identity());
                    return;
                }
                //AssertUtil.notEmpty(flowList, ExceptionEnum.STORY_FLOW_ERROR, "Match to the next process node as empty! identity: {}", currentFlowElement.identity());
            }
            prevElement = currentFlowElement;
            flowElementStack.pushList(elementOptional.get(), flowList);
            // 无需执行的子流程，可能会参与驱动之后的流程
            if (!Objects.equals(flowList.size(), elementOptional.get().outingList().size())) {
                processNotMatchElement(contextStoryBus, flowList, elementOptional.get());
            }
            promise.complete();
        }).onFailure(fail -> {
            promise.fail(fail);
        });
        return promise.future();
    }


    private PeekStrategy getPeekStrategy(FlowElement currentFlowElement) {
        Optional<PeekStrategy> peekStrategyOptional = PeekStrategyRepository.getPeekStrategy().stream().filter(peekStrategy -> peekStrategy.match(currentFlowElement)).findFirst();
        return peekStrategyOptional.orElseThrow(() -> ExceptionUtil.buildException(null, ExceptionEnum.CONFIGURATION_UNSUPPORTED_ELEMENT, null));
    }

    private void processNotMatchElement(ContextStoryBus contextStoryBus, List<FlowElement> flowList, FlowElement element) {
        List<FlowElement> notNeedPeekList = Lists.newArrayList(element.outingList());
        notNeedPeekList.removeAll(flowList);
        notNeedPeekList.forEach(notNeedPeekElement -> {
            SequenceFlow notNeedPeekSequenceFlow = GlobalUtil.transferNotEmpty(notNeedPeekElement, SequenceFlow.class);
            notNeedPeekSequenceFlow.getEndElementList().forEach(endElement -> {
                List<FlowElement> comingList = endElement.comingList().stream().filter(e -> e.getFlowTrack().contains(
                        notNeedPeekSequenceFlow.getIndex()) || Objects.equals(e.getIndex(), notNeedPeekSequenceFlow.getIndex())).collect(Collectors.toList());
                if (endElement instanceof ParallelGateway && ((ParallelGateway) endElement).isStrictMode() && CollectionUtils.isNotEmpty(comingList)) {
                    throw ExceptionUtil.buildException(null, ExceptionEnum.STORY_FLOW_ERROR, GlobalUtil.format(
                            "A process branch that cannot reach the ParallelGateway appears! identity: {}", notNeedPeekSequenceFlow.identity()));
                }
                comingList.forEach(coming -> {
                    ElementAllowNextEnum allowNextEnum = PeekStrategyRepository.allowDoNext(endElement, coming, contextStoryBus, false);
                    if (allowNextEnum == ElementAllowNextEnum.ALLOW_NEX) {
                        AssertUtil.isTrue(coming instanceof SequenceFlow);
                        flowElementStack.push(coming.comingList().get(0), coming);
                        LOGGER.debug("The last incoming degree is executed, opening the next event flow! event: {}, coming: {}", endElement.identity(), coming.identity());
                    } else if (allowNextEnum == ElementAllowNextEnum.NOT_ALLOW_NEX_NEED_COMPENSATE) {
                        processNotMatchElement(contextStoryBus, Lists.newArrayList(), endElement);
                    }
                });
            });
        });
    }
}
