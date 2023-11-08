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
package cn.spider.framework.flow.component.bpmn;

import cn.spider.framework.flow.bpmn.SequenceFlow;
import cn.spider.framework.flow.bpmn.enums.IterateStrategyEnum;
import cn.spider.framework.flow.bpmn.impl.*;
import cn.spider.framework.flow.bus.InstructContent;
import cn.spider.framework.flow.component.bpmn.builder.InclusiveJoinPointBuilder;
import cn.spider.framework.flow.component.bpmn.builder.ParallelJoinPointBuilder;
import cn.spider.framework.flow.component.bpmn.builder.SubProcessBuilder;
import cn.spider.framework.flow.component.bpmn.builder.SubProcessLink;
import cn.spider.framework.flow.component.bpmn.joinpoint.InclusiveJoinPoint;
import cn.spider.framework.flow.component.bpmn.joinpoint.ParallelJoinPoint;
import cn.spider.framework.flow.component.bpmn.link.ProcessLink;
import cn.spider.framework.flow.component.bpmn.link.StartProcessLink;
import cn.spider.framework.flow.component.utils.BasicInStack;
import cn.spider.framework.flow.component.utils.InStack;
import cn.spider.framework.flow.constant.BpmnElementProperties;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.resource.config.ConfigResource;
import cn.spider.framework.flow.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Camunda 解析器实现
 *
 * @author lykan
 */
