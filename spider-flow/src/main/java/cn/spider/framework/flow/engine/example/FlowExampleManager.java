package cn.spider.framework.flow.engine.example;

import cn.spider.framework.annotation.enums.ScopeTypeEnum;
import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.*;
import cn.spider.framework.common.event.enums.ElementStatus;
import cn.spider.framework.common.event.enums.FlowExampleStatus;
import cn.spider.framework.common.utils.*;
import cn.spider.framework.flow.SpiderCoreVerticle;
import cn.spider.framework.flow.bpmn.FlowElement;
import cn.spider.framework.flow.bpmn.ServiceTask;
import cn.spider.framework.flow.bpmn.StartEvent;
import cn.spider.framework.flow.bpmn.enums.BpmnTypeEnum;
import cn.spider.framework.flow.bpmn.enums.ServerTaskTypeEnum;
import cn.spider.framework.flow.bus.*;
import cn.spider.framework.flow.container.component.TaskServiceDef;
import cn.spider.framework.flow.engine.FlowRegister;
import cn.spider.framework.flow.engine.StoryEngineModule;
import cn.spider.framework.flow.engine.example.data.FlowExample;
import cn.spider.framework.flow.engine.example.enums.FlowExampleRole;
import cn.spider.framework.flow.engine.example.enums.FlowExampleRunStatus;
import cn.spider.framework.flow.engine.example.enums.VerifyStatus;
import cn.spider.framework.flow.engine.facade.StoryRequest;
import cn.spider.framework.flow.engine.scheduler.SchedulerManager;
import cn.spider.framework.flow.exception.BusinessException;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.load.loader.ClassLoaderManager;
import cn.spider.framework.flow.monitor.MonitorTracking;
import cn.spider.framework.flow.role.Role;
import cn.spider.framework.flow.timer.SpiderTimer;
import cn.spider.framework.flow.util.*;
import cn.spider.framework.transaction.sdk.data.RegisterTransactionRequest;
import cn.spider.framework.transaction.sdk.data.RegisterTransactionResponse;
import cn.spider.framework.transaction.sdk.interfaces.TransactionInterface;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.engine
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-29  18:02
 * @Description: 管控-流程实例的生命周期
 * @Version: 1.0
 */
@Slf4j
public class FlowExampleManager {
    /**
     * StoryEngine 组成模块
     */
    private StoryEngineModule storyEngineModule;


    private TransactionInterface transactionInterface;

    private SchedulerManager schedulerManager;

    private final String VERIFY_RESULT = "VERIFY_RESULT";

    private EventManager eventManager;


    /**
     * leader的实例map
     */
    private Map<String, FlowExample> leaderFlowExampleMap;

    private ClassLoaderManager classLoaderManager;

    private SpiderTimer spiderTimer;

    public FlowExampleManager(StoryEngineModule storyEngineModule) {
        this.storyEngineModule = storyEngineModule;
        this.leaderFlowExampleMap = Maps.newHashMap();
    }

    public void init() {
        if (Objects.nonNull(this.schedulerManager)) {
            return;
        }
        this.schedulerManager = SpiderCoreVerticle.factory.getBean(SchedulerManager.class);
        this.transactionInterface = SpiderCoreVerticle.factory.getBean(TransactionInterface.class);
        this.eventManager = SpiderCoreVerticle.factory.getBean(EventManager.class);
        this.classLoaderManager = SpiderCoreVerticle.factory.getBean(ClassLoaderManager.class);
        this.spiderTimer = SpiderCoreVerticle.factory.getBean(SpiderTimer.class);

    }

    public void transcriptReplace(LeaderReplaceData replaceData) {

    }

    /**
     * 把副本信息转正
     *
     * @param brokerName
     */
    public void become(String brokerName) {
    }

    public void registerFollowerExample(StoryRequest<Object> storyRequest, String brokerName) {

    }

    public FlowExample queryFlowExample(String exampleId) {
        return this.leaderFlowExampleMap.get(exampleId);
    }

    /**
     * 同步副本实例开始执行
     *
     * @param elementExampleData
     * @param brokerName
     */
    public void syncTranscriptStartElementExample(StartElementExampleData elementExampleData, String brokerName) {

    }

    public void syncTranscriptEndElementExample(EndElementExampleData data, String brokerName) {

    }

