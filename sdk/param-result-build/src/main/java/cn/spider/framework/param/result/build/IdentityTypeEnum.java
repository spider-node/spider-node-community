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
package cn.spider.framework.param.result.build;

/**
 * @author dds
 */
public enum IdentityTypeEnum {

    /**
     * K-V 键值存储
     */
    K_V_ABILITY,

    /**
     * 服务组件
     */
    TASK_COMPONENT,

    /**
     * 服务节点
     */
    SERVICE_TASK,

    /**
     * 服务能力节点
     */
    SERVICE_TASK_ABILITY,

    /**
     * 子流程
     */
    SUB_PROCESS,

    /**
     * 通知字段
     */
    NOTICE_FIELD,

    /**
     * 服务节点资源
     */
    SERVICE_NODE_RESOURCE,

    /**
     * 权限
     */
    PERMISSION
}
