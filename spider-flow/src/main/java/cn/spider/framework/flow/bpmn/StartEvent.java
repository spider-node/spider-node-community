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
package cn.spider.framework.flow.bpmn;

import cn.spider.framework.flow.resource.config.ConfigResource;

import java.util.Optional;

/**
 * StartEvent
 */
public interface StartEvent extends Event {

    /**
     * 获取 Config
     *
     * @return Config
     */
    Optional<ConfigResource> getConfig();

    /**
     * 获取 EndEvent
     *
     * @return EndEvent
     */
    EndEvent getEndEvent();

    /**
     * 设置 Config
     *
     * @param config Config
     */
    void setConfig(ConfigResource config);

    /**
     * 设置 EndEvent
     *
     * @param endEvent 非空
     */
    void setEndEvent(EndEvent endEvent);
}