    /**
     * 同步
     *
     * @param brokerName
     * @param requestId
     */
    public void syncTranscriptFlowExampleEnd(String brokerName, String requestId) {

    }


    /**
     * 注册 流程实例-》返回对应的id
     *
     * @return FlowExample
     */
    public FlowExample registerExample(StoryRequest<Object> storyRequest) {
        init();
        Role role = storyRequest.getRole();
        ScopeDataQuery scopeDataQuery = getScopeDataQuery(storyRequest);
        FlowRegister flowRegister = getFlowRegister(storyRequest, scopeDataQuery);
        BasicStoryBus storyBus = getStoryBus(storyRequest, flowRegister, role);
        String exampleId = storyRequest.getRequestId();
        storyRequest.setRequestId(exampleId);
        FlowExample example = FlowExample.builder()
                .exampleId(exampleId)
                .requestId(exampleId)
                .flowRegister(flowRegister)
                .functionId(storyRequest.getFunctionId())
                .functionName(storyRequest.getFunctionName())
                .role(role)
                .flowExampleRole(storyRequest.getFlowExampleRole())
                .storyEngineModule(this.storyEngineModule)
                .storyBus(storyBus)
                .build();
        example.init();
        this.leaderFlowExampleMap.put(exampleId, example);
        // 注册到队列中 50s没有执行完就移除
        spiderTimer.registerExampleMonitor(exampleId);
        // 构造 事件体
        StartFlowExampleEventData eventData = StartFlowExampleEventData.builder()
                .functionName(storyRequest.getFunctionName())
                .functionId(storyRequest.getFunctionId())
                .requestId(exampleId)
                .requestClassType(storyRequest.getRequestParam().getRequestClassType())
                .requestParam(storyRequest.getRequestParam())
                .startId(flowRegister.getStartEventId())
                .build();
        //发送事件
        eventManager.sendMessage(EventType.START_FLOW_EXAMPLE, eventData);
        runFlowExample(example, true, true);
        return example;
    }

    public Integer queryExampleNum() {
        return leaderFlowExampleMap.size();
    }

    /**
     * @param example 需要执行的流程实例
     * @param isNext  表示 是否需要获取下一个节点进行执行（用于重试）
     */
    public void runFlowExample(FlowExample example, Boolean isNext, Boolean isQuit) {
        if (isNext) {
            // 跳过进行-下一个执行节点
            try {
                example.nextElement();
            } catch (Exception e) {
                // 寻址找不到，直接报错
                example.getPromise().fail(e);
                EndFlowExampleEventData endFlowExampleEventData = EndFlowExampleEventData.builder()
                        .status(FlowExampleStatus.SUSS)
                        .requestId(example.getExampleId())
                        .functionId(example.getFunctionId())
                        .result(new JsonObject())
                        .build();
                endFlowExampleEventData.setException(ExceptionMessage.getStackTrace(e));
                endFlowExampleEventData.setStatus(FlowExampleStatus.FAIL);
                eventManager.sendMessage(EventType.END_FLOW_EXAMPLE, endFlowExampleEventData);
                log.info("获取节点失败 {}", example.getExampleId());
                return;
            }
        }

        //log.info("当前执行的requestId {}", example.getRequestId());

        if (Objects.isNull(example.getFlowElement())) {
           // log.info("当前执行的 requestId 执行节点为空", example.getRequestId());
            return;
        }

        FlowElement flowElement = example.getFlowElement();
        // 说明流程结束
        if (flowElement.getElementType() == BpmnTypeEnum.END_EVENT) {
           // log.info("当前执行的结束的requestId {} 节点信息 {}", example.getRequestId(), flowElement.getId());
            Object result = ResultUtil.buildObjectMessage(example.getStoryBus());
            EndFlowExampleEventData endFlowExampleEventData = EndFlowExampleEventData.builder()
                    .status(FlowExampleStatus.SUSS)
                    .requestId(example.getExampleId())
                    .functionId(flowElement.getId())
                    .result(Objects.isNull(result) ? new JsonObject() : JsonObject.mapFrom(result))
                    .build();
            // step1: 判断时间存在事务-存在就提交事务
            example.endRequest();
            this.leaderFlowExampleMap.remove(example.getExampleId());
            // 发送该流程实例结束的数据
            eventManager.sendMessage(EventType.END_FLOW_EXAMPLE, endFlowExampleEventData);
            return;
        }
        // 当是 service_task就继续执行
        if (flowElement.getElementType() == BpmnTypeEnum.SERVICE_TASK) {
            //执行挂起
           // log.info("当前执行的requestId {} taskId {}", example.getRequestId(), example.getFlowElement().getId());
            // 通知，执行结束
            ServiceTask serviceTask = (ServiceTask) flowElement;
            //log.info("获取参数-------------开始 {} 时间 {}", serviceTask.getTaskService(), System.currentTimeMillis());
            String transactionGroupId = serviceTask.queryTransactionGroup();
            serviceTask.setRequestId(example.getRequestId());
            ServerTaskTypeEnum taskType = serviceTask.queryServiceTaskType();

            StartElementExampleData elementExampleData = StartElementExampleData.builder()
                    .flowElementId(flowElement.getId())
                    .flowElementName(flowElement.getName())
                    .functionName(example.getFunctionName())
                    .requestId(example.getRequestId())
                    .functionId(example.getFunctionId())
                    .build();

            switch (taskType) {
                // 正常系欸但
                case NORMAL:
                    normal(transactionGroupId, serviceTask, example, elementExampleData);
                    break;
                //轮询节点
                case POLL:
                    example.endRequest();
                    poll(serviceTask, example, isQuit, transactionGroupId, elementExampleData);
                    break;
                // 审批节点
                case APPROVE:
                    // 注册监听，配置时间(没有被唤醒，直接结束)
                    // 直接睡眠
                    example.endRequest();
                    approve(example, serviceTask, elementExampleData);
                    break;
                case DELAY:
                    example.endRequest();
                    delayRun(serviceTask.queryDelayTime(), isQuit, transactionGroupId, serviceTask, example, elementExampleData);
                    break;
            }

        } else {
            runPlan(example);
        }
    }