public class CamundaBpmnModelTransfer implements BpmnModelTransfer<BpmnModelInstance> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CamundaBpmnModelTransfer.class);

    /**
     * Camunda 中定义的 ServiceTask、Task 类型常量
     */
    private final List<String> CAMUNDA_TASK_TYPE_LIST = Lists.newArrayList(BpmnModelConstants.BPMN_ELEMENT_SERVICE_TASK, BpmnModelConstants.BPMN_ELEMENT_TASK);

    @Override
    public Optional<ProcessLink> getProcessLink(ConfigResource config, BpmnModelInstance instance, String startId) {
        AssertUtil.notNull(config, ExceptionEnum.CONFIGURATION_PARSE_FAILURE);
        if (instance == null || StringUtils.isBlank(startId)) {
            return Optional.empty();
        }
        org.camunda.bpm.model.bpmn.instance.StartEvent camundaStartEvent = instance.getModelElementById(startId);
        if (camundaStartEvent == null || camundaStartEvent.getParentElement() == null || camundaStartEvent.getParentElement().getElementType() == null
                || Objects.equals(BpmnModelConstants.BPMN_ELEMENT_SUB_PROCESS, camundaStartEvent.getParentElement().getElementType().getTypeName())) {
            return Optional.empty();
        }
        StartProcessLink processLink = StartProcessLink.build(camundaStartEvent.getId(), camundaStartEvent.getName());
        doGetStartEvent(config, camundaStartEvent, processLink);
        return Optional.of(processLink);
    }

    @Override
    public Map<String, SubProcessLink> getAllSubProcessLink(ConfigResource config, BpmnModelInstance instance) {
        AssertUtil.notNull(config, ExceptionEnum.CONFIGURATION_PARSE_FAILURE);
        Map<String, SubProcessLink> subProcessMap = Maps.newHashMap();
        if (instance == null) {
            return subProcessMap;
        }

        Collection<org.camunda.bpm.model.bpmn.instance.SubProcess> subProcesses = instance.getModelElementsByType(org.camunda.bpm.model.bpmn.instance.SubProcess.class);
        if (CollectionUtils.isEmpty(subProcesses)) {
            return subProcessMap;
        }
        subProcesses.forEach(sp -> {
            Collection<org.camunda.bpm.model.bpmn.instance.StartEvent> childStartEvent = sp.getChildElementsByType(org.camunda.bpm.model.bpmn.instance.StartEvent.class);
            AssertUtil.oneSize(childStartEvent, ExceptionEnum.CONFIGURATION_SUBPROCESS_ERROR, "SubProcesses are only allowed to also have a start event! fileName: {}", config.getConfigName());
            org.camunda.bpm.model.bpmn.instance.StartEvent startEvent = childStartEvent.iterator().next();

            SubProcessLink subProcessLink = SubProcessLink.build(sp.getId(), sp.getName(), startEvent.getId(), startEvent.getName(), subBpmnLink -> doGetStartEvent(config, startEvent, subBpmnLink));
            ElementPropertyUtil.getNodeProperty(sp, BpmnElementProperties.TASK_STRICT_MODE).map(BooleanUtils::toBooleanObject).filter(b -> !b).ifPresent(b -> subProcessLink.setStrictMode(false));
            ElementPropertyUtil.getNodeProperty(sp, BpmnElementProperties.TASK_TIMEOUT).map(s -> NumberUtils.toInt(s, -1)).filter(i -> i >= 0).ifPresent(subProcessLink::setTimeout);
            fillIterableProperty(config, sp, subProcessLink::setElementIterable);

            SubProcessImpl subProcess = subProcessLink.buildSubDiagramBpmnLink(config).getElement();
            AssertUtil.notTrue(subProcessMap.containsKey(subProcess.getId()), ExceptionEnum.ELEMENT_DUPLICATION_ERROR,
                    "There are duplicate SubProcess ids defined! identity: {}, fileName: {}", subProcess.identity(), config.getConfigName());
            subProcessMap.put(subProcess.getId(), subProcessLink);
        });
        return subProcessMap;
    }

    private void doGetStartEvent(ConfigResource config, org.camunda.bpm.model.bpmn.instance.StartEvent camundaStartEvent, StartProcessLink processLink) {
        if (camundaStartEvent == null) {
            return;
        }

        Map<ProcessLink, ProcessLink> inclusiveProcessLinkMap = Maps.newHashMap();

        Set<org.camunda.bpm.model.bpmn.instance.FlowNode> circularDependencyCheck = Sets.newHashSet();
        Map<org.camunda.bpm.model.bpmn.instance.FlowNode, ProcessLink> nextBpmnLinkMap = Maps.newHashMap();
        nextBpmnLinkMap.put(camundaStartEvent, processLink);

        Map<org.camunda.bpm.model.bpmn.instance.FlowNode, Integer> comingCountMap = Maps.newHashMap();
        InStack<FlowNode> basicInStack = new BasicInStack<>();
        basicInStack.push(camundaStartEvent);
        while (!basicInStack.isEmpty()) {
            org.camunda.bpm.model.bpmn.instance.FlowNode element = basicInStack.pop().orElseThrow(() -> ExceptionUtil.buildException(null, ExceptionEnum.SYSTEM_ERROR, null));
            if (element instanceof org.camunda.bpm.model.bpmn.instance.EndEvent) {
                continue;
            }

            ProcessLink beforeProcessLink = nextBpmnLinkMap.get(element);
            if (element instanceof org.camunda.bpm.model.bpmn.instance.InclusiveGateway) {
                beforeProcessLink = inclusiveProcessLinkMap.getOrDefault(beforeProcessLink, beforeProcessLink);
            }
            for (org.camunda.bpm.model.bpmn.instance.SequenceFlow seq : element.getOutgoing()) {
                FlowNode targetNode = seq.getTarget();
                if (targetNode instanceof org.camunda.bpm.model.bpmn.instance.EndEvent) {
                    beforeProcessLink.end(sequenceFlowMapping(config, seq));
                } else if (targetNode instanceof org.camunda.bpm.model.bpmn.instance.ParallelGateway) {
                    ProcessLink parallelProcessLink = nextBpmnLinkMap.computeIfAbsent(targetNode, node -> {
                        ParallelJoinPointBuilder parallelJoinPointBuilder = processLink.parallel(targetNode.getId());
                        ElementPropertyUtil.getNodeProperty(targetNode,
                                BpmnElementProperties.ASYNC_ELEMENT_OPEN_ASYNC).map(BooleanUtils::toBoolean).filter(b -> b).ifPresent(b -> parallelJoinPointBuilder.openAsync());
                        ElementPropertyUtil.getNodeProperty(targetNode,
                                BpmnElementProperties.TASK_STRICT_MODE).map(BooleanUtils::toBooleanObject).filter(b -> !b).ifPresent(b -> parallelJoinPointBuilder.notStrictMode());
                        return parallelJoinPointBuilder.build();
                    });
                    ProcessLink nextProcessLink = beforeProcessLink.nextParallel(sequenceFlowMapping(config, seq), (ParallelJoinPoint) parallelProcessLink);
                    nextBpmnLinkMap.put(targetNode, nextProcessLink);
                } else if (targetNode instanceof org.camunda.bpm.model.bpmn.instance.InclusiveGateway) {
                    ProcessLink inclusiveProcessLink = nextBpmnLinkMap.computeIfAbsent(targetNode, node -> {

                        ServiceTaskImpl serviceTask = getServiceTask(node, config);
                        InclusiveJoinPoint beforeMockLink = processLink.inclusive(targetNode.getId() + "-Inclusive-" + GlobalUtil.uuid()).build();
                        ProcessLink nextMockLink = instructWrapper(true, targetNode, serviceTask, null, beforeMockLink);

                        InclusiveJoinPointBuilder inclusiveJoinPointBuilder = processLink.inclusive(targetNode.getId());
                        ElementPropertyUtil.getNodeProperty(targetNode,
                                BpmnElementProperties.ASYNC_ELEMENT_OPEN_ASYNC).map(BooleanUtils::toBoolean).filter(b -> b).ifPresent(b -> inclusiveJoinPointBuilder.openAsync());
                        InclusiveJoinPoint actualInclusive = inclusiveJoinPointBuilder.build();
                        if (beforeMockLink == nextMockLink) {
                            return actualInclusive;
                        }

                        nextMockLink.nextInclusive(buildSequenceFlow(targetNode.getId()), actualInclusive);
                        inclusiveProcessLinkMap.put(beforeMockLink, actualInclusive);
                        return beforeMockLink;
                    });
                    beforeProcessLink.nextInclusive(sequenceFlowMapping(config, seq), (InclusiveJoinPoint) inclusiveProcessLink);
                    nextBpmnLinkMap.put(targetNode, inclusiveProcessLink);
                } else if (targetNode instanceof org.camunda.bpm.model.bpmn.instance.ExclusiveGateway) {
                    SequenceFlow sf = sequenceFlowMapping(config, seq);
                    ServiceTaskImpl serviceTask = getServiceTask(targetNode, config);
                    ProcessLink nextProcessLink = instructWrapper(true, targetNode, serviceTask, sf, beforeProcessLink);
                    if (nextProcessLink != beforeProcessLink) {
                        sf = buildSequenceFlow(targetNode.getId());
                    }
                    nextProcessLink = nextProcessLink.nextExclusive(targetNode.getId(), sf).build();
                    nextBpmnLinkMap.put(targetNode, nextProcessLink);
                    basicInStack.push(targetNode);
                } else if (targetNode instanceof org.camunda.bpm.model.bpmn.instance.Task && CAMUNDA_TASK_TYPE_LIST.contains(targetNode.getElementType().getTypeName())) {
                    ServiceTaskImpl serviceTask = getServiceTask(targetNode, config);
                    ProcessLink nextProcessLink = instructWrapper(false, targetNode, serviceTask, sequenceFlowMapping(config, seq), beforeProcessLink);
                    nextBpmnLinkMap.put(targetNode, nextProcessLink);
                    basicInStack.push(targetNode);
                } else if (targetNode instanceof org.camunda.bpm.model.bpmn.instance.SubProcess || targetNode instanceof org.camunda.bpm.model.bpmn.instance.CallActivity) {
                    String subProcessId = targetNode.getId();
                    if (targetNode instanceof org.camunda.bpm.model.bpmn.instance.CallActivity) {
                        subProcessId = ((org.camunda.bpm.model.bpmn.instance.CallActivity) targetNode).getCalledElement();
                    }
                    SubProcessBuilder subProcessBuilder = beforeProcessLink.nextSubProcess(sequenceFlowMapping(config, seq), subProcessId);
                    fillIterableProperty(config, targetNode, subProcessBuilder::iterable);

                    ElementPropertyUtil.getNodeProperty(targetNode,
                            BpmnElementProperties.TASK_STRICT_MODE).map(BooleanUtils::toBooleanObject).filter(b -> !b).ifPresent(b -> subProcessBuilder.notStrictMode());
                    ElementPropertyUtil.getNodeProperty(targetNode, BpmnElementProperties.TASK_TIMEOUT).map(s -> NumberUtils.toInt(s, -1)).filter(i -> i >= 0).ifPresent(subProcessBuilder::timeout);
                    nextBpmnLinkMap.put(targetNode, subProcessBuilder.build());
                    basicInStack.push(targetNode);
                } else {
                    throw ExceptionUtil.buildException(null, ExceptionEnum.CONFIGURATION_UNSUPPORTED_ELEMENT, GlobalUtil.format("There is an error in the bpmn file! fileName: {}", config.getConfigName()));
                }

                if ( isBpmnSupportAggregation(targetNode)) {
                    comingCountMap.merge(targetNode, 1, Integer::sum);
                    if (Objects.equals(comingCountMap.get(targetNode), targetNode.getIncoming().size())) {
                        basicInStack.push(targetNode);
                    }
                } else {
                    AssertUtil.notTrue(circularDependencyCheck.contains(targetNode), ExceptionEnum.CONFIGURATION_FLOW_ERROR,
                            "Duplicate calls between elements are not allowed! fileName: {}, elementId: {}, elementName: {}", config.getConfigName(), targetNode.getId(), targetNode.getName());
                    circularDependencyCheck.add(targetNode);
                }
            }
        }
    }

    private boolean isBpmnSupportAggregation(org.camunda.bpm.model.bpmn.instance.FlowElement element) {
        return element instanceof org.camunda.bpm.model.bpmn.instance.EndEvent
                || element instanceof org.camunda.bpm.model.bpmn.instance.ParallelGateway
                || element instanceof org.camunda.bpm.model.bpmn.instance.InclusiveGateway;
    }

    private ServiceTaskImpl getServiceTask(FlowNode flowNode, ConfigResource config) {
        ServiceTaskImpl serviceTaskImpl = new ServiceTaskImpl();
        serviceTaskImpl.setId(flowNode.getId());
        serviceTaskImpl.setName(flowNode.getName());
        AssertUtil.notBlank(serviceTaskImpl.getId(), ExceptionEnum.CONFIGURATION_ATTRIBUTES_REQUIRED, "The bpmn element id attribute cannot be empty! fileName: {}", config.getConfigName());
        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.SERVICE_TASK_TASK_COMPONENT).ifPresent(serviceTaskImpl::setTaskComponent);
        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.SERVICE_TASK_TASK_SERVICE).ifPresent(serviceTaskImpl::setTaskService);
        // 事务组标识
        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.TASK_TRANSACTION_GROUP_ID).ifPresent(serviceTaskImpl::setTransactionGroupId);
        // service_类型-- 等待节点，需要被唤醒才能让流程继续走
        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.SERVICE_TASK_TYPE).ifPresent(serviceTaskImpl::setServiceTaskType);
        // 设置轮询次数
        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.POLL_COUNT).ifPresent(serviceTaskImpl::setPollCount);

        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.VERIFY_COUNT).ifPresent(serviceTaskImpl::setVerifyInfo);
        // 设置延迟时间
        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.DELAY_TIME).ifPresent(serviceTaskImpl::setDelayTime);

        // 配置字段隐射-用于参数转换
        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.FIELD_MAPPING).ifPresent(serviceTaskImpl::setFieldMapping);

        // 配置回溯节点
        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.BACK_ID).ifPresent(serviceTaskImpl::setBackId);

        // 节点信息需要为异步的标识
        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.ASYNC).ifPresent(serviceTaskImpl::setAsync);
        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.RETRY_COUNT).ifPresent(serviceTaskImpl::setRetryCount);
        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.SERVICE_TASK_TASK_PROPERTY).ifPresent(serviceTaskImpl::setTaskProperty);
        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.SERVICE_TASK_TASK_PARAMS).ifPresent(serviceTaskImpl::setTaskParams);
        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.SERVICE_TASK_CUSTOM_ROLE).flatMap(CustomRoleInfo::buildCustomRole).ifPresent(serviceTaskImpl::setCustomRoleInfo);
        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.TASK_ALLOW_ABSENT).map(BooleanUtils::toBooleanObject).ifPresent(serviceTaskImpl::setAllowAbsent);
        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.TASK_STRICT_MODE).map(BooleanUtils::toBooleanObject).ifPresent(serviceTaskImpl::setStrictMode);
        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.TASK_TIMEOUT).map(s -> NumberUtils.toInt(s, -1)).filter(i -> i >= 0).ifPresent(serviceTaskImpl::setTimeout);
        fillIterableProperty(config, flowNode, serviceTaskImpl::mergeElementIterable);
        return serviceTaskImpl;
    }

    private ProcessLink instructWrapper(boolean allowEmpty, FlowNode targetNode, ServiceTaskImpl serviceTask, SequenceFlow firstSequenceFlow, ProcessLink nextProcessLink) {
        ProcessLink before = nextProcessLink;
        String nodeProperty = ElementPropertyUtil.getNodeProperty(targetNode, BpmnElementProperties.SERVICE_TASK_TASK_PROPERTY).orElse(null);

        List<InstructContent> beforeInstructContentList = getInstructContentList(targetNode, true);
        if (CollectionUtils.isNotEmpty(beforeInstructContentList)) {
            for (InstructContent instructContent : beforeInstructContentList) {
                SequenceFlow sf;
                if (firstSequenceFlow != null) {
                    sf = firstSequenceFlow;
                    firstSequenceFlow = null;
                } else {
                    sf = buildSequenceFlow(targetNode.getId());
                }
                nextProcessLink = nextProcessLink.nextInstruct(sf, instructContent.getInstruct()
                        .substring(1), instructContent.getContent()).property(nodeProperty).id(targetNode.getId() + "-Instruct-" + GlobalUtil.uuid()).build();
            }
        }

        if (serviceTask.validTask()) {
            SequenceFlow sf;
            if (firstSequenceFlow != null) {
                sf = firstSequenceFlow;
                firstSequenceFlow = null;
            } else {
                sf = buildSequenceFlow(targetNode.getId());
            }
            nextProcessLink = nextProcessLink.nextTask(sf, serviceTask);
        }

        List<InstructContent> afterInstructContentList = getInstructContentList(targetNode, false);
        if (CollectionUtils.isNotEmpty(afterInstructContentList)) {
            for (InstructContent instructContent : afterInstructContentList) {
                SequenceFlow sf;
                if (firstSequenceFlow != null) {
                    sf = firstSequenceFlow;
                    firstSequenceFlow = null;
                } else {
                    sf = buildSequenceFlow(targetNode.getId());
                }
                nextProcessLink = nextProcessLink.nextInstruct(sf,
                        instructContent.getInstruct(), instructContent.getContent()).property(nodeProperty).id(targetNode.getId() + "-Instruct-" + GlobalUtil.uuid()).build();
            }
        }
        AssertUtil.isTrue(allowEmpty || before != nextProcessLink,
                ExceptionEnum.CONFIGURATION_ATTRIBUTES_REQUIRED, "Invalid serviceNode definition, please add the necessary attributes! elementId: {}", targetNode.getId());
        return nextProcessLink;
    }

    public List<InstructContent> getInstructContentList(FlowNode flowNode, boolean isBefore) {
        List<Pair<String, String>> instructPairList =
                ElementPropertyUtil.getNodeProperty(flowNode, (isBefore ? "^" : StringUtils.EMPTY) + BpmnElementProperties.SERVICE_TASK_TASK_INSTRUCT, true, false);
        if (CollectionUtils.isEmpty(instructPairList)) {
            return Lists.newArrayList();
        }
        return instructPairList.stream().filter(instructPair -> StringUtils.isNotBlank(instructPair.getLeft()))
                .map(instructPair -> new InstructContent(Optional.of(instructPair.getLeft()).map(String::trim).orElse(null), instructPair.getRight())).collect(Collectors.toList());
    }

    private void fillIterableProperty(ConfigResource config, FlowNode flowNode, Consumer<BasicElementIterable> setConsumer) {
        BasicElementIterable elementIterable = new BasicElementIterable();
        Optional<String> iteSourceProperty = ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.ITERATE_SOURCE);
        if (iteSourceProperty.filter(s -> {
            if (ElementParserUtil.isValidDataExpression(s)) {
                return true;
            }
            LOGGER.warn("[{}] The set ite-source being iterated over is invalid. fileName: {}, calledElementId: {}", ExceptionEnum.BPMN_ATTRIBUTE_INVALID.getExceptionCode(), config.getConfigName(), flowNode.getId());
            return false;
        }).map(StringUtils::isNotBlank).orElse(false)) {
            elementIterable.setIteSource(iteSourceProperty.get());
        }
        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.ITERATE_ASYNC).map(BooleanUtils::toBoolean).ifPresent(elementIterable::setOpenAsync);
        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.ITERATE_STRATEGY).flatMap(IterateStrategyEnum::of).ifPresent(elementIterable::setIteStrategy);
        ElementPropertyUtil.getNodeProperty(flowNode, BpmnElementProperties.ITERATE_STRIDE).map(NumberUtils::toInt).ifPresent(elementIterable::setStride);
        setConsumer.accept(elementIterable);
    }

    private SequenceFlow sequenceFlowMapping(ConfigResource config, org.camunda.bpm.model.bpmn.instance.SequenceFlow sf) {
        SequenceFlowImpl sequenceFlow = new SequenceFlowImpl();
        sequenceFlow.setId(sf.getId());
        sequenceFlow.setName(sf.getName());
        AssertUtil.notBlank(sequenceFlow.getId(), ExceptionEnum.CONFIGURATION_ATTRIBUTES_REQUIRED, "The bpmn element id attribute cannot be empty! fileName: {}", config.getConfigName());
        if (sf.getConditionExpression() != null && StringUtils.isNotBlank(sf.getConditionExpression().getTextContent())) {
            SequenceFlowExpression sequenceFlowExpression = new SequenceFlowExpression(sf.getConditionExpression().getTextContent());
            sequenceFlowExpression.setId(sf.getConditionExpression().getId());
            sequenceFlowExpression.setName(sf.getConditionExpression().getTextContent());
            sequenceFlow.setExpression(sequenceFlowExpression);
        }
        return sequenceFlow;
    }

    private SequenceFlow buildSequenceFlow(String id) {
        SequenceFlowImpl sequenceFlow = new SequenceFlowImpl();
        sequenceFlow.setId(String.format("%s-Sequence-%s", id, GlobalUtil.uuid()));
        sequenceFlow.setName(sequenceFlow.getId());
        return sequenceFlow;
    }
}