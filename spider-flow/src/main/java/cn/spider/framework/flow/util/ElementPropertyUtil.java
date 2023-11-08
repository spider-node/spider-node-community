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
package cn.spider.framework.flow.util;

import cn.spider.framework.flow.bpmn.BpmnElement;
import cn.spider.framework.flow.bpmn.FlowElement;
import cn.spider.framework.flow.bpmn.Gateway;
import cn.spider.framework.flow.bpmn.extend.AggregationFlowElement;
import cn.spider.framework.flow.bpmn.extend.AsyncFlowElement;
import cn.spider.framework.flow.exception.ExceptionEnum;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ElementPropertyUtil
 *
 * @author lykan
 */
public class ElementPropertyUtil {

    /**
     *
     * @param element element
     * @return true：是
     */
    public static boolean isSupportAggregation(BpmnElement element) {
        if (element == null) {
            return false;
        }
        return element instanceof AggregationFlowElement;
    }

    /**
     * 是否需要开启异步
     *
     * @param flowElement flowElement
     * @return boolean
     */
    public static boolean needGatewayOpenAsync(FlowElement flowElement) {
        if (!(flowElement instanceof AsyncFlowElement && flowElement instanceof Gateway)) {
            return false;
        }
        AsyncFlowElement asyncFlowElement = GlobalUtil.transferNotEmpty(flowElement, AsyncFlowElement.class);
        return BooleanUtils.isTrue(asyncFlowElement.openAsync());
    }

    public static Optional<String> getNodeProperty(FlowNode flowNode, String name) {
        List<Pair<String, String>> list = getNodeProperty(flowNode, name, false, true);
        if (CollectionUtils.isEmpty(list)) {
            return Optional.empty();
        }
        Pair<String, String> pair = list.get(0);
        return Optional.ofNullable(pair.getRight()).filter(StringUtils::isNotBlank).map(String::trim);
    }

    public static List<Pair<String, String>> getNodeProperty(FlowNode flowNode, String name, boolean isLike, boolean oneSize) {
        AssertUtil.notBlank(name);
        if (flowNode == null) {
            return Lists.newArrayList();
        }

        ExtensionElements extensionElements = flowNode.getExtensionElements();
        if (extensionElements == null) {
            return Lists.newArrayList();
        }

        Collection<CamundaProperties> camundaProperties = extensionElements.getChildElementsByType(CamundaProperties.class);
        if (CollectionUtils.isEmpty(camundaProperties)) {
            return Lists.newArrayList();
        }

        List<Pair<String, String>> resultList = Lists.newArrayList();
        for (CamundaProperties camundaProperty : camundaProperties) {
            Collection<CamundaProperty> ps = camundaProperty.getCamundaProperties();
            if (CollectionUtils.isEmpty(ps)) {
                continue;
            }
            List<Pair<String, String>> list = ps.stream().filter(p -> StringUtils.isNotBlank(p.getCamundaName()))
                    .filter(p -> {
                        if (isLike) {
                            return p.getCamundaName().trim().toLowerCase(Locale.ROOT).startsWith(name);
                        }
                        return Objects.equals(p.getCamundaName().trim().toLowerCase(Locale.ROOT), name);
                    })
                    .map(prop -> Pair.of(prop.getCamundaName(), prop.getCamundaValue())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(list)) {
                resultList.addAll(list);
            }
        }
        if (CollectionUtils.isNotEmpty(resultList)) {
            List<String> duplicateList = resultList.stream().map(Pair::getLeft).filter(StringUtils::isNotBlank).collect(Collectors.toMap(e -> e, e -> 1, Integer::sum))
                    .entrySet().stream().filter(e -> e.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.toList());
            AssertUtil.isEmpty(duplicateList, ExceptionEnum.CONFIGURATION_PARSE_FAILURE, "Configuration file component properties are repeatedly defined. duplicateList: {}", duplicateList);
        }
        AssertUtil.isTrue((!oneSize || resultList.size() <= 1), ExceptionEnum.CONFIGURATION_PARSE_FAILURE, "Configuration file component properties are repeatedly defined. prop: {}", resultList);
        return resultList;
    }
}