    private void delayRun(Integer time,
                          Boolean isQuit,
                          String transactionGroupId,
                          ServiceTask serviceTask,
                          FlowExample example,
                          StartElementExampleData elementExampleData) {
        if (!isQuit) {
            // 设置允许移除
            example.setAllowRemove(true);
            normal(transactionGroupId, serviceTask, example, elementExampleData);
            return;
        }
        example.setAllowRemove(false);
        spiderTimer.registerExampleMonitor(example.getExampleId());
        spiderTimer.registerFinalDelay(example, time);
    }

    /**
     * 进入审批节点的处理
     *
     * @param example
     * @param serviceTask
     * @param elementExampleData
     */
    private void approve(FlowExample example, ServiceTask serviceTask, StartElementExampleData elementExampleData) {
        example.setFlowExampleRunStatus(FlowExampleRunStatus.SLEEP);
        example.setVerifyCount(0);
        example.setVerifySumCount(serviceTask.queryVerifyMonitorCount());
        InScopeData inScopeData = (InScopeData) example.getStoryBus().getSta();
        String key = VERIFY_RESULT;
        inScopeData.remove(key);
        spiderTimer.registerApprove(example.getExampleId(), serviceTask.queryVerifyMonitorInterval());
        eventManager.sendMessage(EventType.ELEMENT_START, elementExampleData);
    }

    /**
     * 校验睡眠数据
     *
     * @return
     */
    public Boolean checkFlowExampleVerify(String flowExampleId) {
        FlowExample example = this.leaderFlowExampleMap.get(flowExampleId);
        if (example.getFlowExampleRunStatus().equals(FlowExampleRunStatus.RUN)) {
            return true;
        }
        if (example.getVerifySumCount() > example.getVerifyCount()) {
            EndElementExampleData elementExampleData = EndElementExampleData.builder()
                    .requestId(example.getRequestId())
                    .flowElementId(example.getFlowElement().getId())
                    .status(ElementStatus.FAIL)
                    .exception("check结束没有等待到对应的数据")
                    .build();
            eventManager.sendMessage(EventType.ELEMENT_END, elementExampleData);
            endFlowExampleFail(example, new BusinessException(ExceptionEnum.BUSINESS_INVOKE_ERROR.getExceptionCode(), "审批节点没有等待到审批-审批超时"));
            // 构造结束方法
            return true;
        }
        // 校验+1
        example.autoincrementVerify();
        return false;
    }

