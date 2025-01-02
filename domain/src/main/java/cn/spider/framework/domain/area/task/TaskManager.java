package cn.spider.framework.domain.area.task;

import cn.spider.framework.domain.area.agent.AgentVertxClient;
import cn.spider.framework.domain.area.flowdata.entity.SpiderDataFlow;
import cn.spider.framework.domain.area.flowdata.service.ISpiderDataFlowService;
import cn.spider.framework.domain.area.node.data.QueryDomainFunctionResult;
import cn.spider.framework.domain.area.node.data.SonDomainInfoFunctionModel;
import cn.spider.framework.domain.area.node.data.enums.NodeStatus;
import cn.spider.framework.domain.area.node.entity.SpiderAreaFunction;
import cn.spider.framework.domain.area.node.entity.SpiderAreaFunctionVersion;
import cn.spider.framework.domain.area.node.service.ISpiderAreaFunctionService;
import cn.spider.framework.domain.area.node.service.ISpiderAreaFunctionVersionService;
import cn.spider.framework.domain.area.sondomain.entity.AreaDomainBaseInfo;
import cn.spider.framework.domain.area.sondomain.entity.SpiderSonArea;
import cn.spider.framework.domain.area.sondomain.service.IAreaDomainBaseInfoService;
import cn.spider.framework.domain.area.sondomain.service.ISpiderSonAreaService;
import cn.spider.framework.domain.area.task.data.CreateCoderParam;
import cn.spider.framework.domain.area.task.data.QueryAiCoderStepResult;
import cn.spider.framework.domain.area.task.data.QueryDomainFunctionTaskResult;
import cn.spider.framework.domain.area.task.data.enums.TaskStatus;
import cn.spider.framework.domain.area.task.data.enums.TaskType;
import cn.spider.framework.domain.area.task.entity.SpiderDomainFunctionAiCoderStep;
import cn.spider.framework.domain.area.task.entity.SpiderDomainFunctionTask;
import cn.spider.framework.domain.area.task.service.ISpiderDomainFunctionAiCoderStepService;
import cn.spider.framework.domain.area.task.service.ISpiderDomainFunctionTaskService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.base.Preconditions;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ai任务的管理类
 */
@Slf4j
public class TaskManager {
    /**
     * 功能版本的service
     */
    private ISpiderAreaFunctionVersionService spiderAreaFunctionVersionService;

    /**
     * 领域功能的service
     */
    private ISpiderAreaFunctionService spiderAreaFunctionService;

    /**
     * 子域的service
     */
    private ISpiderSonAreaService spiderSonAreaService;

    /**
     * 子域的基础代码信息
     */
    private IAreaDomainBaseInfoService baseInfoService;

    private ISpiderDomainFunctionTaskService spiderDomainFunctionTaskService;

    private AgentVertxClient agentVertxClient;

    private ISpiderDomainFunctionAiCoderStepService stepService;

    private ISpiderDataFlowService dataFlowService;

    public TaskManager(ISpiderAreaFunctionVersionService spiderAreaFunctionVersionService,
                       ISpiderAreaFunctionService spiderAreaFunctionService,
                       ISpiderSonAreaService spiderSonAreaService,
                       IAreaDomainBaseInfoService baseInfoService,
                       ISpiderDomainFunctionTaskService spiderDomainFunctionTaskService,
                       AgentVertxClient agentVertxClient, ISpiderDomainFunctionAiCoderStepService stepService,ISpiderDataFlowService dataFlowService) {
        this.spiderAreaFunctionVersionService = spiderAreaFunctionVersionService;
        this.spiderAreaFunctionService = spiderAreaFunctionService;
        this.spiderSonAreaService = spiderSonAreaService;
        this.baseInfoService = baseInfoService;
        this.spiderDomainFunctionTaskService = spiderDomainFunctionTaskService;
        this.agentVertxClient = agentVertxClient;
        this.stepService = stepService;
        this.dataFlowService = dataFlowService;
    }

