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
package cn.spider.framework.flow.engine.facade;

import cn.spider.framework.annotation.enums.ScopeTypeEnum;
import cn.spider.framework.container.sdk.data.StartFlowRequest;
import cn.spider.framework.flow.bus.ScopeData;
import cn.spider.framework.flow.component.bpmn.lambda.LambdaParam;
import cn.spider.framework.flow.engine.example.enums.FlowExampleRole;
import cn.spider.framework.flow.enums.TrackingTypeEnum;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.monitor.RecallStory;
import cn.spider.framework.flow.role.Role;
import cn.spider.framework.flow.util.AssertUtil;
import cn.spider.framework.flow.util.LambdaUtil;
import com.alibaba.fastjson.JSON;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * @author lykan
 */
public class ReqBuilder<T> {

    private final StoryRequest<T> storyRequest = new StoryRequest<>();

    private ReqBuilder() {

    }

    public static <T> ReqBuilder<T> returnType(Class<T> returnType) {
        ReqBuilder<T> builder = new ReqBuilder<>();
        builder.result(returnType);
        return builder;
    }

    public static <T> ReqBuilder<T> returnType(T returnInstance) {
        ReqBuilder<T> builder = new ReqBuilder<>();
        builder.result(returnInstance.getClass());
        return builder;
    }

    public StoryRequest<T> build() {
        AssertUtil.notBlank(this.storyRequest.getStartId());
        return this.storyRequest;
    }

    public ReqBuilder<T> startId(String startId) {
        AssertUtil.notBlank(startId);
        this.storyRequest.setStartId(startId);
        return this;
    }

    public ReqBuilder<T> functionId(String functionId) {
        AssertUtil.notBlank(functionId);
        this.storyRequest.setFunctionId(functionId);
        return this;
    }

    public ReqBuilder<T> functionName(String functionName) {
        AssertUtil.notBlank(functionName);
        this.storyRequest.setFunctionName(functionName);
        return this;
    }

    public ReqBuilder<T> flowExampleRole(FlowExampleRole flowExampleRole) {
        this.storyRequest.setFlowExampleRole(flowExampleRole);
        return this;
    }

    public ReqBuilder<T> startFlowRequest(StartFlowRequest request) {
        this.storyRequest.setRequestParam(request);
        return this;
    }

    public <Link> ReqBuilder<T> startProcess(LambdaParam.LambdaProcess<Link> process) {
        String startId = LambdaUtil.getProcessName(process);
        AssertUtil.notBlank(startId);
        this.storyRequest.setStartId(startId);
        return this;
    }

    public ReqBuilder<T> request(Object request) {
        if (request != null) {
            this.storyRequest.setRequest(request);
        }
        return this;
    }

    public ReqBuilder<T> businessId(String businessId) {
        this.storyRequest.setBusinessId(businessId);
        return this;
    }

    public ReqBuilder<T> trackingType(TrackingTypeEnum trackingTypeEnum) {
        this.storyRequest.setTrackingType(trackingTypeEnum);
        return this;
    }

    public ReqBuilder<T> varScopeData(ScopeData varScopeData) {
        if (varScopeData != null) {
            AssertUtil.isTrue(varScopeData.getScopeDataEnum() == ScopeTypeEnum.VARIABLE, ExceptionEnum.PARAMS_ERROR);
            this.storyRequest.setVarScopeData(varScopeData);
        }
        return this;
    }

    public ReqBuilder<T> staScopeData(ScopeData staScopeData) {
        if (staScopeData != null) {
            AssertUtil.isTrue(staScopeData.getScopeDataEnum() == ScopeTypeEnum.STABLE, ExceptionEnum.PARAMS_ERROR);
            this.storyRequest.setStaScopeData(staScopeData);
        }
        return this;
    }

    public ReqBuilder<T> role(Role role) {
        if (role != null) {
            this.storyRequest.setRole(role);
        }
        return this;
    }

    public ReqBuilder<T> timeout(Integer timeout) {
        this.storyRequest.setTimeout(timeout);
        return this;
    }

    public ReqBuilder<T> recallStoryHook(Consumer<RecallStory> recallStoryHook) {
        this.storyRequest.setRecallStoryHook(recallStoryHook);
        return this;
    }

    public ReqBuilder<T> requestId(String requestId) {
        this.storyRequest.setRequestId(requestId);
        return this;
    }

    public ReqBuilder<T> storyExecutor(ThreadPoolExecutor storyExecutor) {
        AssertUtil.notNull(storyExecutor);
        this.storyRequest.setStoryExecutor(storyExecutor);
        return this;
    }

    private void result(Class<?> returnType) {
        this.storyRequest.setReturnType(returnType);
    }
}
