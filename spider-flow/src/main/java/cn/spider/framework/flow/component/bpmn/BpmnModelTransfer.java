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

import cn.spider.framework.flow.component.bpmn.builder.SubProcessLink;
import cn.spider.framework.flow.component.bpmn.link.ProcessLink;
import cn.spider.framework.flow.resource.config.ConfigResource;

import java.util.Map;
import java.util.Optional;

/**
 * 将 BPMN 文件解析成链路流程对象
 *
 * @author lykan
 */
public interface BpmnModelTransfer<T> {

    /**
     * 从 其他 Bpmn Model 转化为 Kstry Model
     *
     * @param config   config
     * @param instance Bpmn Model
     * @param startId  startId
     * @return Bpmn Model
     */
    Optional<ProcessLink> getProcessLink(ConfigResource config, T instance, String startId);

    /**
     * 获取 子流程
     *
     * @param config   config
     * @param instance Bpmn Model
     * @return 子流程
     */
    Map<String, SubProcessLink> getAllSubProcessLink(ConfigResource config, T instance);
}
