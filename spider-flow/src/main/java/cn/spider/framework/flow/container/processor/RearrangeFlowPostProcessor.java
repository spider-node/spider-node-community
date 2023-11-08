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
package cn.spider.framework.flow.container.processor;
import cn.spider.framework.flow.bpmn.*;
import cn.spider.framework.flow.bpmn.extend.ServiceTaskSupport;
import cn.spider.framework.flow.bpmn.impl.FlowElementImpl;
import cn.spider.framework.flow.bpmn.impl.InclusiveGatewayImpl;
import cn.spider.framework.flow.bpmn.impl.SequenceFlowImpl;
import cn.spider.framework.flow.bpmn.impl.ServiceTaskImpl;
import cn.spider.framework.flow.component.bpmn.DiagramTraverseSupport;
import cn.spider.framework.flow.container.component.TaskContainer;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.resource.service.ServiceNodeResource;
import cn.spider.framework.flow.util.AssertUtil;
import cn.spider.framework.flow.util.ElementParserUtil;
import cn.spider.framework.flow.util.ExceptionUtil;
import cn.spider.framework.flow.util.GlobalUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 重排链路
 *  - 如果排他网关、包含网关中指定了 TaskService，这个流程会把指定的 TaskService 单独拎出来作为执行节点加入到链路中
 *
 * @author lykan
 */
public class RearrangeFlowPostProcessor extends DiagramTraverseSupport<Object> implements StartEventPostProcessor {

    private final Map<String, List<ServiceNodeResource>> serviceNodeResourceMap;

    public RearrangeFlowPostProcessor(ApplicationContext applicationContext) {
        Map<String, TaskContainer> taskContainerMap = applicationContext.getBeansOfType(TaskContainer.class);
        AssertUtil.oneSize(taskContainerMap.values());
        Map<String, List<ServiceNodeResource>> serviceNodeResourceMap = Maps.newHashMap();
        taskContainerMap.values().iterator().next().getServiceNodeResource().forEach(resource -> {
            List<ServiceNodeResource> serviceNodeResources = serviceNodeResourceMap.computeIfAbsent(resource.getServiceName(), key -> Lists.newArrayList());
            serviceNodeResources.add(resource);
        });
        this.serviceNodeResourceMap = serviceNodeResourceMap;
    }

    @Override
    public Optional<StartEvent> postStartEvent(StartEvent startEvent) {
        traverse(startEvent);
        return Optional.of(startEvent);
    }

    @Override
    public void doPlainElement(Object course, FlowElement node, SubProcess subProcess) {
        if (node instanceof ServiceTaskSupport) {
            Optional<ServiceTask> serviceTaskOptional = ((ServiceTaskSupport) node).getServiceTask();
            serviceTaskOptional.ifPresent(serviceTask -> doSeparateFlowElement(node, serviceTask));
        }
        if (node instanceof ServiceTask) {
            ServiceNodeResource customRoleInfo = ((ServiceTask) node).getCustomRoleInfo();
            // 支持自定义角色属性
            if (customRoleInfo != null) {
                List<FlowElement> outingList = Lists.newArrayList(node.outingList());
                AssertUtil.notEmpty(outingList);
                node.clearOutingChain();

                SequenceFlowImpl sequenceFlow = new SequenceFlowImpl();
                sequenceFlow.setId(GlobalUtil.uuid());
                ServiceTask serviceTask = ServiceTask.builder().service(customRoleInfo.getServiceName()).component(customRoleInfo.getComponentName()).ins();
                ElementParserUtil.tryFillTaskName(GlobalUtil.transferNotEmpty(serviceTask, ServiceTaskImpl.class), serviceNodeResourceMap.get(serviceTask.getTaskService()));
                node.outing(sequenceFlow);
                sequenceFlow.outing(serviceTask);
                outingList.forEach(serviceTask::outing);
            }
        }
    }

    private void doSeparateFlowElement(FlowElement node, ServiceTask serviceTask) {
        AssertUtil.isTrue(node instanceof ServiceTaskSupport);

        FlowElementImpl serviceTaskImpl = GlobalUtil.transferNotEmpty(serviceTask, FlowElementImpl.class);
        serviceTaskImpl.setId("Service-Task-" + node.getId());
        serviceTaskImpl.setName(node.getName());

        if (node instanceof InclusiveGateway) {
            List<FlowElement> nodeComingList = node.comingList();
            AssertUtil.isTrue(nodeComingList.size() > 0);

            InclusiveGatewayImpl mockInclusiveGateway = new InclusiveGatewayImpl();
            mockInclusiveGateway.setId("Inclusive-Gateway-" + node.getId());
            Lists.newArrayList(nodeComingList).forEach(flowElement -> {
                FlowElementImpl beforeSequenceFlow = GlobalUtil.transferNotEmpty(flowElement, FlowElementImpl.class);
                beforeSequenceFlow.clearOutingChain();
                beforeSequenceFlow.outing(mockInclusiveGateway);
            });

            SequenceFlowImpl sequenceFlow1 = new SequenceFlowImpl();
            sequenceFlow1.setId(GlobalUtil.uuid());
            mockInclusiveGateway.outing(sequenceFlow1);
            sequenceFlow1.outing(serviceTaskImpl);

            SequenceFlowImpl sequenceFlow2 = new SequenceFlowImpl();
            sequenceFlow2.setId(GlobalUtil.uuid());
            serviceTaskImpl.outing(sequenceFlow2);
            sequenceFlow2.outing(node);

        } else if (node instanceof ExclusiveGateway) {
            List<FlowElement> nodeComingList = node.comingList();
            AssertUtil.oneSize(nodeComingList);

            SequenceFlowImpl sequenceFlow = new SequenceFlowImpl();
            sequenceFlow.setId(GlobalUtil.uuid());

            FlowElementImpl beforeSequenceFlow = GlobalUtil.transferNotEmpty(nodeComingList.get(0), FlowElementImpl.class);
            beforeSequenceFlow.clearOutingChain();
            beforeSequenceFlow.outing(serviceTaskImpl);
            serviceTaskImpl.outing(sequenceFlow);
            sequenceFlow.outing(node);
        } else {
            throw ExceptionUtil.buildException(null, ExceptionEnum.CONFIGURATION_UNSUPPORTED_ELEMENT, "There is a flow analysis that exceeds expectations!");
        }
    }

    @Override
    public int getOrder() {
        return 20;
    }
}
