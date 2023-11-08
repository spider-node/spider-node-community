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
package cn.spider.framework.flow.monitor;

import cn.spider.framework.annotation.enums.ScopeTypeEnum;
import cn.spider.framework.flow.bus.StoryBus;
import cn.spider.framework.flow.role.Role;
import cn.spider.framework.flow.util.AssertUtil;

import java.util.Optional;

/**
 * @author lykan
 */
public class RecallStory {

    private final Throwable exception;

    private final StoryBus storyBus;

    public RecallStory(StoryBus storyBus) {
        AssertUtil.notNull(storyBus);
        this.exception = null;
        this.storyBus = storyBus;
    }

    public RecallStory(Throwable exception, StoryBus storyBus) {
        AssertUtil.notNull(storyBus);
        this.exception = exception;
        this.storyBus = storyBus;
    }

    public Optional<Throwable> getException() {
        return Optional.ofNullable(exception);
    }

    /**
     * 获取 Request 域 数据
     *
     * @return data
     */
    public Object getReq() {
        return storyBus.getReq();
    }

    /**
     * 获取 执行结果
     *
     * @return ReturnResult
     */
    public Optional<Object> getResult() {
        return storyBus.getResult();
    }

    /**
     * 获取 Value
     *
     * @param scopeTypeEnum 域
     * @param key           key
     * @return value
     */
    public Optional<Object> getValue(ScopeTypeEnum scopeTypeEnum, String key) {
        return storyBus.getValue(scopeTypeEnum, key);
    }

    /**
     * 获取角色
     *
     * @return 角色
     */
    public Role getRole() {
        return storyBus.getRole();
    }

    /**
     * 获取链路追踪器
     *
     * @return 链路追踪器
     */
    public MonitorTracking getMonitorTracking() {
        return storyBus.getMonitorTracking();
    }

    /**
     * 获取业务 ID
     *
     * @return 业务 ID
     */
    public String getBusinessId() {
        return storyBus.getBusinessId();
    }

    /**
     * 获取StartID
     *
     * @return StartID
     */
    public String getStartId() {
        return storyBus.getStartId();
    }
}
