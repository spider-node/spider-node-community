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
package cn.spider.framework.flow.role;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.spider.framework.flow.enums.ServiceNodeType;
import cn.spider.framework.flow.role.permission.Permission;
import cn.spider.framework.flow.role.permission.PermissionAuth;

/**
 * 角色接口
 *
 * @author lykan
 */
public interface Role {

    /**
     * 获取权限名称
     *
     * @return 名称
     */
    String getName();

    /**
     * 判断当前角色是否被允许使用资源
     *
     * @param permissionAuth 资源
     * @return 允许：true
     */
    boolean allowedUseResource(PermissionAuth permissionAuth);

    /**
     * parent role 中不能出现自身的引用
     *
     * @param roleSet 父级角色
     */
    void addParentRole(Set<Role> roleSet);

    /**
     * 获取 parent role
     *
     * @return parent role（副本）
     */
    Set<Role> getParentRole();

    /**
     * 添加权限
     *
     * @param permissionList 资源列表
     */
    void addPermission(List<Permission> permissionList);

    /**
     * 获取 角色具备的权限
     *
     * @return 权限（副本）
     */
    Map<ServiceNodeType, List<Permission>> getPermission();
}
