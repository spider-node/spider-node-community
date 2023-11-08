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
package cn.spider.framework.flow.monitor;

import cn.spider.framework.flow.bpmn.FlowElement;
import cn.spider.framework.flow.bpmn.SequenceFlow;
import cn.spider.framework.flow.bpmn.enums.BpmnTypeEnum;
import cn.spider.framework.flow.component.utils.BasicInStack;
import cn.spider.framework.flow.component.utils.InStack;
import cn.spider.framework.flow.constant.GlobalProperties;
import cn.spider.framework.flow.enums.TrackingTypeEnum;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.util.AssertUtil;
import cn.spider.framework.flow.util.ExceptionUtil;
import cn.spider.framework.flow.util.GlobalUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 链路追踪器
 *
 * @author lykan
 */
public class MonitorTracking {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorTracking.class);

    public static final String BAD_VALUE = "bad value!";

    public static final String BAD_TARGET = "bad target!";

    /**
     * 链路追踪类型
     */
    private final TrackingTypeEnum trackingTypeEnum;

    /**
     * 保存 node tracking
     */
    private final Map<String, NodeTracking> nodeTrackingMap = Maps.newConcurrentMap();

    /**
     * 节点计步器
     */
    private final AtomicInteger invokePedometer = new AtomicInteger(0);

    private final LocalDateTime startTime;

    private final FlowElement startEvent;

    public MonitorTracking(FlowElement startEvent, TrackingTypeEnum trackingTypeEnum) {
        this.startTime = LocalDateTime.now();
        this.startEvent = startEvent;
        this.trackingTypeEnum = Optional.ofNullable(trackingTypeEnum).orElse(TrackingTypeEnum.of(GlobalProperties.STORY_MONITOR_TRACKING_TYPE));
    }

    /**
     * 创建一个带节点监控的栈结构
     *
     * @return 带节点监控的栈结构
     */
    public TrackingStack newTrackingStack() {
        return new TrackingStack(this);
    }

    /**
     * 构建监控节点
     *
     * @param prevElement 被监控的节点
     * @return 监控节点
     */
    public Optional<NodeTracking> buildNodeTracking(FlowElement prevElement) {
        if (prevElement == null || trackingTypeEnum.isNone()) {
            return Optional.empty();
        }
        return Optional.of(nodeTrackingMap.computeIfAbsent(prevElement.getId(), key -> {
            NodeTracking nt = new NodeTracking();
            nt.setNodeId(prevElement.getId());
            nt.setNodeName(prevElement.getName());
            nt.setNodeType(prevElement.getElementType());
            return nt;
        }));
    }

    /**
     * 对 NextElement 进行监控
     *
     * @param flowElement NextElement
     * @return NextElement
     */
    public Optional<FlowElement> trackingNextElement(FlowElement flowElement) {
        buildNodeTracking(flowElement).ifPresent(nodeTracking -> {
            nodeTracking.setIndex(invokePedometer.incrementAndGet());
            getServiceNodeTracking(flowElement).ifPresent(tracking -> tracking.setStartTime(LocalDateTime.now()));
        });
        return Optional.ofNullable(flowElement);
    }

    /**
     * 对 SequenceFlow List 进行监控
     *
     * @param flowList flowList
     */
    public void trackingSequenceFlow(List<FlowElement> flowList) {
        if (CollectionUtils.isEmpty(flowList) || trackingTypeEnum.isNone()) {
            return;
        }
        flowList.stream().map(f -> GlobalUtil.transferNotEmpty(f, SequenceFlow.class)).forEach(flow -> {
            AssertUtil.oneSize(flow.comingList());
            FlowElement flowElement = flow.comingList().get(0);
            buildNodeTracking(flowElement).ifPresent(t -> t.addToNodeId(flow.getId()));
        });
    }

    /**
     * 对 TaskService 入参进行监控
     *
     * @param flowElement flowElement
     * @param paramTrackingSupplier paramTrackingSupplier
     */
    public void trackingNodeParams(FlowElement flowElement, Supplier<ParamTracking> paramTrackingSupplier) {
        AssertUtil.notNull(flowElement);
        getServiceNodeTracking(flowElement).filter(t -> trackingTypeEnum.needServiceDetailTracking()).ifPresent(tracking -> {
            ParamTracking paramTracking = paramTrackingSupplier.get();
            if (paramTracking == null || paramTracking.getValue() == null || GlobalProperties.KSTRY_STORY_TRACKING_PARAMS_LENGTH_LIMIT == 0) {
                return;
            }
            if (GlobalProperties.KSTRY_STORY_TRACKING_PARAMS_LENGTH_LIMIT == -1
                    || (paramTracking.getValue().length() <= GlobalProperties.KSTRY_STORY_TRACKING_PARAMS_LENGTH_LIMIT)) {
                tracking.addParamTracking(paramTracking);
            } else {
                try {
                    tracking.addParamTracking(ParamTracking.build(paramTracking.getParamName(), paramTracking.getValue()
                            .substring(0, GlobalProperties.KSTRY_STORY_TRACKING_PARAMS_LENGTH_LIMIT), paramTracking.getSourceScopeType(), paramTracking.getSourceName()));
                } catch (Exception e) {
                    LOGGER.error("build ParamTracking error! paramTracking: {}", paramTracking, e);
                }
            }
        });
    }

    /**
     * 对执行结果对 StoryBus 的通知进行监控
     *
     * @param flowElement flowElement
     * @param noticeTrackingSupplier noticeTrackingSupplier
     */
    public void trackingNodeNotice(FlowElement flowElement, Supplier<NoticeTracking> noticeTrackingSupplier) {
        AssertUtil.notNull(flowElement);
        getServiceNodeTracking(flowElement).filter(t -> trackingTypeEnum.needServiceDetailTracking()).ifPresent(tracking -> {
            NoticeTracking noticeTracking = noticeTrackingSupplier.get();
            if (noticeTracking == null || noticeTracking.getValue() == null || GlobalProperties.KSTRY_STORY_TRACKING_PARAMS_LENGTH_LIMIT == 0) {
                return;
            }
            if (GlobalProperties.KSTRY_STORY_TRACKING_PARAMS_LENGTH_LIMIT == -1
                    || (noticeTracking.getValue().length() <= GlobalProperties.KSTRY_STORY_TRACKING_PARAMS_LENGTH_LIMIT)) {
                tracking.addNoticeTracking(noticeTracking);
            } else {
                try {
                    tracking.addNoticeTracking(NoticeTracking.build(noticeTracking.getFieldName(), noticeTracking.getNoticeName(),
                            noticeTracking.getNoticeScopeType(), noticeTracking.getValue().substring(0, GlobalProperties.KSTRY_STORY_TRACKING_PARAMS_LENGTH_LIMIT)));
                } catch (Exception e) {
                    LOGGER.error("build NoticeTracking error! noticeTracking: {}", noticeTracking, e);
                }
            }
        });
    }

    /**
     * TaskService 执行完成后监控记录数据
     *
     * @param flowElement flowElement
     * @param exception exception
     */
    public void finishTaskTracking(FlowElement flowElement, Throwable exception) {
        getServiceNodeTracking(flowElement).ifPresent(tracking -> {
            tracking.setEndTime(LocalDateTime.now());
            tracking.setTaskException(exception);
            tracking.setSpendTime(Duration.between(tracking.getStartTime(), tracking.getEndTime()).toMillis());
        });
    }

    public void demotionTaskTracking(FlowElement flowElement, DemotionInfo demotionInfo) {
        getServiceNodeTracking(flowElement).ifPresent(tracking -> tracking.setDemotionInfo(demotionInfo));
    }

    public void iterateCountTracking(FlowElement flowElement, int count, int stride) {
        if (count <= 0) {
            return;
        }
        getServiceNodeTracking(flowElement).ifPresent(tracking -> {
            tracking.setIterateCount(count);
            tracking.setIterateStride(stride);
        });
    }

    public Optional<NodeTracking> getServiceNodeTracking(FlowElement flowElement) {
        if (trackingTypeEnum.notNeedServiceTracking()) {
            return Optional.empty();
        }
        NodeTracking nodeTracking = nodeTrackingMap.get(flowElement.getId());
        AssertUtil.notNull(nodeTracking);
        return Optional.of(nodeTracking);
    }

    public List<NodeTracking> getStoryTracking() {
        if (trackingTypeEnum.isNone()) {
            return Lists.newArrayList();
        }

        if (trackingTypeEnum.isServiceTracking()) {
            nodeTrackingMap.values().stream().filter(nodeTracking -> nodeTracking.getNodeType() == BpmnTypeEnum.SERVICE_TASK)
                    .forEach(serviceTracking -> {
                        if (CollectionUtils.isEmpty(serviceTracking.getToNodeIds())) {
                            return;
                        }
                        Set<String> idSet = new HashSet<>();
                        InStack<String> toNodeIdStack = new BasicInStack<>();
                        toNodeIdStack.pushCollection(serviceTracking.getToNodeIds());
                        while (!toNodeIdStack.isEmpty()) {
                            String id = toNodeIdStack.pop().orElseThrow(() -> ExceptionUtil.buildException(null, ExceptionEnum.SYSTEM_ERROR, null));
                            NodeTracking nodeTracking = nodeTrackingMap.get(id);
                            if (nodeTracking == null) {
                                continue;
                            }
                            if (nodeTracking.getNodeType() == BpmnTypeEnum.SERVICE_TASK) {
                                idSet.add(nodeTracking.getNodeId());
                                continue;
                            }
                            toNodeIdStack.pushCollection(nodeTracking.getToNodeIds());
                        }
                        serviceTracking.refreshToNodeIds(idSet);
                    });
        }
        return nodeTrackingMap.values().stream().peek(nodeTracking -> {
                    if (trackingTypeEnum.notNeedServiceTracking() || nodeTracking.finishService()) {
                        return;
                    }
                    nodeTracking.setEndTime(LocalDateTime.now());
                    nodeTracking.setSpendTime(Duration.between(nodeTracking.getStartTime(), nodeTracking.getEndTime()).toMillis());
                })
                .filter(nodeTracking -> nodeTracking.getIndex() != null)
                .filter(nodeTracking -> !trackingTypeEnum.isServiceTracking() || nodeTracking.getNodeType() == BpmnTypeEnum.SERVICE_TASK)
                .sorted(Comparator.comparing(NodeTracking::getIndex))
                .collect(Collectors.toList());
    }

    public long getSpendTime() {
        return Duration.between(startTime, LocalDateTime.now()).toMillis();
    }

    public void trackingLog() {
        if (!GlobalProperties.KSTRY_STORY_TRACKING_LOG) {
            return;
        }
        List<NodeTracking> storyTracking = getStoryTracking();
        if (CollectionUtils.isNotEmpty(storyTracking)) {
            LOGGER.info("[{}] startId: {}, spend {}ms: {}",
                    ExceptionEnum.STORY_TRACKING_CODE.getExceptionCode(), startEvent.getId(), getSpendTime(), JSON.toJSONString(storyTracking));
        }
    }
}
