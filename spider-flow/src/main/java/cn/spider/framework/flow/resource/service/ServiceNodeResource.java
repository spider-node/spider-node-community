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
package cn.spider.framework.flow.resource.service;

import org.apache.commons.lang3.StringUtils;

/**
 * 服务节点资源
 *
 * @author lykan
 */
public interface ServiceNodeResource extends ServiceNodeIdentity {

    /**
     * 获取服务组件名
     *
     * @return 服务组件名
     */
    String getComponentName();

    /**
     * 获取服务名
     *
     * @return 服务名
     */
    String getServiceName();

    /**
     * 获取能力名
     *
     * @return 能力名
     */
    String getAbilityName();

    /**
     * 获取描述信息
     *
     * @return 资源描述信息
     */
    default String getDescription() {
        return StringUtils.EMPTY;
    }
}
