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
package cn.spider.framework.flow.role.permission;

import cn.spider.framework.flow.enums.IdentityTypeEnum;
import cn.spider.framework.flow.enums.PermissionType;
import cn.spider.framework.flow.enums.ServiceNodeType;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.resource.identity.BasicIdentity;
import cn.spider.framework.flow.resource.service.ServiceNodeResource;
import cn.spider.framework.flow.util.ExceptionUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author lykan
 */
public class SimplePermission extends BasicIdentity implements Permission {

    /**
     * 权限类型
     */
    private final PermissionType permissionType;

    public SimplePermission(PermissionType permissionType, ServiceNodeResource resource) {
        super(permissionType.getPermissionId(resource)
                .orElseThrow(() -> ExceptionUtil.buildException(null, ExceptionEnum.SYSTEM_ERROR, null)), IdentityTypeEnum.PERMISSION);
        this.permissionType = permissionType;
    }

    @Override
    public PermissionType getPermissionType() {
        return permissionType;
    }

    @Override
    public ServiceNodeType getServiceNodeType() {
        return getPermissionType().getServiceNodeType();
    }

    @Override
    public boolean auth(Permission permission) {
        if (permission == null) {
            return false;
        }
        return permissionType == permission.getPermissionType() && Objects.equals(permission.getIdentityId(), this.getIdentityId());
    }

    @Override
    public boolean auth(List<Permission> permissionList) {
        if (CollectionUtils.isEmpty(permissionList)) {
            return false;
        }
        return permissionList.stream().anyMatch(this::auth);
    }
}
