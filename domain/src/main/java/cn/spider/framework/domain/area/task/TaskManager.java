package cn.spider.framework.domain.area.task;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.domain.area.agent.AgentVertxClient;
import cn.spider.framework.domain.area.datasource.DatasourceManager;
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
import cn.spider.framework.domain.area.task.data.AiAnalysisDemandParam;
import cn.spider.framework.domain.area.task.data.CreateCoderParam;
import cn.spider.framework.domain.area.task.data.QueryAiCoderStepResult;
import cn.spider.framework.domain.area.task.data.QueryDomainFunctionTaskResult;
import cn.spider.framework.domain.area.task.data.enums.CodeCreateType;
import cn.spider.framework.domain.area.task.data.enums.TaskStatus;
import cn.spider.framework.domain.area.task.data.enums.TaskType;
import cn.spider.framework.domain.area.task.entity.SpiderDomainFunctionAiCoderStep;
import cn.spider.framework.domain.area.task.entity.SpiderDomainFunctionTask;
import cn.spider.framework.domain.area.task.service.ISpiderDomainFunctionAiCoderStepService;
import cn.spider.framework.domain.area.task.service.ISpiderDomainFunctionTaskService;
import cn.spider.framework.domain.area.util.LockManager;
import cn.spider.framework.domain.sdk.data.DemandAnalysisParam;
import cn.spider.framework.domain.sdk.data.UpdateDemandsParam;
import cn.spider.node.framework.code.agent.sdk.data.CreateProjectResult;
import cn.spider.node.host.plugin.center.sdk.data.PluginOfflineParam;
import cn.spider.node.host.plugin.center.sdk.data.QueryFunctionVersionResult;
import cn.spider.node.host.plugin.center.sdk.data.QueryFunctionVersionsParam;
import cn.spider.node.host.plugin.center.sdk.interfaces.HostPluginInterface;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
     * 子域的基础代码信息
     */
    private IAreaDomainBaseInfoService baseInfoService;

    private ISpiderDomainFunctionTaskService spiderDomainFunctionTaskService;

    private AgentVertxClient agentVertxClient;

    private ISpiderDomainFunctionAiCoderStepService stepService;

    private ISpiderDataFlowService dataFlowService;

    private HostPluginInterface hostPluginInterface;
    // 锁更新code的操作
    private LockManager lockManager;

    private DatasourceManager datasourceManager;

    private final String codeCreateType = "codeCreateType";

    private final String updateCode = "updateCode";

    public TaskManager(ISpiderAreaFunctionVersionService spiderAreaFunctionVersionService,
                       ISpiderAreaFunctionService spiderAreaFunctionService,
                       IAreaDomainBaseInfoService baseInfoService,
                       ISpiderDomainFunctionTaskService spiderDomainFunctionTaskService,
                       AgentVertxClient agentVertxClient,
                       ISpiderDomainFunctionAiCoderStepService stepService,
                       ISpiderDataFlowService dataFlowService, HostPluginInterface hostPluginInterface, LockManager lockManager, DatasourceManager datasourceManager) {
        this.spiderAreaFunctionVersionService = spiderAreaFunctionVersionService;
        this.spiderAreaFunctionService = spiderAreaFunctionService;
        this.baseInfoService = baseInfoService;
        this.spiderDomainFunctionTaskService = spiderDomainFunctionTaskService;
        this.agentVertxClient = agentVertxClient;
        this.stepService = stepService;
        this.dataFlowService = dataFlowService;
        this.hostPluginInterface = hostPluginInterface;
        this.lockManager = lockManager;
        this.datasourceManager = datasourceManager;
    }

    // 新增领域功能的任务
    public void runDomainFunctionTask(String versionId, CodeCreateType codeCreateType, String selectedVersion) {
        // 判断下状态，如果是编译通过，就不能进行代码生成
        String lockKye = this.codeCreateType + versionId;
        if (!lockManager.lock(lockKye)) {
            Preconditions.checkArgument(false, "当前版本在代码生成中，请稍后再试");
            return;
        }
        SpiderAreaFunctionVersion functionVersion = spiderAreaFunctionVersionService.getById(versionId);
        SpiderDataFlow spiderDataFlow = dataFlowService.getById(functionVersion.getDataFlowId());

        String groupId = functionVersion.getFlowGroupParentId();


        Map<String, Object> dataFlowInfo = buildFlowInfo(groupId, spiderDataFlow);
        List<Integer> domainBaseIdList = JSON.parseArray(JSON.toJSONString(dataFlowInfo.get("domainBaseIds")), Integer.class);
        Set<Integer> domainBaseIds = Sets.newHashSet(domainBaseIdList);
        // 查询
        SpiderAreaFunction areaFunction = spiderAreaFunctionService.getById(functionVersion.getDomainFunctionId());
        String projectName = areaFunction.getName() + "_" + functionVersion.getVersion();
        String domainId = areaFunction.getAreaId();
        JsonObject domainBaseInfos = new JsonObject();
        SpiderAreaFunctionVersion reuseFunction = null;
        switch (codeCreateType) {
            // 复用版本
            case reuse_version:
                Preconditions.checkArgument(StringUtils.isNotEmpty(selectedVersion), "请选择版本");
                reuseFunction = spiderAreaFunctionVersionService.lambdaQuery()
                        .eq(SpiderAreaFunctionVersion::getDomainFunctionId, functionVersion.getDomainFunctionId())
                        .eq(SpiderAreaFunctionVersion::getVersion, selectedVersion).one();
                break;
            //重新创建
            case create:
                break;
            // 复用参数
            case reuse_param:
                reuseFunction = functionVersion;
                break;
        }

        // 发起ai生成代码
        if (Objects.nonNull(reuseFunction)) {
            QueryFunctionVersionsParam param = new QueryFunctionVersionsParam(reuseFunction.getId());
            hostPluginInterface.queryVersionParam(JsonObject.mapFrom(param)).onSuccess(res -> {
                QueryFunctionVersionResult queryFunctionVersionResult = res.mapTo(QueryFunctionVersionResult.class);
                createCoder(domainBaseIds,
                        domainBaseInfos,
                        areaFunction,
                        projectName,
                        functionVersion,
                        versionId,
                        domainId,
                        dataFlowInfo,
                        queryFunctionVersionResult.getAreaFunctionParamClass(),
                        queryFunctionVersionResult.getAreaFunctionResultClass(),
                        queryFunctionVersionResult.getServiceName());
            }).onFailure(fail -> {
                log.error("查询版本信息失败 {}", ExceptionMessage.getStackTrace(fail));
            });
            return;
        }
        QueryFunctionVersionsParam param = new QueryFunctionVersionsParam(functionVersion.getId());
        hostPluginInterface.queryVersionParam(JsonObject.mapFrom(param)).onSuccess(res -> {
            QueryFunctionVersionResult queryFunctionVersionResult = res.mapTo(QueryFunctionVersionResult.class);
            createCoder(domainBaseIds,
                    domainBaseInfos,
                    areaFunction,
                    projectName,
                    functionVersion,
                    versionId,
                    domainId,
                    dataFlowInfo,
                    null,
                    null, queryFunctionVersionResult.getServiceName());
        }).onFailure(fail -> {
            log.error("查询版本信息失败 {}", ExceptionMessage.getStackTrace(fail));
        });
    }


    private void createCoder(Set<Integer> domainBaseInfoIds,
                             JsonObject domainBaseInfos,
                             SpiderAreaFunction areaFunction,
                             String projectName,
                             SpiderAreaFunctionVersion functionVersion,
                             String versionId,
                             String domainId,
                             Map<String, Object> dataFlowInfo,
                             String inputParam,
                             String outParam,
                             String serviceName) {
        List<JsonObject> domainBaseInfoJson = buildDomainInfo(domainBaseInfoIds);
        domainBaseInfos.put("domainBaseInfos", domainBaseInfoJson);
        domainBaseInfos.put("taskComponent", firstLowerCase(areaFunction.getTaskComponent()));
        domainBaseInfos.put("taskService", firstLowerCase(areaFunction.getTaskService()));

        CreateCoderParam createCoderParam = new CreateCoderParam(projectName, domainBaseInfos, functionVersion.getFunctionFunctional().getFunctionalList());
        createCoderParam.setInputParam(inputParam);
        // 根据参数来判断，是否采用预定参数
        createCoderParam.setCustomizedParam(StringUtils.isNotEmpty(inputParam));
        createCoderParam.setOutParam(outParam);
        createCoderParam.setDataFlow(dataFlowInfo);
        // 创建任务
        SpiderDomainFunctionTask spiderDomainFunctionTasks = spiderDomainFunctionTaskService.lambdaQuery().eq(SpiderDomainFunctionTask::getDomainFunctionVersionId, versionId).one();
        SpiderDomainFunctionTask domainFunctionTask = new SpiderDomainFunctionTask();
        domainFunctionTask.setId(Objects.nonNull(spiderDomainFunctionTasks) ? spiderDomainFunctionTasks.getId() : null);
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
        Integer baseInfoId = domainBaseInfoIds.stream().findFirst().orElse(null);
        String datasource = queryDatasource(baseInfoId);
        String datasourceId = queryDatasourceId(datasource);
        // 更新datasource
        spiderAreaFunctionVersionService.lambdaUpdate()
                .set(SpiderAreaFunctionVersion::getDatasourceId, datasourceId)
                .eq(SpiderAreaFunctionVersion::getId, versionId)
                .update();
        createCoderParam.setDatasource(datasource);
        // 重新给领域对象信息 + 数据流程信息
        //createCoderParam.setDomainInfoAnalysis(spiderDataFlow.getDataFlowAnalysisModel().getDomainInfoResult());
        //createCoderParam.setDataFlowAnalysis(spiderDataFlow.getDataFlowAnalysisModel().getFlowDataResult());
        //createCoderParam.setDataFlow(spiderDataFlow.getData());
        createCoderParam.setNeedDataFlow(Boolean.TRUE);
        createCoderParam.setDomainFunctionVersionId(versionId);
        if (StringUtils.isEmpty(serviceName)) {
            createCoderParam.setServiceName(serviceName);
        }
        log.info("create_coder_info {}", JSON.toJSONString(createCoderParam));
        agentVertxClient.createCoder(JsonObject.mapFrom(createCoderParam));
        functionVersion.setStatus(NodeStatus.CODING);
        spiderAreaFunctionVersionService.updateById(functionVersion);
        if (Objects.isNull(spiderDomainFunctionTasks)) {
            return;
        }
        Wrapper<SpiderDomainFunctionAiCoderStep> queryWrapper = new LambdaQueryWrapper<SpiderDomainFunctionAiCoderStep>()
                .eq(SpiderDomainFunctionAiCoderStep::getSpiderDomainFunctionTaskId, domainFunctionTask.getId());
        stepService.remove(queryWrapper);
    }

    public void analysisDemand(DemandAnalysisParam param) {
        log.info("analysis_demand_param {}", JSON.toJSONString(param));
        SpiderDataFlow spiderDataFlow = dataFlowService.getById(param.getFlowId());
        SpiderAreaFunctionVersion functionVersion = spiderAreaFunctionVersionService.getById(param.getFunctionVersionId());
        Map<String, Object> dataFlowInfo = buildFlowInfo(functionVersion.getFlowGroupParentId(), spiderDataFlow);
        AiAnalysisDemandParam analysisDemandParam = new AiAnalysisDemandParam(dataFlowInfo, param.getDemands(), param.getFunctionVersionId());
        agentVertxClient.analysisDemand(JsonObject.mapFrom(analysisDemandParam));
    }


    private Map<String, Object> buildFlowInfo(String groupId, SpiderDataFlow spiderDataFlow) {
        JSONArray cells = spiderDataFlow.getData().getJSONArray("cells");
        List<JSONObject> selectedCells = new ArrayList<>();
        // 用于存groupId组中的节点id
        Set<String> domainIds = new HashSet<>();
        Set<Integer> domainBaseIds = new HashSet<>();
        for (int i = 0; i < cells.size(); i++) {
            JSONObject cell = cells.getJSONObject(i);
            String shape = cell.getString("shape");
            if (StringUtils.equals(shape, "custom-group-node") || StringUtils.equals(shape, "er-rect")) {
                if (cell.getString("id").equals(groupId) || (StringUtils.isNotEmpty(cell.getString("parent"))
                        && StringUtils.equals(groupId, cell.getString("parent")))) {
                    selectedCells.add(cell);
                    domainIds.add(cell.getString("id"));
                    if (StringUtils.equals(shape, "er-rect")) {
                        domainBaseIds.add(cell.getInteger("id"));
                    }
                }
            }
        }
        // 获取连线-edge的内容信息
        if (CollectionUtils.isNotEmpty(selectedCells)) {
            // 说明领域中数据不为空
            for (int i = 0; i < cells.size(); i++) {
                JSONObject cell = cells.getJSONObject(i);
                String shape = cell.getString("shape");
                if (StringUtils.equals(shape, "edge")) {
                    JSONObject source = cell.getJSONObject("source");
                    JSONObject target = cell.getJSONObject("target");
                    String sourceId = source.getString("cell");
                    String targetId = target.getString("cell");
                    if (domainIds.contains(sourceId) && domainIds.contains(targetId)) {
                        selectedCells.add(cell);
                    }
                }
            }
        }
        List<JSONObject> desc = spiderDataFlow.getFlowDataDesc().getFlowDataDescMap().get(groupId);

        Map<String, Object> dataFlowInfo = new HashMap<>();
        dataFlowInfo.put("flow_desc", desc);
        dataFlowInfo.put("flow_data", selectedCells);
        dataFlowInfo.put("domainBaseIds", domainBaseIds);
        return dataFlowInfo;
    }

    public List<JsonObject> buildDomainInfo(Set<Integer> ids) {
        List<AreaDomainBaseInfo> areaDomainBaseInfos = baseInfoService.lambdaQuery()
                .in(AreaDomainBaseInfo::getId, ids).list();
        return areaDomainBaseInfos.stream().map(item -> {
            JsonObject domainBaseInfo = JsonObject.mapFrom(item);
            domainBaseInfo.put("domainObjectName", firstLowerCase(item.getDomainObjectEntityName()));
            return domainBaseInfo;
        }).collect(Collectors.toList());
    }

    public String queryDatasource(Integer id) {
        AreaDomainBaseInfo domainBaseInfo = baseInfoService.lambdaQuery().eq(AreaDomainBaseInfo::getId, id).one();
        return domainBaseInfo.getDatasourceName();
    }

    public String queryDatasourceId(String datasource) {
        return datasourceManager.queryDatasourceId(datasource);
    }

    public Future<Void> updateCoder(JsonObject param) {

        log.info("update_coder_info {}", param);
        String domainFunctionVersionId = param.getString("domainFunctionVersionId");
        String domainFunctionVersionLockId = this.updateCode + domainFunctionVersionId;
        if (!lockManager.lock(domainFunctionVersionLockId)) {
            return Future.failedFuture("在更新中,请稍后在世");
        }

        String datasourceId = queryDatasourceIdByDomainVersion(domainFunctionVersionId);
        // 更新datasource
        spiderAreaFunctionVersionService.lambdaUpdate()
                .set(SpiderAreaFunctionVersion::getDatasourceId, datasourceId)
                .eq(SpiderAreaFunctionVersion::getId, domainFunctionVersionId)
                .update();

        Promise<Void> promise = Promise.promise();
        agentVertxClient.updatePlugin(param).onSuccess(suss -> {
            // 发起跟k8s交互
            // 构造基础信息成功- 开始发起部署
            CreateProjectResult projectResult = suss.mapTo(CreateProjectResult.class);
            hostPluginInterface.pluginOnline(new JsonObject().put("functionId", projectResult.getId())).onFailure(fail -> {
                log.warn("发起部署失败 {}", ExceptionMessage.getStackTrace(fail));
                promise.fail(fail);
                lockManager.unLock(domainFunctionVersionLockId);
            }).onSuccess(deploySuss -> {
                log.warn("发起部署成功 {}", param.toString());
                promise.complete();
                lockManager.unLock(domainFunctionVersionLockId);
            });
        }).onFailure(fail -> {
            promise.fail(fail);
            log.error("更新插件失败 {}", ExceptionMessage.getStackTrace(fail));
            lockManager.unLock(domainFunctionVersionLockId);
        });
        return promise.future();
    }

    /**
     * 新增工具一个方法，支持把一个英文首字母变小写
     */
    private String firstLowerCase(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public void syncAiCoderStep(SpiderDomainFunctionAiCoderStep step) {
        if (StringUtils.isNotEmpty(step.getError())) {
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

    private String queryDatasourceIdByDomainVersion(String domainFunctionVersionId) {
        SpiderAreaFunctionVersion functionVersion = spiderAreaFunctionVersionService.getById(domainFunctionVersionId);
        SpiderDataFlow spiderDataFlow = dataFlowService.getById(functionVersion.getDataFlowId());
        Set<Integer> domainBaseInfoIds = JSON.parseObject(spiderDataFlow.getSonAreaIds(), Set.class);
        Integer baseInfoId = domainBaseInfoIds.stream().findFirst().orElse(null);
        String datasource = queryDatasource(baseInfoId);
        return queryDatasourceId(datasource);
    }

    public Future<Void> uninstallBiz(String domainFunctionVersionId) {
        Promise<Void> promise = Promise.promise();
        PluginOfflineParam pluginOfflineParam = new PluginOfflineParam(domainFunctionVersionId);
        hostPluginInterface.pluginOffline(JsonObject.mapFrom(pluginOfflineParam)).onSuccess(suss -> {
            log.info("插件下线成功 {}", domainFunctionVersionId);
            promise.complete();
        }).onFailure(fail -> {
            log.info("插件下线失败功能id为 {},异常信息为 {}", domainFunctionVersionId, ExceptionMessage.getStackTrace(fail));
            promise.fail(fail);
        });
        return promise.future();
    }
}