    /**
     * 正常
     *
     * @param transactionGroupId
     * @param serviceTask
     * @param example
     * @param elementExampleData
     */
    private void normal(String transactionGroupId, ServiceTask serviceTask, FlowExample example, StartElementExampleData elementExampleData) {
        // 当组的事务id,不为空的情况下，需要先注册事务信息
        if (!StringUtils.isEmpty(transactionGroupId)) {
            Future<JsonObject> transaction = registerTransaction(serviceTask, example);
            transaction.onSuccess(suss -> {
                JsonObject transactionJson = suss;
                RegisterTransactionResponse response = transactionJson.mapTo(RegisterTransactionResponse.class);
                example.getTransactionGroupMap().put(transactionGroupId, response.getGroupId());
                serviceTask.setXid(response.getGroupId());
                serviceTask.setBranchId(response.getBranchId());
                runPlan(example);
                // 设置 groupId
                elementExampleData.setTransactionGroupId(response.getGroupId());
                // 设置该实例的 事务id
                elementExampleData.setBranchId(response.getBranchId());
                eventManager.sendMessage(EventType.ELEMENT_START, elementExampleData);
            }).onFailure(fail -> {
                // 获取事务事务信息失败-（直接）
                log.error("获取事务信息失败 {}", ExceptionMessage.getStackTrace(fail));
                endFlowExampleFail(example, fail);
            });
            return;
        }
        eventManager.sendMessage(EventType.ELEMENT_START, elementExampleData);
        runPlan(example);
    }

    /**
     * 轮询 -节点
     *
     * @param transactionGroupId
     * @param serviceTask
     * @param example
     * @param elementExampleData
     */
    public void poll(ServiceTask serviceTask, FlowExample example, Boolean isQuit, String transactionGroupId, StartElementExampleData elementExampleData) {
        if (!isQuit) {
            // 具体执行
            example.setAllowRemove(true);
            normal(transactionGroupId, serviceTask, example, elementExampleData);
            return;
        }
        example.autoincrementPollCount();
        example.setAllowRemove(false);
        spiderTimer.registerExampleMonitor(example.getExampleId());
        registerPoll(example, serviceTask.queryPollInterval());
    }

    /***
     * 注册延迟
     * @param example
     * @param time
     */
    private void registerPoll(FlowExample example, Integer time) {
        spiderTimer.registerDelay(example, time);
    }

    /**
     * 审批
     *
     * @param requestId
     * @param status
     */
    public void activation(String requestId, VerifyStatus status) {
        if (!this.leaderFlowExampleMap.containsKey(requestId)) {
            return;
        }
        FlowExample flowExample = this.leaderFlowExampleMap.get(requestId);
        flowExample.setFlowExampleRunStatus(FlowExampleRunStatus.RUN);
        ScopeDataOperator scopeDataOperator = flowExample.getStoryBus().getScopeDataOperator();
        String key = VERIFY_RESULT;
        scopeDataOperator.setVarData(key, status.name());
        runFlowExample(flowExample, true, true);
    }

    /**
     * 因为节点执行异常的处理方法
     *
     * @param flowExample
     * @param fail
     */
    public void endFlowExampleFail(FlowExample flowExample, Throwable fail) {
        // 通知整个节点结束
        flowExample.endFailRequest(fail);
        // 移除实例
        this.leaderFlowExampleMap.remove(flowExample.getExampleId());
        // 发送对应的异常事件
        EndFlowExampleEventData endFlowExampleEventData = EndFlowExampleEventData.builder()
                .status(FlowExampleStatus.FAIL)
                .requestId(flowExample.getExampleId())
                .functionId(flowExample.getFunctionId())
                .exception(ExceptionMessage.getStackTrace(fail))
                .build();
        eventManager.sendMessage(EventType.END_FLOW_EXAMPLE, endFlowExampleEventData);
    }

    /**
     * 移除执行实例
     * @param flowExampleIds
     */
    public void endFlowExample(List<String> flowExampleIds) {
        flowExampleIds.forEach(item -> {
            if (!this.leaderFlowExampleMap.containsKey(item)) {
                return;
            }
            FlowExample flowExample = leaderFlowExampleMap.get(item);
            // 不允许移除的情况西下，需要等待下次移除
            if (!flowExample.isAllowRemove()) {
                return;
            }
            this.leaderFlowExampleMap.remove(item);
        });

    }

