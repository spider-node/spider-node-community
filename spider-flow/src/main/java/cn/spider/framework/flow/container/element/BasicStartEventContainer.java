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
package cn.spider.framework.flow.container.element;

import cn.spider.framework.flow.bpmn.StartEvent;
import cn.spider.framework.flow.bus.ScopeDataQuery;
import cn.spider.framework.flow.container.processor.StartEventProcessor;
import cn.spider.framework.flow.resource.factory.StartEventFactory;
import cn.spider.framework.flow.util.AssertUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * StartEvent 容器
 *
 * @author dds
 */
@Slf4j
public class BasicStartEventContainer implements StartEventContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicStartEventContainer.class);

    private final StartEventProcessor startEventProcessor;

    /**
     * 保存 StartEvent
     */
    private Map<String, StartEvent> globalStartEventMap;

    /**
     * StartEvent 资源创建工厂
     */
    private final StartEventFactory startEventFactory;

    public BasicStartEventContainer(StartEventFactory startEventFactory, StartEventProcessor startEventProcessor) {
        AssertUtil.anyNotNull(startEventFactory, startEventProcessor);
        this.startEventFactory = startEventFactory;
        this.startEventProcessor = startEventProcessor;
        this.globalStartEventMap = Maps.newHashMap();
    }

    @PostConstruct
    public void refreshStartEvent() {
        List<StartEvent> resourceList = this.startEventFactory.getResourceList();
        Map<String, StartEvent> startEventMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(resourceList)) {
            LOGGER.warn("No bpmn configuration information available!");
            return;
        }
        resourceList.stream().map(event -> startEventProcessor.postStartEvent(event).orElse(null)).filter(Objects::nonNull).forEach(event -> startEventMap.put(event.getId(), event));
        this.globalStartEventMap.putAll(startEventMap);
    }

    public void refreshStartEvent(List<StartEvent> resourceList) {
        log.info("refreshStartEvent-startEvent {}", JSON.toJSONString(resourceList));
        Map<String, StartEvent> startEventMap = Maps.newHashMap();
        resourceList.stream().map(event -> startEventProcessor.postStartEvent(event).orElse(null)).filter(Objects::nonNull).forEach(event -> startEventMap.put(event.getId(), event));
        this.globalStartEventMap.putAll(startEventMap);
        log.info("refreshStartEvent-globalStartEventMap {}", JSON.toJSONString(startEventMap));
    }

    public void removeStartId(Set<String> startIds){
        startIds.forEach(item->{
            this.globalStartEventMap.remove(item);
        });
    }

    @Override
    public Optional<StartEvent> getStartEventById(ScopeDataQuery scopeDataQuery) {
        if (StringUtils.isBlank(scopeDataQuery.getStartId())) {
            return Optional.empty();
        }
        StartEvent startEvent = globalStartEventMap.get(scopeDataQuery.getStartId());
        if (startEvent != null) {
            return Optional.of(startEvent);
        }
        return startEventFactory.getDynamicStartEvent(scopeDataQuery);
    }
}
