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
package cn.spider.framework.flow.engine;

import cn.spider.framework.annotation.enums.ScopeTypeEnum;
import cn.spider.framework.db.util.RocksdbUtil;
import cn.spider.framework.flow.engine.example.FlowExampleManager;
import cn.spider.framework.flow.engine.example.data.FlowExample;
import cn.spider.framework.flow.engine.example.enums.VerifyStatus;
import cn.spider.framework.flow.engine.facade.TaskResponse;
import cn.spider.framework.flow.engine.facade.TaskResponseBox;
import cn.spider.framework.flow.bpmn.StartEvent;
import cn.spider.framework.flow.bus.BasicStoryBus;
import cn.spider.framework.flow.bus.ScopeData;
import cn.spider.framework.flow.bus.ScopeDataQuery;
import cn.spider.framework.flow.constant.GlobalProperties;
import cn.spider.framework.flow.engine.facade.StoryRequest;
import cn.spider.framework.flow.engine.future.AdminFuture;
import cn.spider.framework.flow.engine.future.FlowTaskSubscriber;
import cn.spider.framework.flow.engine.future.MonoFlowFuture;
import cn.spider.framework.flow.engine.thread.FlowTask;
import cn.spider.framework.flow.engine.thread.MonoFlowTask;
import cn.spider.framework.flow.engine.thread.hook.ThreadSwitchHook;
import cn.spider.framework.flow.engine.thread.hook.ThreadSwitchHookProcessor;
import cn.spider.framework.flow.enums.AsyncTaskState;
import cn.spider.framework.flow.exception.BusinessException;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.monitor.MonitorTracking;
import cn.spider.framework.flow.monitor.RecallStory;
import cn.spider.framework.flow.role.BusinessRoleRepository;
import cn.spider.framework.flow.role.Role;
import cn.spider.framework.flow.role.ServiceTaskRole;
import cn.spider.framework.flow.util.*;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 执行引擎
 *
 * @author lykan
 */
@Slf4j
public class StoryEngine {

    /**
     * StoryEngine 组成模块
     */
    private final StoryEngineModule storyEngineModule;

    /**
     * 角色资源库，可以根据业务ID决策使用哪个角色
     */
    private final BusinessRoleRepository businessRoleRepository;

    private FlowExampleManager flowExampleManager;

    public StoryEngine(StoryEngineModule storyEngineModule, BusinessRoleRepository businessRoleRepository) {
        AssertUtil.anyNotNull(businessRoleRepository, storyEngineModule);
        this.businessRoleRepository = businessRoleRepository;
        this.storyEngineModule = storyEngineModule;
        this.flowExampleManager = new FlowExampleManager(storyEngineModule);
    }

    public Future<TaskResponse<Object>> fire(StoryRequest<Object> storyRequest) {
        Promise<TaskResponse<Object>> promise = Promise.promise();
        ScopeDataQuery scopeDataQuery = getScopeDataQuery(storyRequest);
        initRole(storyRequest, scopeDataQuery);
        return doFire(storyRequest, promise);
    }