    public void retryDomainFunctionTask(String versionId) {
        SpiderAreaFunctionVersion functionVersion = spiderAreaFunctionVersionService.getById(versionId);

        if (Objects.isNull(functionVersion)) {
            return;
        }
        if(functionVersion.getStatus().equals(NodeStatus.INIT) || functionVersion.getStatus().equals(NodeStatus.CODING)){
            return;
        }


    }

    // 新增领域功能的任务
    public void runDomainFunctionTask(String versionId) {
        // 判断下状态，如果是编译通过，就不能进行代码生成
        SpiderAreaFunctionVersion functionVersion = spiderAreaFunctionVersionService.getById(versionId);
        if (!functionVersion.getStatus().equals(NodeStatus.INIT)) {
            Preconditions.checkArgument(false, "该功能版本状态为：" + functionVersion.getStatus() + "，不能进行代码生成");
        }
        Set<Integer> sonDomainIds = functionVersion.getSonDomainFunctions().getSonDomainFunctionList().stream().map(SonDomainInfoFunctionModel::getSonDomainId).collect(Collectors.toSet());
        List<SpiderSonArea> sonAreas = spiderSonAreaService.lambdaQuery().in(SpiderSonArea::getId, sonDomainIds).list();
        String domainId = sonAreas.get(0).getAreaId();
        Set<Integer> domainBaseInfoIds = functionVersion.getSonDomainFunctions().getSonDomainFunctionList().stream().map(SonDomainInfoFunctionModel::getVersionId).collect(Collectors.toSet());
        List<AreaDomainBaseInfo> areaDomainBaseInfos = baseInfoService.lambdaQuery()
                .in(AreaDomainBaseInfo::getId, domainBaseInfoIds).list();
        SpiderAreaFunction areaFunction = spiderAreaFunctionService.getById(functionVersion.getDomainFunctionId());
        String projectName = areaFunction.getName() + "_" + functionVersion.getVersion();

        JsonObject domainBaseInfos = new JsonObject();

        List<JsonObject> domainBaseInfoJson = areaDomainBaseInfos.stream().map(item -> {
            JsonObject domainBaseInfo = JsonObject.mapFrom(item);
            domainBaseInfo.put("domainObjectName", firstLowerCase(item.getDomainObjectEntityName()));
            return domainBaseInfo;
        }).collect(Collectors.toList());
        domainBaseInfos.put("domainBaseInfos", domainBaseInfoJson);
        domainBaseInfos.put("taskComponent", firstLowerCase(areaFunction.getTaskComponent()));
        domainBaseInfos.put("taskService", firstLowerCase(areaFunction.getTaskService()));

        CreateCoderParam createCoderParam = new CreateCoderParam(projectName, domainBaseInfos, functionVersion.getFunctionFunctional().getFunctionalList());
        // 创建任务
        SpiderDomainFunctionTask spiderDomainFunctionTasks = spiderDomainFunctionTaskService.lambdaQuery().eq(SpiderDomainFunctionTask::getDomainFunctionVersionId, versionId).one();
        SpiderDomainFunctionTask domainFunctionTask = new SpiderDomainFunctionTask();
        domainFunctionTask.setId(Objects.nonNull(spiderDomainFunctionTasks) ? spiderDomainFunctionTasks.getId(): null);
        domainFunctionTask.setDomainFunctionVersionId(functionVersion.getId());
        domainFunctionTask.setDomainFunctionId(functionVersion.getDomainFunctionId());
        domainFunctionTask.setTaskDomainId(domainId);

        domainFunctionTask.setSonDomainInfo(areaFunction.getSonDomainInfo());
        domainFunctionTask.setTaskType(TaskType.NEWLY_ADDED);
        domainFunctionTask.setStatus(TaskStatus.DATA_INIT);
        spiderDomainFunctionTaskService.saveOrUpdate(domainFunctionTask);
        // 发起跟ai交互
        createCoderParam.setTaskId(domainFunctionTask.getId());
        createCoderParam.setBaseInfoIds(domainBaseInfoIds);
        if(Objects.nonNull(functionVersion.getDataFlowId())){
            SpiderDataFlow spiderDataFlow = dataFlowService.getById(functionVersion.getDataFlowId());
            createCoderParam.setDataFlow(spiderDataFlow.getData());
            createCoderParam.setNeedDataFlow(Boolean.TRUE);
            createCoderParam.setDataFlowDesc(spiderDataFlow.getFlowDataDesc());
        }else {
            createCoderParam.setNeedDataFlow(Boolean.FALSE);
            createCoderParam.setDataFlow(new JSONObject());
            createCoderParam.setDataFlowDesc("");
        }
        createCoderParam.setDomainFunctionVersionId(versionId);
        log.info("create_coder_info {}", JSON.toJSONString(createCoderParam));
        agentVertxClient.createCoder(JsonObject.mapFrom(createCoderParam));
        functionVersion.setStatus(NodeStatus.CODING);
        spiderAreaFunctionVersionService.updateById(functionVersion);
        if(Objects.isNull(spiderDomainFunctionTasks)){
            return;
        }
        Wrapper<SpiderDomainFunctionAiCoderStep> queryWrapper = new LambdaQueryWrapper<SpiderDomainFunctionAiCoderStep>()
                .eq(SpiderDomainFunctionAiCoderStep::getSpiderDomainFunctionTaskId, domainFunctionTask.getId());
        stepService.remove(queryWrapper);
    }

