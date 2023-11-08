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
package cn.spider.framework.flow.component.bpmn.link;

import cn.spider.framework.flow.component.bpmn.builder.InclusiveJoinPointBuilder;
import cn.spider.framework.flow.component.bpmn.builder.ParallelJoinPointBuilder;
import cn.spider.framework.flow.component.bpmn.lambda.LambdaParam;
import cn.spider.framework.flow.util.LambdaUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 在 ProcessLink 的基础上增加了开始节点特有的功能
 */
public interface StartProcessLink extends ProcessLink {

    /**
     * 创建包含网关汇聚节点
     *
     * @return 包含网关汇聚节点构建类
     */
    InclusiveJoinPointBuilder inclusive();

    /**
     * 创建并行网关汇聚节点
     *
     * @return 包含网关汇聚节点构建类
     */
    ParallelJoinPointBuilder parallel();

    /**
     * 创建包含网关汇聚节点
     *
     * @param id 指定网关ID
     * @return 包含网关汇聚节点构建类
     */
    InclusiveJoinPointBuilder inclusive(String id);

    /**
     * 创建并行网关汇聚节点
     *
     * @param id 指定网关ID
     * @return 包含网关汇聚节点构建类
     */
    ParallelJoinPointBuilder parallel(String id);

    /**
     * 构建
     *
     * @param id 非空
     * @return ProcessLink
     */
    static StartProcessLink build(String id) {
        return build(id, StringUtils.EMPTY);
    }

    /**
     * 构建
     *
     * @param process 非空
     * @return ProcessLink
     */
    static <Link> StartProcessLink build(LambdaParam.LambdaProcess<Link> process) {
        return build(process, StringUtils.EMPTY);
    }

    /**
     * 构建
     *
     * @param id   非空
     * @param name 允许为空
     * @return ProcessLink
     */
    static StartProcessLink build(String id, String name) {
        return new StartDiagramProcessLink(id, name);
    }

    /**
     * 构建
     *
     * @param process 非空
     * @param name 允许为空
     * @return ProcessLink
     */
    static <Link> StartProcessLink build(LambdaParam.LambdaProcess<Link> process, String name) {
        return build(LambdaUtil.getProcessName(process), name);
    }
}