    @SuppressWarnings("unchecked")
    private Future<TaskResponse<Object>> doFire(StoryRequest<Object> storyRequest, Promise<TaskResponse<Object>> promise) {
        try {
            FlowExample flowExample = flowExampleManager.registerExample(storyRequest);
            FlowRegister flowRegisterAsync = flowExample.getFlowRegister();
            BasicStoryBus storyBusAsync = flowExample.getStoryBus();
            Future<Void> future = flowExample.getFuture();
            // 最终执行完成的结果执行--
            future.onSuccess(suss -> {
                Object result = ResultUtil.buildObject(storyBusAsync);
                Optional.ofNullable(storyRequest.getRecallStoryHook()).ifPresent(c -> c.accept(new RecallStory(storyBusAsync)));
                TaskResponse<Object> response = TaskResponseBox.buildSuccess(result);
                flowRegisterAsync.getMonitorTracking().trackingLog();
                promise.complete(response);
            }).onFailure(fail -> {
                flowRegisterAsync.getMonitorTracking().trackingLog();
                TaskResponse<Object> errorResponse = TaskResponseBox.buildError(ExceptionEnum.BUSINESS_INVOKE_ERROR.getCode(), fail.getMessage());
                GlobalUtil.transferNotEmpty(errorResponse, TaskResponseBox.class).setResultException(fail);
                promise.complete(errorResponse);
            });
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
        return promise.future();
    }

    public Future<Void> activationExample(String requestId, VerifyStatus verifyStatus) {
        try {
            flowExampleManager.activation(requestId,verifyStatus);
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
        return Future.succeededFuture();
    }

    public FlowExampleManager getFlowExampleManager() {
        return this.flowExampleManager;
    }


    private <T> void initRole(StoryRequest<T> storyRequest, ScopeDataQuery scopeDataQuery) {
        if (StringUtils.isBlank(storyRequest.getStartId()) || storyRequest.getRole() != null) {
            return;
        }
        storyRequest.setRole(businessRoleRepository.getRole(storyRequest, scopeDataQuery).orElse(new ServiceTaskRole()));
    }


    @SuppressWarnings("unchecked")
    private ScopeDataQuery getScopeDataQuery(StoryRequest<?> storyRequest) {

        return new ScopeDataQuery() {

            @Override
            public <T> T getReqScope() {
                return (T) storyRequest.getRequest();
            }

            @Override
            public <T extends ScopeData> T getStaScope() {
                return (T) storyRequest.getStaScopeData();
            }

            @Override
            public <T extends ScopeData> T getVarScope() {
                return (T) storyRequest.getVarScopeData();
            }

            @Override
            public <T> Optional<T> getResult() {
                throw new BusinessException(ExceptionEnum.BUSINESS_INVOKE_ERROR.getExceptionCode(), "Method is not allowed to be called!");
            }

            @Override
            public String getRequestId() {
                return storyRequest.getRequestId();
            }

            @Override
            public String getStartId() {
                return storyRequest.getStartId();
            }

            @Override
            public Optional<String> getBusinessId() {
                return Optional.ofNullable(storyRequest.getBusinessId()).filter(StringUtils::isNotBlank);
            }

            @Override
            public <T> Optional<T> getReqData(String name) {
                T reqScope = getReqScope();
                if (reqScope == null) {
                    return Optional.empty();
                }
                return PropertyUtil.getProperty(reqScope, name).map(obj -> (T) obj);
            }

            @Override
            public <T> Optional<T> getStaData(String name) {
                T staScope = getStaScope();
                if (staScope == null) {
                    return Optional.empty();
                }
                return PropertyUtil.getProperty(staScope, name).map(obj -> (T) obj);
            }

            @Override
            public <T> Optional<T> getVarData(String name) {
                T varScope = getVarScope();
                if (varScope == null) {
                    return Optional.empty();
                }
                return PropertyUtil.getProperty(varScope, name).map(obj -> (T) obj);
            }

            @Override
            public <T> Optional<T> getData(String expression) {
                if (!ElementParserUtil.isValidDataExpression(expression)) {
                    return Optional.empty();
                }

                String[] expArr = expression.split("\\.", 2);
                Optional<ScopeTypeEnum> ScopeTypeOptional = ScopeTypeEnum.of(expArr[0]);
                if (ScopeTypeOptional.orElse(null) == ScopeTypeEnum.RESULT) {
                    return getResult();
                }

                String key = (expArr.length == 2) ? expArr[1] : null;
                if (StringUtils.isBlank(key)) {
                    return Optional.empty();
                }
                return ScopeTypeOptional.flatMap(scope -> {
                    if (scope == ScopeTypeEnum.REQUEST) {
                        return getReqData(key);
                    } else if (scope == ScopeTypeEnum.STABLE) {
                        return getStaData(key);
                    } else if (scope == ScopeTypeEnum.VARIABLE) {
                        return getVarData(key);
                    }
                    return Optional.empty();
                });
            }

            @Override
            public Optional<String> getTaskProperty() {
                throw new BusinessException(ExceptionEnum.BUSINESS_INVOKE_ERROR.getExceptionCode(), "Method is not allowed to be called!");
            }

            @Override
            public <T> Optional<T> iterDataItem() {
                throw new BusinessException(ExceptionEnum.BUSINESS_INVOKE_ERROR.getExceptionCode(), "Method is not allowed to be called!");
            }

            @Override
            public ReentrantReadWriteLock.ReadLock readLock() {
                throw new BusinessException(ExceptionEnum.BUSINESS_INVOKE_ERROR.getExceptionCode(), "Method is not allowed to be called!");
            }
        };
    }
}
