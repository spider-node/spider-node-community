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

import cn.spider.framework.flow.bus.ScopeDataQuery;
import cn.spider.framework.flow.component.dynamic.RoleDynamicComponent;
import cn.spider.framework.flow.engine.facade.StoryRequest;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.util.AssertUtil;
import cn.spider.framework.flow.util.ExceptionUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 角色资源库，可以根据业务ID决策使用哪个角色
 *
 * @author lykan
 */
public class BusinessRoleRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessRoleRepository.class);

    private final Cache<String, Optional<BusinessRole>> businessRoleMapping = CacheBuilder.newBuilder()
            .concurrencyLevel(8).initialCapacity(1024).maximumSize(10_000).expireAfterWrite(10, TimeUnit.MINUTES)
            .removalListener(notification -> LOGGER.info("Business role cache lose efficacy. key: {}, value: {}, cause: {}",
                    notification.getKey(), notification.getValue(), notification.getCause())).build();

    private final List<BusinessRole> businessRoleList;
    private final RoleDynamicComponent roleDynamicComponent;

    public BusinessRoleRepository(RoleDynamicComponent roleDynamicComponent, List<BusinessRole> businessRoleList) {
        if (businessRoleList == null) {
            businessRoleList = Lists.newArrayList();
        }
        checkDuplicated(businessRoleList);
        this.roleDynamicComponent = roleDynamicComponent;
        this.businessRoleList = Collections.unmodifiableList(businessRoleList.stream().filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public Optional<Role> getRole(StoryRequest<?> storyRequest, ScopeDataQuery scopeDataQuery) {
        String startId = storyRequest.getStartId();
        String businessId = storyRequest.getBusinessId();
        if (StringUtils.isBlank(startId)) {
            return Optional.empty();
        }

        String key = getKey(businessId, startId);
        try {
            Optional<BusinessRole> businessRole = businessRoleMapping.get(key, () -> {
                Optional<BusinessRole> brOptional = getBusinessRole(businessId, startId);
                brOptional.ifPresent(br -> LOGGER.debug("business role match. startId: {}, businessId: {}, role name: {}", startId, businessId, br.getRole().getName()));
                return brOptional;
            });
            return Optional.ofNullable(businessRole.map(BusinessRole::getRole).orElseGet(() -> getDynamicRole(scopeDataQuery)));
        } catch (ExecutionException e) {
            LOGGER.error(e.getMessage(), e);
            throw ExceptionUtil.buildException(e, ExceptionEnum.STORY_ERROR, null);
        }
    }

    private Role getDynamicRole(ScopeDataQuery scopeDataQuery) {
        return roleDynamicComponent.dynamicGetComponent(null, null, scopeDataQuery).orElse(null);
    }

    private Optional<BusinessRole> getBusinessRole(String businessId, String startId) {
        Optional<BusinessRole> firstOptional = businessRoleList.stream().filter(br -> br.priorityMatch(businessId, startId)).findFirst();
        if (firstOptional.isPresent() || StringUtils.isBlank(businessId)) {
            return firstOptional;
        }
        // businessId 不为空时，重新按 startId 再匹配一次
        return businessRoleList.stream().filter(br -> br.secondMatch(startId)).findFirst();
    }

    private String getKey(String businessId, String startId) {
        return Optional.ofNullable(businessId).orElse(StringUtils.EMPTY) + "@@" + startId;
    }

    private void checkDuplicated(List<BusinessRole> businessRoleList) {
        Set<String> keySet = Sets.newHashSet();
        businessRoleList.forEach(br ->
                br.getStartIdList().forEach(sId -> {
                            if (CollectionUtils.isEmpty(br.getBusinessIdList())) {
                                String key = getKey(null, sId);
                                AssertUtil.notTrue(keySet.contains(key), ExceptionEnum.COMPONENT_DUPLICATION_ERROR,
                                        "BusinessRole is not allowed to be repeatedly defined! startId: {}", sId
                                );
                                keySet.add(key);
                            }
                            br.getBusinessIdList().forEach(bId -> {
                                String key = getKey(bId, sId);
                                AssertUtil.notTrue(keySet.contains(key), ExceptionEnum.COMPONENT_DUPLICATION_ERROR,
                                        "BusinessRole is not allowed to be repeatedly defined! businessId: {}, startId: {}", bId, sId
                                );
                                keySet.add(key);
                            });
                        }
                ));
    }
}
