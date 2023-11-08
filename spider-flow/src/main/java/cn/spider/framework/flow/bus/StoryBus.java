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
package cn.spider.framework.flow.bus;

import cn.spider.framework.annotation.enums.ScopeTypeEnum;
import cn.spider.framework.flow.bpmn.FlowElement;
import cn.spider.framework.flow.container.component.TaskServiceDef;
import cn.spider.framework.flow.monitor.MonitorTracking;
import cn.spider.framework.flow.role.Role;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * StoryBus
 *
 * @author lykan
 */
public interface StoryBus {

    /**
     * 获取 Request 域 数据
     * @return data
     */
    Object getReq();

    /**
     * 获取 执行结果
     *
     * @return ReturnResult
     */
    Optional<Object> getResult();

    /**
     * 获取 Var 域变量
     *
     * @return var scope data
     */
    ScopeData getVar();

    /**
     * 获取 Sta 域变量
     *
     * @return sta scope data
     */
    ScopeData getSta();

    /**
     * 获取 Value
     *
     * @param scopeTypeEnum 域
     * @param key key
     * @return value
     */
    Optional<Object> getValue(ScopeTypeEnum scopeTypeEnum, String key);

    /**
     * 获取角色
     *
     * @return 角色
     */
    Role getRole();

    /**
     * 将节点执行结果回填至 ScopeData 中
     *
     * @param flowElement flowElement
     * @param result 执行结果
     * @param taskServiceDef 回填描述
     */
    void noticeResult(FlowElement flowElement, Object result, TaskServiceDef taskServiceDef);

    /**
     * 获取链路追踪器
     * @return 链路追踪器
     */
    MonitorTracking getMonitorTracking();

    /**
     * 获取业务 ID
     *
     * @return 业务 ID
     */
    String getBusinessId();

    /**
     * 获取开始事件ID
     *
     * @return 开始事件ID
     */
    String getStartId();

    /**
     * 获取 ScopeDataOperator
     *
     * @return ScopeDataOperator
     */
    ScopeDataOperator getScopeDataOperator();

    /**
     * 获取当前任务使用的自定义任务执行器
     */
    ThreadPoolExecutor getStoryExecutor();

    /**
     * 距离最后期限的剩余时间
     */
    int remainTimeMillis();
}
