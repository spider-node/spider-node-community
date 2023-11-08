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
package cn.spider.framework.flow.container.task.impl;

import cn.spider.framework.flow.container.component.MethodWrapper;
import cn.spider.framework.flow.container.task.TaskServiceWrapper;
import cn.spider.framework.flow.enums.ServiceNodeType;
import cn.spider.framework.flow.resource.identity.BasicIdentity;
import cn.spider.framework.flow.resource.service.ServiceNodeResourceAuth;
import cn.spider.framework.flow.role.Role;
import cn.spider.framework.flow.role.ServiceTaskRole;
import cn.spider.framework.flow.util.AssertUtil;

import javax.annotation.Nonnull;

/**
 * 服务节点包装类
 *
 * @author lykan
 */
public class AbilityTaskServiceWrapper extends BasicIdentity implements TaskServiceWrapper {

    /**
     * 服务节点组件代理类
     */
    protected final TaskComponentProxy target;

    /**
     * 服务节点方法包装类
     */
    private final MethodWrapper methodWrapper;

    /**
     * 服务节点标
     */
    private final ServiceNodeResourceAuth serviceNodeResource;

    public AbilityTaskServiceWrapper(TaskComponentProxy target, MethodWrapper methodWrapper, ServiceNodeResourceAuth serviceNodeResource) {
        super(serviceNodeResource.getIdentityId(), serviceNodeResource.getServiceNodeType().getType());

        AssertUtil.anyNotNull(target, methodWrapper, serviceNodeResource);
        this.target = target;
        this.methodWrapper = methodWrapper;
        this.serviceNodeResource = serviceNodeResource;
    }

    @Override
    public String getName() {
        return serviceNodeResource.getServiceName();
    }

    @Override
    public TaskComponentProxy getTarget() {
        return target;
    }

    @Override
    public MethodWrapper getMethodWrapper() {
        return methodWrapper;
    }

    @Override
    public ServiceNodeResourceAuth getServiceNodeResource() {
        return serviceNodeResource;
    }

    @Override
    public ServiceNodeType getServiceNodeType() {
        return serviceNodeResource.getServiceNodeType();
    }

    @Override
    public boolean match(@Nonnull Role role) {
        if (getServiceNodeType() == ServiceNodeType.SERVICE_TASK && (role instanceof ServiceTaskRole)) {
            return true;
        }
        return role.allowedUseResource(serviceNodeResource);
    }
}
