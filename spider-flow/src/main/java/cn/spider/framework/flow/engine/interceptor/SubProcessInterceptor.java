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
package cn.spider.framework.flow.engine.interceptor;

import cn.spider.framework.flow.bus.ScopeDataOperator;
import cn.spider.framework.flow.role.Role;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.Ordered;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 子流程拦截器
 *
 * @author lykan
 */
public interface SubProcessInterceptor extends Ordered {

    /**
     * 子流程拦截器切入点
     *
     * @return 返回子流程开始事件Id集合
     */
    default Set<String> pointcut() {
        return Sets.newHashSet();
    }

    /**
     * 获取子流程身份标志集合，只有当前子流程在集合内才会执行拦截器
     *
     * @return 子流程身份标志集合
     */
    default Set<SubProcessIdentity> getSubProcessIdentity() {
        Set<String> pointcut = pointcut();
        if (CollectionUtils.isEmpty(pointcut)) {
            return Sets.newHashSet();
        }
        return pointcut.stream().map(SubProcessIdentity::new).collect(Collectors.toSet());
    }

    /**
     * 子流程进入前的前置处理器
     *
     * @param dataOperator 数据操作入口
     * @param role 角色
     * @return true：正常进入，false：跳过子流程继续执行下一个节点
     */
    default boolean beforeProcessor(ScopeDataOperator dataOperator, Role role) {
        // DO NOTHING
        return true;
    }

    /**
     * 子流程进入执行完后的后置处理器
     *
     * @param dataOperator 数据操作入口
     * @param role 角色
     */
    default void afterProcessor(ScopeDataOperator dataOperator, Role role) {
        // DO NOTHING
    }

    /**
     * 子流程执行失败后的错误处理器
     *
     * @param exception 异常信息
     * @param dataOperator 数据操作入口
     * @param role 角色
     */
    default void errorProcessor(Throwable exception, ScopeDataOperator dataOperator, Role role) {
        // DO NOTHING
    }

    /**
     * 无论子流程是否执行成功，都会执行 finally 操作
     *
     * @param dataOperator 数据操作入口
     * @param role 角色
     */
    default void finallyProcessor(ScopeDataOperator dataOperator, Role role) {
        // DO NOTHING
    }

    /**
     * 控制拦截器的排序顺序
     *
     * @return 排序id
     */
    default int getOrder() {
        return 1000;
    }
}
