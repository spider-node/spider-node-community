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

import cn.spider.framework.flow.bpmn.EndEvent;
import cn.spider.framework.flow.bpmn.FlowElement;
import cn.spider.framework.flow.bpmn.StartEvent;
import cn.spider.framework.flow.bpmn.impl.EndEventImpl;
import cn.spider.framework.flow.bpmn.impl.InclusiveGatewayImpl;
import cn.spider.framework.flow.bpmn.impl.ParallelGatewayImpl;
import cn.spider.framework.flow.bpmn.impl.StartEventImpl;
import cn.spider.framework.flow.component.bpmn.builder.InclusiveJoinPointBuilder;
import cn.spider.framework.flow.component.bpmn.builder.ParallelJoinPointBuilder;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.util.AssertUtil;
import cn.spider.framework.flow.util.GlobalUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * BPMN 元素代码方式连接起点
 *
 * @author lykan
 */
public class StartDiagramProcessLink extends BpmnDiagramLink implements StartProcessLink {

    /**
     * 开始事件
     */
    private final StartEvent startEvent;

    /**
     * 结束事件
     */
    private final EndEvent endEvent;

    public StartDiagramProcessLink(String id, String name) {
        AssertUtil.notBlank(id, ExceptionEnum.PARAMS_ERROR, "Id is not allowed to be empty!");
        StartEventImpl se = new StartEventImpl();
        se.setId(id);
        se.setName(name);
        this.startEvent = se;

        EndEventImpl endEvent = new EndEventImpl();
        endEvent.setId(GlobalUtil.uuid());
        this.endEvent = endEvent;
    }

    @Override
    public ProcessLink getProcessLink() {
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends FlowElement> T getElement() {
        return (T) startEvent;
    }

    @Override
    public InclusiveJoinPointBuilder inclusive() {
        return inclusive(null);
    }

    @Override
    public ParallelJoinPointBuilder parallel() {
        return parallel(null);
    }

    @Override
    public InclusiveJoinPointBuilder inclusive(String id) {
        if (StringUtils.isBlank(id)) {
            id = GlobalUtil.uuid();
        }
        InclusiveGatewayImpl gateway = new InclusiveGatewayImpl();
        gateway.setId(id);
        return new InclusiveJoinPointBuilder(gateway, this);
    }

    @Override
    public ParallelJoinPointBuilder parallel(String id) {
        if (StringUtils.isBlank(id)) {
            id = GlobalUtil.uuid();
        }
        ParallelGatewayImpl gateway = new ParallelGatewayImpl();
        gateway.setId(id);
        return new ParallelJoinPointBuilder(gateway, this);
    }

    public EndEvent getEndEvent() {
        return endEvent;
    }
}
