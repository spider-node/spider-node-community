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
package cn.spider.framework.flow.resource.config;

import cn.spider.framework.flow.bpmn.StartEvent;
import cn.spider.framework.flow.component.bpmn.builder.SubProcessLink;

import java.util.List;
import java.util.Map;

/**
 * BPMN 配置文件定义
 *
 * @author lykan
 */
public interface BpmnConfigResource extends ConfigResource {

    /**
     * 获取配置文件实例中全部 SubProcess
     * - k：SubProcess id
     * - v: SubProcess 对象
     *
     * @return SubProcess Map
     */
    Map<String, SubProcessLink> getSubProcessMap();

    /**
     * 获取 StartEvent 集合
     *
     * @return StartEvent 集合
     */
    List<StartEvent> getStartEventList();
}