    public void updateCoder(JsonObject param) {
        agentVertxClient.updatePlugin(param).onSuccess(suss->{
            // 发起更新,重新部署
            // todo 优化 调用k8s-api
        }).onFailure(fail->{

        });
    }

    /**
     * 新增工具一个方法，支持把一个英文首字母变小写
     */
    private String firstLowerCase(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public void syncAiCoderStep(SpiderDomainFunctionAiCoderStep step) {
        if(StringUtils.isNotEmpty(step.getError())){
            SpiderDomainFunctionTask task = spiderDomainFunctionTaskService.lambdaQuery().eq(SpiderDomainFunctionTask::getId, step.getSpiderDomainFunctionTaskId()).one();
            spiderAreaFunctionVersionService.lambdaUpdate()
                    .set(SpiderAreaFunctionVersion::getStatus, NodeStatus.CODING_FAIL)
                    .eq(SpiderAreaFunctionVersion::getId, task.getDomainFunctionVersionId())
                    .update();
            task.setStatus(TaskStatus.ERROR);
            spiderDomainFunctionTaskService.updateById(task);
        }
        stepService.save(step);
    }

    public QueryAiCoderStepResult queryAiCoderStep(String functionVersionId) {

        SpiderDomainFunctionTask task = spiderDomainFunctionTaskService.lambdaQuery()
                .eq(SpiderDomainFunctionTask::getDomainFunctionVersionId, functionVersionId)
                .one();
        List<SpiderDomainFunctionAiCoderStep> steps = stepService.lambdaQuery().eq(SpiderDomainFunctionAiCoderStep::getSpiderDomainFunctionTaskId, task.getId()).list();

        // 获取tasks中创建时间最小的时间
        Date minCreateTime = steps.stream().map(SpiderDomainFunctionAiCoderStep::getCreateTime).min(Date::compareTo).get();
        Date maxCreateTime = steps.stream().map(SpiderDomainFunctionAiCoderStep::getCreateTime).max(Date::compareTo).get();
        long between = maxCreateTime.getTime() - minCreateTime.getTime();
        // 获取maxCreateTime与minCreateTime之间的时差
        QueryAiCoderStepResult result = new QueryAiCoderStepResult();
        result.setSteps(steps);
        result.setTakeTime(between);
        return result;
    }
}
