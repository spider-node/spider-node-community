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
package cn.spider.framework.flow.component.expression;

import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.role.permission.Permission;
import cn.spider.framework.flow.util.AssertUtil;
import cn.spider.framework.flow.util.ExceptionUtil;
import cn.spider.framework.flow.util.GlobalUtil;
import cn.spider.framework.flow.util.PermissionUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author lykan
 */
public class RoleConditionExpression extends ConditionExpressionImpl implements ConditionExpression {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleConditionExpression.class);

    private static final Cache<String, RoleCondition> rolePermissionCache = CacheBuilder.newBuilder()
            .concurrencyLevel(8).initialCapacity(1024).maximumSize(50_000).expireAfterWrite(10, TimeUnit.MINUTES)
            .removalListener(notification -> LOGGER.info("Role permission cache lose efficacy. key: {}, value: {}, cause: {}",
                    notification.getKey(), notification.getValue(), notification.getCause())).build();

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    @SuppressWarnings("all")
    public RoleConditionExpression() {
        super((scopeData, exp) -> {
            RoleCondition roleCondition = null;
            try {
                roleCondition = rolePermissionCache.get(exp, () -> getRoleCondition(exp));
            } catch (ExecutionException e) {
                LOGGER.error(e.getMessage(), e);
                throw ExceptionUtil.buildException(e, ExceptionEnum.STORY_ERROR, null);
            }
            AssertUtil.isTrue(roleCondition != null && roleCondition.matched && CollectionUtils.isNotEmpty(roleCondition.permissionList),
                    ExceptionEnum.STORY_ERROR);

            Boolean[] matchResult = roleCondition.permissionList.stream().map(p -> {
                return Optional.of(scopeData.getRole()).map(role -> role.allowedUseResource(p)).orElse(false);
            }).toArray(Boolean[]::new);
            return PARSER.parseExpression(MessageFormat.format(roleCondition.expression, matchResult)).getValue(Boolean.class);
        });
    }

    @Override
    public boolean match(String expression) {
        try {
            return rolePermissionCache.get(expression, () -> getRoleCondition(expression)).matched;
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
            throw ExceptionUtil.buildException(e, ExceptionEnum.STORY_ERROR, null);
        }
    }

    @Override
    public boolean isNeedParserExpression() {
        return false;
    }

    public static RoleCondition getRoleCondition(String expression) {
        RoleCondition roleCondition = new RoleCondition();
        String exp = expression;
        List<Permission> pList = Lists.newArrayList();
        List<String> psList = Stream.of(expression.split("[&|!()]")).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        for (int i = 0; i < psList.size(); i++) {
            String ps = psList.get(i);
            Optional<Permission> permissionOptional = PermissionUtil.parsePermission(ps);
            if (!permissionOptional.isPresent()) {
                return roleCondition;
            }
            pList.add(permissionOptional.get());
            exp = StringUtils.replaceOnce(exp, ps, GlobalUtil.format("{{}}", i));
        }

        try {
            Object[] params = IntStream.range(0, psList.size()).mapToObj(i -> true).toArray(Boolean[]::new);
            AssertUtil.notNull(PARSER.parseExpression(MessageFormat.format(exp, params)).getValue(Boolean.class));
        } catch (Throwable e) {
            LOGGER.info(e.getMessage(), e);
            return roleCondition;
        }
        roleCondition.expression = exp;
        roleCondition.matched = true;
        roleCondition.permissionList = pList;
        LOGGER.debug("Role permission cache. expression: {}, condition: {}", expression, JSON.toJSONString(roleCondition));
        return roleCondition;
    }

    private static class RoleCondition {

        private List<Permission> permissionList;

        private String expression;

        private boolean matched = false;

        public List<Permission> getPermissionList() {
            return permissionList;
        }

        public String getExpression() {
            return expression;
        }
    }
}