    /**
     * 操作- 实例执行完后的事务
     *
     * @param example
     */
    private void transactionGroupOperate(FlowExample example) {
        // 获取事务组map
        Map<String, String> transactionGroupMap = example.getTransactionGroupMap();
        // 校验事务是否结束

        for (String taskGroupId : transactionGroupMap.keySet()) {
            if (example.getTransactionGroupSussIds().contains(taskGroupId) || example.getTransactionGroupFailIds().contains(taskGroupId)) {
                continue;
            }
            String groupId = transactionGroupMap.get(taskGroupId);
            Future<JsonObject> transactionFuture = transaction(example, groupId, taskGroupId);
            transactionFuture.onSuccess(suss -> {
                example.addFailTransactionGroupSuss(taskGroupId);
                checkExampleTransactionIsFinish(example);
                // 校验是否 事务完毕
            }).onFailure(fail -> {
                // 因为已经重试了- 10次，不需要再重试了
                example.addFailTransactionGroupFailId(taskGroupId);
                example.getTransactionPromise().fail(fail);
            });
            break;
        }
    }

    // 转正相关内容


    private void checkExampleTransactionIsFinish(FlowExample example) {
        Map<String, String> transactionMap = example.getTransactionGroupMap();
        Boolean isFinish = true;
        for (String taskGroupId : transactionMap.keySet()) {
            if (example.getTransactionGroupFailIds().contains(taskGroupId) || example.getTransactionGroupSussIds().contains(taskGroupId)) {
                continue;
            }
            isFinish = false;
            break;
        }
        if (isFinish) {
            example.getTransactionPromise().complete();
        }
    }

    /**
     * 处理事务组
     *
     * @param example
     * @param groupId
     * @param taskGroupId
     */
    private Future<JsonObject> transaction(FlowExample example, String groupId, String taskGroupId) {
        // 防止- 事务接口为空
        init();
        if (example.getTransactionGroupFailIds().contains(taskGroupId)) {
            Future<JsonObject> future = transactionInterface.rollBack(new JsonObject().put("groupId", groupId));
            return future;
        }
        // 进行提交
        Future<JsonObject> future = transactionInterface.commit(new JsonObject().put("groupId", groupId));
        return future;
    }

    /**
     * 执行具体的节点
     *
     * @param example
     */
    private void runPlan(FlowExample example) {
        // 进行等待，需要被唤醒
        example.runExample().onSuccess(suss -> {
            Object result = suss;
            // 使用变量把参数引入到该 区域内
            FlowRegister flowRegisterAsync = example.getFlowRegister();
            FlowElement flowElementAsync = example.getFlowElement();
            // 执行下一个节点
            try {
                Boolean isNext = true;
                if (example.getFlowElement().getElementType().equals(BpmnTypeEnum.SERVICE_TASK)) {
                    ServiceTask serviceTask = (ServiceTask) example.getFlowElement();
                    ServerTaskTypeEnum taskType = serviceTask.queryServiceTaskType();
                    // poll-轮询task
                    if (taskType.equals(ServerTaskTypeEnum.POLL)) {
                        JsonObject param = JsonObject.mapFrom(result);
                        String status = param.getString("status");
                       // log.info("获取到的状态为 {}", status);
                        if (StringUtils.equals(status, Constant.WAIT)) {
                            if (example.getPollRunCount() > serviceTask.queryPollCount()) {
                                param.put("status", Constant.FAIL);
                                result = param.mapTo(Object.class);
                            } else {
                                // 执行当前节点
                                isNext = false;
                            }
                        }
                    }
                }

                // 配合轮询使用
                if (isNext) {
                    noticeResult(example, flowElementAsync, result);
                    flowElementAsync = example.queryFlowNewElement();
                }
                example.removeFailFlowElement(flowElementAsync.getId());
                flowRegisterAsync.predictNextElementNew(example.getCsd(), flowElementAsync);
                runFlowExample(example, isNext, true);
            } catch (Exception e) {
                // 存入es-异常信息
                log.error("执行失败fail---- {}", ExceptionMessage.getStackTrace(e));
                endFlowExampleFail(example, e);
            }
        }).onFailure(fail -> {
            // 注册延迟队列
            log.error("执行失败fail {}", ExceptionMessage.getStackTrace(fail));
            FlowElement flowElementAsync = example.getFlowElement();
            // // 后续改造重试-- 当重（）; 改造子流程
            example.addFailFlowElement(flowElementAsync.getId());
            ServiceTask serviceTask = (ServiceTask) flowElementAsync;
            Integer retryCount = serviceTask.getRetryCount();
            if (example.queryFailCount(flowElementAsync.getId()) >= retryCount) {
                // 整体通知失败
                endFlowExampleFail(example, fail);
                return;
            }
            runFlowExample(example, false, true);
        });
    }

