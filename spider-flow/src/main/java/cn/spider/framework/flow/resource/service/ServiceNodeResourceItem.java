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

import cn.spider.framework.flow.enums.IdentityTypeEnum;
import cn.spider.framework.flow.enums.ServiceNodeType;
import cn.spider.framework.flow.resource.identity.BasicIdentity;
import cn.spider.framework.flow.util.KeyUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 服务节点资源项
 *
 * @author lykan
 */
public class ServiceNodeResourceItem extends BasicIdentity implements ServiceNodeResourceAuth {

    /**
     * 服务组件名
     */
    private final String componentName;

    /**
     * 服务节点名
     */
    private final String serviceName;

    /**
     * 服务能力名
     */
    private final String abilityName;

    /**
     * 资源描述信息
     */
    private final String description;

    public ServiceNodeResourceItem(String componentName, String serviceName, String abilityName, String description) {
        super(KeyUtil.pr(componentName, serviceName, abilityName), IdentityTypeEnum.SERVICE_NODE_RESOURCE);

        this.componentName = componentName;
        this.serviceName = serviceName;
        this.abilityName = abilityName;
        this.description = description;
    }

    @Override
    public String getComponentName() {
        return componentName;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public String getAbilityName() {
        return abilityName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ServiceNodeType getServiceNodeType() {
        return StringUtils.isBlank(getAbilityName()) ? ServiceNodeType.SERVICE_TASK : ServiceNodeType.SERVICE_TASK_ABILITY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ServiceNodeResourceItem that = (ServiceNodeResourceItem) o;
        return Objects.equals(componentName, that.componentName) && Objects.equals(serviceName, that.serviceName) && Objects.equals(abilityName, that.abilityName) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), componentName, serviceName, abilityName, description);
    }
}
