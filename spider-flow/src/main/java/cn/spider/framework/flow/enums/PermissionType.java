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
package cn.spider.framework.flow.enums;

import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.resource.service.ServiceNodeResource;
import cn.spider.framework.flow.util.AssertUtil;
import cn.spider.framework.flow.util.ExceptionUtil;
import cn.spider.framework.flow.util.KeyUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * 权限类型
 *
 * @author lykan
 */
public enum PermissionType {

    /**
     * 带服务组件名的服务权限
     */
    COMPONENT_SERVICE("pr", ServiceNodeType.SERVICE_TASK),

    /**
     * 带服务组件名的服务能力权限
     */
    COMPONENT_SERVICE_ABILITY("pr", ServiceNodeType.SERVICE_TASK_ABILITY),

    /**
     * 普通服务权限
     */
    SERVICE("r", ServiceNodeType.SERVICE_TASK),

    /**
     * 服务能力权限
     */
    SERVICE_ABILITY("r", ServiceNodeType.SERVICE_TASK_ABILITY);

    /**
     * 权限字符串的前缀
     */
    private final String prefix;

    /**
     * 服务类型
     */
    private final ServiceNodeType serviceNodeType;

    PermissionType(String prefix, ServiceNodeType serviceNodeType) {
        this.prefix = prefix;
        this.serviceNodeType = serviceNodeType;
    }

    public String getPrefix() {
        return prefix;
    }

    public ServiceNodeType getServiceNodeType() {
        return serviceNodeType;
    }

    public Optional<String> getPermissionId(ServiceNodeResource resource) {
        AssertUtil.notNull(resource);
        switch (this) {
            case COMPONENT_SERVICE:
                if (StringUtils.isAnyBlank(resource.getComponentName(), resource.getServiceName())) {
                    return Optional.empty();
                }
                return Optional.of(KeyUtil.pr(resource.getComponentName(), resource.getServiceName()));
            case COMPONENT_SERVICE_ABILITY:
                if (StringUtils.isAnyBlank(resource.getComponentName(), resource.getServiceName(), resource.getAbilityName())) {
                    return Optional.empty();
                }
                return Optional.of(KeyUtil.pr(resource.getComponentName(), resource.getServiceName(), resource.getAbilityName()));
            case SERVICE:
                AssertUtil.notBlank(resource.getServiceName());
                if (StringUtils.isBlank(resource.getServiceName())) {
                    return Optional.empty();
                }
                return Optional.of(KeyUtil.r(resource.getServiceName()));
            case SERVICE_ABILITY:
                if (StringUtils.isAnyBlank(resource.getServiceName(), resource.getAbilityName())) {
                    return Optional.empty();
                }
                return Optional.of(KeyUtil.r(resource.getServiceName(), resource.getAbilityName()));
            default:
                throw ExceptionUtil.buildException(null, ExceptionEnum.SYSTEM_ERROR, null);
        }
    }
}