    public void pollRunFlowExample(FlowExample example) {
        runFlowExample(example, false, false);
    }

    public void daleyFinalRun(FlowExample example) {
        runFlowExample(example, false, false);
    }

    public void noticeResult(FlowExample example, FlowElement flowElement, Object result) {
        if (flowElement.getElementType() == BpmnTypeEnum.SERVICE_TASK) {
            ServiceTask serviceTask = (ServiceTask) flowElement;
            // 获取 TaskServiceDef
            Optional<TaskServiceDef> taskServiceDefOptional = this.storyEngineModule.getTaskContainer().getTaskServiceDef(serviceTask.getTaskComponent(), serviceTask.getTaskService(), example.getRole());
            TaskServiceDef taskServiceDef = taskServiceDefOptional.orElseThrow(() ->
                    ExceptionUtil.buildException(null, ExceptionEnum.TASK_SERVICE_MATCH_ERROR, ExceptionEnum.TASK_SERVICE_MATCH_ERROR.getDesc()
                            + GlobalUtil.format(" service task identity: {}", serviceTask.identity())));
            // 校验返回结果是否属于Mono--- 当前默认不支持
            example.getStoryBus().noticeResult(serviceTask, result, taskServiceDef);
            // 通知监控
            example.getFlowRegister().getMonitorTracking().finishTaskTracking(flowElement, null);
        }
    }


    public Future<JsonObject> registerTransaction(ServiceTask serviceTask, FlowExample example) {
        // 防止- 事务接口为空
        init();
        RegisterTransactionRequest request = new RegisterTransactionRequest();
        request.setRequestId(example.getRequestId());
        request.setTaskId(serviceTask.getId());
        request.setTaskGroupId(serviceTask.queryTransactionGroup());
        request.setGroupId(example.getTransactionGroupMap().get(serviceTask.queryTransactionGroup()));
        request.setWorkerName(schedulerManager.queryWorkerName(serviceTask.getTaskComponent()));
        return transactionInterface.registerTransaction(JsonObject.mapFrom(request));
    }

    private <T> FlowRegister getFlowRegister(StoryRequest<T> storyRequest, ScopeDataQuery scopeDataQuery) {
        String startId = storyRequest.getStartId();
        AssertUtil.notBlank(startId, ExceptionEnum.PARAMS_ERROR, "StartId is not allowed to be empty!");
        Optional<StartEvent> startEventOptional = storyEngineModule.getStartEventContainer().getStartEventById(scopeDataQuery);
        StartEvent startEvent = startEventOptional.orElseThrow(() -> ExceptionUtil
                .buildException(null, ExceptionEnum.PARAMS_ERROR, GlobalUtil.format("StartId did not match a valid StartEvent! startId: {}", startId)));
        return new FlowRegister(startEvent, storyRequest);
    }

    private <T> BasicStoryBus getStoryBus(StoryRequest<T> storyRequest, FlowRegister flowRegister, Role role) {
        String businessId = storyRequest.getBusinessId();
        ScopeData varScopeData = storyRequest.getVarScopeData();
        ScopeData staScopeData = storyRequest.getStaScopeData();
        MonitorTracking monitorTracking = flowRegister.getMonitorTracking();
        return new BasicStoryBus(storyRequest.getTimeout(), storyRequest.getStoryExecutor(),
                storyRequest.getRequestId(), storyRequest.getStartId(), businessId, role, monitorTracking, storyRequest.getRequest(), varScopeData, staScopeData);
    }


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
