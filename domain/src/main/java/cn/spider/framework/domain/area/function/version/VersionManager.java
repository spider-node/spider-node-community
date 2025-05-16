package cn.spider.framework.domain.area.function.version;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.container.sdk.data.UnloadBpmnParam;
import cn.spider.framework.container.sdk.interfaces.ContainerService;
import cn.spider.framework.domain.area.agent.AgentVertxClient;
import cn.spider.framework.domain.area.data.*;
import cn.spider.framework.domain.area.data.enums.BpmnStatus;
import cn.spider.framework.domain.area.data.enums.FunctionNodeType;
import cn.spider.framework.domain.area.flowdata.entity.SpiderDataFlow;
import cn.spider.framework.domain.area.flowdata.service.ISpiderDataFlowService;
import cn.spider.framework.domain.area.function.data.ExecuteFunctionInfo;
import cn.spider.framework.domain.area.function.data.FunctionParamConfigModel;
import cn.spider.framework.domain.area.function.data.GenerateJavaCodeParam;
import cn.spider.framework.domain.area.function.data.GenerateJavaCodeResult;
import cn.spider.framework.domain.area.function.entity.SpiderBusinessFunctionVersion;
import cn.spider.framework.domain.area.function.enums.GenerateCoderType;
import cn.spider.framework.domain.area.function.service.ISpiderBusinessFunctionVersionService;
import cn.spider.framework.domain.area.function.version.data.*;
import cn.spider.framework.domain.area.function.version.data.enums.VersionStatus;
import cn.spider.framework.domain.area.function.version.enums.ToJavaEntitySource;
import cn.spider.framework.domain.area.http.entity.SpiderToolHttp;
import cn.spider.framework.domain.area.http.service.ISpiderToolHttpService;
import cn.spider.framework.domain.area.node.NodeManger;
import cn.spider.framework.domain.area.task.TaskManager;
import cn.spider.framework.domain.area.util.LockManager;
import cn.spider.framework.domain.sdk.data.*;
import cn.spider.framework.domain.sdk.data.enums.UploadBpmnStatus;
import cn.spider.node.host.plugin.center.sdk.data.QueryDomainFunctionClassInfo;
import cn.spider.node.host.plugin.center.sdk.data.QueryInputParam;
import cn.spider.node.host.plugin.center.sdk.interfaces.HostPluginInterface;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.function.version
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-14  22:04
 * @Description: 版本管理
 * @Version: 1.0
 */
@Slf4j
public class VersionManager {

    private MySQLPool client;

    private ContainerService containerService;

    private AgentVertxClient agentVertxClient;

    private ISpiderBusinessFunctionVersionService spiderBusinessFunctionVersionService;

    private HostPluginInterface hostPluginInterface;

    private ISpiderDataFlowService spiderDataFlowService;

    private LockManager lockManager;

    private ISpiderToolHttpService spiderToolHttpService;


    private final String BUILD_JS_PARAM = "BUILD_JS_PARAM";

    private TaskManager taskManager;

    public VersionManager(MySQLPool client,
                          ContainerService containerService,
                          ISpiderBusinessFunctionVersionService spiderBusinessFunctionVersionService,
                          LockManager lockManager,
                          AgentVertxClient agentVertxClient,
                          HostPluginInterface hostPluginInterface,
                          ISpiderDataFlowService spiderDataFlowService, ISpiderToolHttpService spiderToolHttpService,TaskManager taskManager) {
        this.hostPluginInterface = hostPluginInterface;
        this.spiderDataFlowService = spiderDataFlowService;
        this.client = client;
        this.containerService = containerService;
        this.spiderBusinessFunctionVersionService = spiderBusinessFunctionVersionService;
        this.lockManager = lockManager;
        this.agentVertxClient = agentVertxClient;
        this.spiderToolHttpService = spiderToolHttpService;
        this.taskManager = taskManager;
    }

    private RowMapper<FunctionVersionModel> ROW_BUSINESS = row -> {
        FunctionVersionModel functionVersionModel = new FunctionVersionModel();
        functionVersionModel.setId(row.getString("id"));
        functionVersionModel.setFunctionName(row.getString("function_name"));
        functionVersionModel.setDesc(row.getString("desc"));
        functionVersionModel.setVersion(row.getString("version"));
        functionVersionModel.setFunctionId(row.getString("function_id"));
        functionVersionModel.setBpmnUrl(row.getString("bpmn_url"));
        functionVersionModel.setStartEventId(row.getString("start_event_id"));
        functionVersionModel.setBpmnName(row.getString("bpmn_name"));
        if (StringUtils.isNotEmpty(row.getString("bpmn_status"))) {
            functionVersionModel.setBpmnStatus(BpmnStatus.valueOf(row.getString("bpmn_status")));
        }

        return functionVersionModel;
    };


    /**
     * 新增功能版本
     *
     * @param functionVersionModel
     * @return
     */
    public Future<Void> createFunctionVersion(FunctionVersionModel functionVersionModel) {
        Promise<Void> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();
        functionVersionModel.setId(UUID.randomUUID().toString());
        functionVersionModel.setId(UUID.randomUUID().toString());
        if (Objects.isNull(functionVersionModel.getStatus())) {
            functionVersionModel.setStatus(VersionStatus.STOP);
        }
        functionVersionModel.setBpmnStatus(BpmnStatus.INIT);
        JsonObject param = JsonObject.mapFrom(functionVersionModel);
        Map<String, Object> parameters = param.getMap();

        sql.append("insert into spider_business_function_version (id,`desc`,version,function_name,function_id,`status`,`bpmn_status`) values (#{id},#{desc},#{version},#{functionName},#{functionId},#{status},#{bpmnStatus})");
        SqlTemplate
                .forUpdate(client, sql.toString())
                .execute(parameters)
                .onSuccess(function -> {
                    promise.complete();
                }).onFailure(fail -> {
                    log.info("新增version错误信息为 {}", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

    /**
     * update功能版本
     *
     * @param functionVersionModel
     * @return
     */
    public Future<Void> updateFunctionVersion(FunctionVersionModel functionVersionModel) {
        Promise<Void> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();

        sql.append("update spider_business_function_version set function_name = #{functionName}, `desc` = #{desc} ,version = #{version},function_id = #{functionId},bpmn_url = #{bpmnUrl},start_event_id = #{startEventId} ,`bpmn_name` = #{bpmnName},`bpmn_status` = #{bpmnStatus},`status` = #{status} where id = #{id}");
        JsonObject param = JsonObject.mapFrom(functionVersionModel);
        Map<String, Object> parameters = param.getMap();
        SqlTemplate
                .forUpdate(client, sql.toString())
                .execute(parameters)
                .onSuccess(function -> {
                    promise.complete();
                }).onFailure(fail -> {
                    promise.fail(fail);
                });
        return promise.future();
    }


    /**
     * 功能启停-整体功能开关
     *
     * @param
     * @return
     */
    public Future<Void> startStop(VersionStopStartParam param) {
        Promise<Void> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();
        QueryVersionFunctionParam queryVersionFunctionParam = new QueryVersionFunctionParam();
        queryVersionFunctionParam.setVersionId(param.getVersionId());
        queryVersionFunctionParam.setStatus(VersionStatus.START.name());
        queryVersionFunctionParam.setPage(1);
        queryVersionFunctionParam.setSize(1);
        Future<List<FunctionVersionModel>> functionFuture = selectVersion(queryVersionFunctionParam);
        functionFuture.onSuccess(suss -> {
            List<FunctionVersionModel> functionVersionModels = suss;
            if (param.equals(VersionStatus.START)) {
                if (CollectionUtils.isEmpty(functionVersionModels)) {
                    promise.fail("该功能中存在一个有效版本,不允许启动其他版本");
                }
            }
            sql.append("update spider_business_function_version set `status` = #{status} where id = #{id}");
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("id", param.getVersionId());
            parameters.put("status", param.getStatus().name());
            SqlTemplate
                    .forUpdate(client, sql.toString())
                    .execute(parameters)
                    .onSuccess(function -> {
                        RefreshBpmnParam refreshBpmnParam = new RefreshBpmnParam();
                        refreshBpmnParam.setFunctionVersionId(param.getVersionId());
                        refreshBpmnParam.setStatus(param.getStatus().equals(VersionStatus.START) ? UploadBpmnStatus.DEPLOY : UploadBpmnStatus.INIT);
                        Future<Void> refreshFuture = refreshBpmn(refreshBpmnParam);
                        refreshFuture.onSuccess(refreshSuss -> {
                            promise.complete();
                        }).onFailure(refreshFail -> {
                            promise.fail(refreshFail);
                        });
                        // 发生事件---->该节点信息-
                    }).onFailure(fail -> {
                        promise.fail(fail);
                    });

        }).onFailure(fail -> {
            promise.fail(fail);
        });
        return promise.future();
    }

    /**
     * 查询版本信息
     *
     * @param param
     * @return
     */
    public Future<List<FunctionVersionModel>> selectVersion(QueryVersionFunctionParam param) {

        Promise<List<FunctionVersionModel>> promise = Promise.promise();

        StringBuilder sql = new StringBuilder();
        param.setPage((param.getPage() - 1) * param.getSize());
        JsonObject params = JsonObject.mapFrom(param);
        Map<String, Object> parameters = params.getMap();
        sql.append("select * from spider_business_function_version where 1=1 ");
        if (StringUtils.isNotEmpty(param.getFunctionId())) {
            sql.append(" and function_id = #{functionId} ");
        }

        if (StringUtils.isNotEmpty(param.getStartEventId())) {
            sql.append(" and start_event_id = #{startEventId} ");
        }

        if (StringUtils.isNotEmpty(param.getVersion())) {
            sql.append(" and version = #{version} ");
        }

        /*if (StringUtils.isNotEmpty(param.getStatus())) {
            sql.append(" and `status` = #{status} ");
        }*/

        if (StringUtils.isNotEmpty(param.getFunctionName())) {
            sql.append(" and function_name = #{functionName} ");
        }

        if (StringUtils.isNotEmpty(param.getVersionId())) {
            sql.append(" and id = #{versionId} ");
        }

        sql.append(" order by create_time limit #{page},#{size}");

        SqlTemplate
                .forQuery(client, sql.toString())
                .mapTo(ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(functions -> {
                    RowSet<FunctionVersionModel> function = functions;
                    List<FunctionVersionModel> businessFunctionList = Lists.newArrayList();
                    function.forEach(item -> {
                        businessFunctionList.add(item);
                    });
                    promise.complete(businessFunctionList);
                }).onFailure(fail -> {
                    log.error("查询数据失败 {}", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }


    /**
     * 查询版本信息
     *
     * @param param
     * @return
     */
    public List<ExecuteFunctionInfo> selectVersionV2(QueryVersionFunctionParam param) {

        List<SpiderBusinessFunctionVersion> businessFunctionVersions = spiderBusinessFunctionVersionService.lambdaQuery()
                .eq(StringUtils.isNotEmpty(param.getFunctionId()), SpiderBusinessFunctionVersion::getFunctionId, param.getFunctionId())
                .eq(StringUtils.isNotEmpty(param.getVersionId()), SpiderBusinessFunctionVersion::getId, param.getVersionId())
                .list();
        return businessFunctionVersions.stream().map(item -> {
            Optional<NodeParamConfig> nodeParamConfig = item.getNodeParamInfo().getNodeParamConfigList()
                    .stream()
                    .filter(items -> items.getJsFunctionParams()
                            .contains("result")).findFirst();
            NodeParamConfig config = nodeParamConfig.isPresent() ? nodeParamConfig.get() : null;
            return ExecuteFunctionInfo.builder()
                    .functionId(item.getFunctionId())
                    .startId(item.getStartEventId())
                    .versionId(item.getId())
                    .functionName(item.getFunctionName())
                    .nodeParamConfig(config)
                    .build();
        }).collect(Collectors.toList());
    }


    // 刷新-bpmn
    public Future<Void> refreshBpmn(RefreshBpmnParam param) {
        Promise<Void> promise = Promise.promise();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", param.getFunctionVersionId());
        SqlTemplate
                .forQuery(client, "SELECT * FROM spider_business_function_version where id = #{id}")
                .mapTo(ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(functions -> {
                    RowSet<FunctionVersionModel> function = functions;
                    List<FunctionVersionModel> businessFunctionList = Lists.newArrayList();
                    function.forEach(item -> {
                        businessFunctionList.add(item);
                    });
                    FunctionVersionModel functionVersionModel = businessFunctionList.get(0);
                    if (StringUtils.isEmpty(functionVersionModel.getBpmnUrl())) {
                        promise.complete();
                        return;
                    }
                    // 进行刷新
                    Future<Void> containerRefresh = deployBpmns(functionVersionModel);
                    containerRefresh.onSuccess(containerRefreshSuss -> {
                        BpmnStatus bpmnStatus = BpmnStatus.DEPLOY;
                        functionVersionModel.setBpmnStatus(bpmnStatus);
                        updateFunctionVersion(functionVersionModel);
                        promise.complete();
                    }).onFailure(containerRefreshFail -> {
                        promise.fail(containerRefreshFail);
                    });
                }).onFailure(fail -> {
                    log.error("查询数据失败 {}", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

    /**
     * 部署bpmn
     *
     * @param functionVersionModel
     * @return
     */
    private Future<Void> deployBpmns(FunctionVersionModel functionVersionModel) {
        cn.spider.framework.container.sdk.data.RefreshBpmnParam refreshBpmnParam = new cn.spider.framework.container.sdk.data.RefreshBpmnParam();
        refreshBpmnParam.setBpmnUrl(functionVersionModel.getBpmnUrl());
        return containerService.refreshBpmn(JsonObject.mapFrom(refreshBpmnParam));
    }

    /**
     * 卸载
     *
     * @param functionVersionModel
     * @return
     */
    private Future<Void> unload(FunctionVersionModel functionVersionModel) {
        UnloadBpmnParam unloadBpmnParam = new UnloadBpmnParam();
        unloadBpmnParam.setBpmnUrl(functionVersionModel.getBpmnUrl());
        return containerService.unloadBpmn(JsonObject.mapFrom(unloadBpmnParam));
    }


    // 初始化bpmn
    public void deployBpmn() {
        Map<String, Object> parameters = new HashMap<>();
        SqlTemplate
                .forQuery(client, "SELECT * FROM spider_business_function_version")
                .mapTo(ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(functions -> {
                    RowSet<FunctionVersionModel> function = functions;
                    List<FunctionVersionModel> businessFunctionList = Lists.newArrayList();
                    function.forEach(item -> {
                        businessFunctionList.add(item);
                    });

                    for (FunctionVersionModel functionVersionModel : businessFunctionList) {
                        // 进行刷新
                        cn.spider.framework.container.sdk.data.RefreshBpmnParam refreshBpmnParam = new cn.spider.framework.container.sdk.data.RefreshBpmnParam();
                        refreshBpmnParam.setBpmnUrl(functionVersionModel.getBpmnUrl());
                        Future<Void> containerRefresh = containerService.refreshBpmn(JsonObject.mapFrom(refreshBpmnParam));
                        containerRefresh.onSuccess(containerRefreshSuss -> {

                        }).onFailure(containerRefreshFail -> {

                        });
                    }
                }).onFailure(fail -> {
                    log.error("查询版本信息失败 {}", ExceptionMessage.getStackTrace(fail));
                });
    }

    /**
     * 查询bpmn-url
     */
    public Future<Set<String>> getBpmnUrl() {
        Promise<Set<String>> promise = Promise.promise();
        Map<String, Object> parameters = new HashMap<>();
        //parameters.put("status", VersionStatus.START.name());
        parameters.put("bpmnStatus", BpmnStatus.DEPLOY.name());
        SqlTemplate
                .forQuery(client, "SELECT * FROM spider_business_function_version where bpmn_status = #{bpmnStatus}")
                .mapTo(ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(functions -> {
                    RowSet<FunctionVersionModel> function = functions;
                    Set<String> bpmnUrls = new HashSet<>();
                    function.forEach(item -> {
                        if (StringUtils.isEmpty(item.getBpmnUrl())) {
                            return;
                        }
                        bpmnUrls.add(item.getBpmnUrl());
                    });
                    promise.complete(bpmnUrls);
                }).onFailure(fail -> {
                    promise.fail(fail);
                });
        return promise.future();
    }

    public QueryFunctionVersionResult queryVersion(QueryVersionFunctionParam param) {
        Page<SpiderBusinessFunctionVersion> rowPage = new Page(param.getPage(), param.getSize());
        LambdaQueryWrapper<SpiderBusinessFunctionVersion> queryWrapper = new LambdaQueryWrapper<SpiderBusinessFunctionVersion>()
                .eq(StringUtils.isNotEmpty(param.getFunctionId()), SpiderBusinessFunctionVersion::getFunctionId, param.getFunctionId())
                .eq(StringUtils.isNotEmpty(param.getVersionId()), SpiderBusinessFunctionVersion::getId, param.getVersionId())
                .eq(StringUtils.isNotEmpty(param.getFunctionName()), SpiderBusinessFunctionVersion::getFunctionName, param.getFunctionName());
        IPage page = spiderBusinessFunctionVersionService.page(rowPage, queryWrapper);
        return new QueryFunctionVersionResult(page.getRecords(), page.getTotal());
    }

    public void addVersion(SpiderBusinessFunctionVersion spiderBusinessFunctionVersion) {
        if (StringUtils.isEmpty(spiderBusinessFunctionVersion.getId())) {
            spiderBusinessFunctionVersion.setId(UUID.randomUUID().toString());
            spiderBusinessFunctionVersion.setBpmnStatus(BpmnStatus.INIT);
            spiderBusinessFunctionVersionService.save(spiderBusinessFunctionVersion);
            return;
        }
        spiderBusinessFunctionVersionService.updateById(spiderBusinessFunctionVersion);
    }

    public SpiderBusinessFunctionVersion queryAllowRunFunctionVersion(String functionId, String version, Map<String, Object> param) {
        // 查询出来版本信息
        List<SpiderBusinessFunctionVersion> functionVersions = spiderBusinessFunctionVersionService.lambdaQuery()
                .eq(SpiderBusinessFunctionVersion::getFunctionId, functionId)
                .eq(StringUtils.isNotEmpty(version), SpiderBusinessFunctionVersion::getVersion, version)
                .eq(SpiderBusinessFunctionVersion::getStatus, VersionStatus.START)
                .eq(SpiderBusinessFunctionVersion::getBpmnStatus, BpmnStatus.DEPLOY)
                .list();
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(functionVersions), "没有找到对应的功能版本,请检查");
        // 如果指定的版本不为空，直接过滤版本
        if (StringUtils.isNotEmpty(version)) {
            Optional<SpiderBusinessFunctionVersion> businessFunctionVersion = functionVersions
                    .stream()
                    .filter(item -> item.getVersion().equals(version))
                    .findFirst();
            Preconditions.checkArgument(businessFunctionVersion.isPresent(), "没有找到对应的功能版本,请检查");
            return businessFunctionVersion.get();
        }
        // 通过表达式来获取正确的版本
        Optional<SpiderBusinessFunctionVersion> businessFunctionVersion = functionVersions
                .stream()
                .filter(item -> elQueryFunctionVersion(item.getRule(), param))
                .findFirst();
        Preconditions.checkArgument(businessFunctionVersion.isPresent(), "没有找到对应的功能版本,请检查");
        return businessFunctionVersion.get();
    }

    private Boolean elQueryFunctionVersion(String rule, Map<String, Object> param) {
        // 创建SpEL表达式解析器
        ExpressionParser parser = new SpelExpressionParser();

        // 创建评估上下文并注册变量（即JSON对象）
        StandardEvaluationContext context = new StandardEvaluationContext(param);
        return parser.parseExpression(rule).getValue(context, Boolean.class);
    }

    /**
     * @param startNodeJsParam functionId,nodeId,paramJsDemand
     *                         构造为流程中各个节点构造参数
     */
    public void paramBuild(StartNodeJsParam startNodeJsParam) throws ExecutionException, InterruptedException {
        String id = startNodeJsParam.getFunctionVersionId();
        String lockKye = id + BUILD_JS_PARAM;
        if (!lockManager.lock(lockKye)) {
            Preconditions.checkArgument(false, "当前版本在参数构建中，请稍后再试");
        }
        SpiderBusinessFunctionVersion spiderBusinessFunctionVersion = spiderBusinessFunctionVersionService.getById(id);
        if (StringUtils.isEmpty(startNodeJsParam.getNodeId())) {
            // 先移除所有的函数信息
            spiderBusinessFunctionVersionService.lambdaUpdate().set(SpiderBusinessFunctionVersion::getNodeParamInfo, null).eq(SpiderBusinessFunctionVersion::getId, id).update();
        }
        if (Objects.isNull(spiderBusinessFunctionVersion.getNodeInfos()) || CollectionUtils.isEmpty(spiderBusinessFunctionVersion.getNodeInfos().getNodeInfos())) {
            Preconditions.checkArgument(false, "节点信息为空");
        }
        NodeInfos nodeInfos = spiderBusinessFunctionVersion.getNodeInfos();
        List<NodeInfo> nodeInfoList = nodeInfos.getNodeInfos();
        // 把nodeInfoList转成map按照functionType
        Map<FunctionNodeType, List<NodeInfo>> nodeInfoMap = nodeInfoList.stream().collect(Collectors.groupingBy(NodeInfo::getFunctionType));
        List<AiNodeInfo> aiNodeInfoList = new ArrayList<>();
        if (nodeInfoMap.containsKey(FunctionNodeType.DOMAIN_FUNCTION)) {
            List<NodeInfo> domainFunctionNodeInfoList = nodeInfoMap.get(FunctionNodeType.DOMAIN_FUNCTION);
            // 获取domainFunctionNodeInfoList中的functionVersionId
            Set<String> domainFunctionNodeInfoListVersionId = domainFunctionNodeInfoList.stream().map(NodeInfo::getFunctionVersionId).collect(Collectors.toSet());
            QueryInputParam queryInputParam = new QueryInputParam(domainFunctionNodeInfoListVersionId);
            Future<JsonArray> inputParamFunctionVersion = hostPluginInterface.queryInputParam(JsonObject.mapFrom(queryInputParam));
            JsonArray inputParams = inputParamFunctionVersion.toCompletionStage().toCompletableFuture().get();
            List<QueryDomainFunctionClassInfo> functionInfo = JSON.parseArray(inputParams.toString(), QueryDomainFunctionClassInfo.class);
            //functionInfo 基于 domainFunctionId转成map
            Map<String, QueryDomainFunctionClassInfo> functionInfoMap = functionInfo.stream().collect(Collectors.toMap(QueryDomainFunctionClassInfo::getDomainFunctionId, Function.identity()));
            // areaFunctionVersions按照id进行转map
            for (NodeInfo nodeInfo : domainFunctionNodeInfoList) {
                QueryDomainFunctionClassInfo functionClassInfo = functionInfoMap.get(nodeInfo.getFunctionVersionId());
                AiNodeInfo aiNodeInfo = new AiNodeInfo(functionClassInfo.getParametersClass(),
                        nodeInfo.getId(), nodeInfo.getName(),
                        nodeInfo.getFunctionVersionId(), functionClassInfo.getReturnsClass(),
                        functionClassInfo.getOtherClass());
                aiNodeInfoList.add(aiNodeInfo);
            }
        }
        if (nodeInfoMap.containsKey(FunctionNodeType.BUSINESS_FUNCTION)) {
            List<NodeInfo> domainFunctionNodeInfoList = nodeInfoMap.get(FunctionNodeType.BUSINESS_FUNCTION);
            // 把domainFunctionNodeInfoList中的functionVersionId 给到一个set当中
            Set<String> domainFunctionNodeInfoListVersionIds = domainFunctionNodeInfoList
                    .stream()
                    .map(NodeInfo::getFunctionVersionId)
                    .collect(Collectors.toSet());
            List<SpiderBusinessFunctionVersion> spiderBusinessFunctionVersions = spiderBusinessFunctionVersionService
                    .lambdaQuery()
                    .in(SpiderBusinessFunctionVersion::getId, domainFunctionNodeInfoListVersionIds)
                    .list();
            // 把spiderBusinessFunctionVersions中数据基于id转成map
            Map<String, SpiderBusinessFunctionVersion> spiderBusinessFunctionVersionMap = spiderBusinessFunctionVersions
                    .stream()
                    .collect(Collectors.toMap(SpiderBusinessFunctionVersion::getId, Function.identity()));
            for (NodeInfo nodeInfo : domainFunctionNodeInfoList) {
                if (!spiderBusinessFunctionVersionMap.containsKey(nodeInfo.getFunctionVersionId())) {
                    continue;
                }
                SpiderBusinessFunctionVersion businessFunctionVersion = spiderBusinessFunctionVersionMap.get(nodeInfo.getFunctionVersionId());
                String requestClass = JSON.toJSONString(businessFunctionVersion.getInputParamJavaClass());
                String resultClass = JSON.toJSONString(businessFunctionVersion.getOutputParamJavaClass());
                AiNodeInfo aiNodeInfo = new AiNodeInfo(requestClass,
                        nodeInfo.getId(), nodeInfo.getName(),
                        nodeInfo.getFunctionVersionId(), resultClass,
                        null);
                aiNodeInfoList.add(aiNodeInfo);
            }
        }
        SpiderDataFlow spiderDataFlow = spiderDataFlowService.getById(spiderBusinessFunctionVersion.getDataFlowId());
        //DataFlowAnalysisModel dataFlowAnalysisModel = spiderDataFlow.getDataFlowAnalysisModel();
        List<Integer> domainBaseIdList = JSON.parseArray(spiderDataFlow.getSonAreaIds(), Integer.class);
        Set<Integer> domainBaseIds = Sets.newHashSet(domainBaseIdList);
        List<JsonObject> domainInfos = taskManager.buildDomainInfo(domainBaseIds);
        String bpmnString = spiderBusinessFunctionVersion.getBpmnXml();
        JSONObject flowData = spiderDataFlow.getData();
        // 获取到领域对象信息
        //spiderDataFlow.getFlowDataDesc()
        ParamBuildInfo paramBuildInfo = new ParamBuildInfo(aiNodeInfoList,
                bpmnString,
                flowData,
                id,
                "",
                spiderBusinessFunctionVersion.getInputParamJavaClass(),
                spiderBusinessFunctionVersion.getOutputParamJavaClass(), domainInfos, startNodeJsParam.getNodeId(), startNodeJsParam.getParamJsDemand());
        log.info("paramBuildInfo {}", JSON.toJSONString(paramBuildInfo));
        agentVertxClient.buildNodeParam(JsonObject.mapFrom(paramBuildInfo));
    }

    /**
     * 根据业务功能/http功能生成java实体
     *
     * @param toJavaParam
     */
    public void configToJavaEntity(ToJavaParam toJavaParam) {
        switch (toJavaParam.getSource()) {
            case BUSINESS_FUNCTION:
                businessToClass(toJavaParam);
                break;
            case HTTP_FUNCTION:
                httpParamToClass(toJavaParam);
                break;
        }
    }

    private void businessToClass(ToJavaParam toJavaParam) {
        Map<String, List<FunctionParamConfigModel>> runObjectConfig = queryBusinessFunctionVersionRunObjectConfig(toJavaParam.getFunctionVersionId());
        GenerateJavaCodeParam generateJavaCodeParam = new GenerateJavaCodeParam(toJavaParam.getFunctionVersionId(), runObjectConfig, GenerateCoderType.INPUT, toJavaParam.getSource(), null);
        agentVertxClient.jsonToJavaEntity(JsonObject.mapFrom(generateJavaCodeParam));
        Map<String, List<FunctionParamConfigModel>> resultObjectConfig = queryBusinessFunctionVersionResultObjectConfig(toJavaParam.getFunctionVersionId());
        GenerateJavaCodeParam generateJavaCodeParam2 = new GenerateJavaCodeParam(toJavaParam.getFunctionVersionId(), resultObjectConfig, GenerateCoderType.OUTPUT, toJavaParam.getSource(), null);
        agentVertxClient.jsonToJavaEntity(JsonObject.mapFrom(generateJavaCodeParam2));

    }

    private void httpParamToClass(ToJavaParam toJavaParam) {
        Map<String, List<FunctionParamConfigModel>> runObjectConfig = queryHttpFunctionRunObjectConfig(toJavaParam.getHttpFunctionId());
        GenerateJavaCodeParam generateJavaCodeParam = new GenerateJavaCodeParam(null, runObjectConfig, GenerateCoderType.INPUT, toJavaParam.getSource(), toJavaParam.getHttpFunctionId());
        agentVertxClient.jsonToJavaEntity(JsonObject.mapFrom(generateJavaCodeParam));
        Map<String, List<FunctionParamConfigModel>> resultObjectConfig = queryHttpFunctionResultObjectConfig(toJavaParam.getHttpFunctionId());
        GenerateJavaCodeParam generateJavaCodeParam2 = new GenerateJavaCodeParam(null, resultObjectConfig, GenerateCoderType.OUTPUT, toJavaParam.getSource(), toJavaParam.getHttpFunctionId());
        agentVertxClient.jsonToJavaEntity(JsonObject.mapFrom(generateJavaCodeParam2));

    }


    private Map<String, List<FunctionParamConfigModel>> queryBusinessFunctionVersionRunObjectConfig(String functionVersionId) {
        SpiderBusinessFunctionVersion spiderBusinessFunctionVersion = spiderBusinessFunctionVersionService.getById(functionVersionId);
        return spiderBusinessFunctionVersion.getRunObjectConfig();
    }

    private Map<String, List<FunctionParamConfigModel>> queryBusinessFunctionVersionResultObjectConfig(String functionVersionId) {
        SpiderBusinessFunctionVersion spiderBusinessFunctionVersion = spiderBusinessFunctionVersionService.getById(functionVersionId);
        return spiderBusinessFunctionVersion.getResultObjectConfig();
    }

    private Map<String, List<FunctionParamConfigModel>> queryHttpFunctionResultObjectConfig(Integer httpFunctionId) {
        SpiderToolHttp spiderToolHttp = spiderToolHttpService.getById(httpFunctionId);
        return spiderToolHttp.getHttpFunctionResultObject();
    }

    private Map<String, List<FunctionParamConfigModel>> queryHttpFunctionRunObjectConfig(Integer httpFunctionId) {
        SpiderToolHttp spiderToolHttp = spiderToolHttpService.getById(httpFunctionId);
        return spiderToolHttp.getHttpFunctionParamObject();
    }


    public void writeJavaEntity(JsonObject param) {
        GenerateJavaCodeResult generateJavaCodeResult = param.mapTo(GenerateJavaCodeResult.class);
        switch (generateJavaCodeResult.getSource()) {
            case BUSINESS_FUNCTION:
                writeJavaEntityBusiness(generateJavaCodeResult);
                break;
            case HTTP_FUNCTION:
                writeJavaEntityHttp(generateJavaCodeResult);
                break;

        }

    }

    private void writeJavaEntityBusiness(GenerateJavaCodeResult generateJavaCodeResult) {
        spiderBusinessFunctionVersionService.lambdaUpdate()
                .set(generateJavaCodeResult.getGenerateCoderType().equals(GenerateCoderType.INPUT), SpiderBusinessFunctionVersion::getInputParamJavaClass, JSON.toJSONString(generateJavaCodeResult.getCodes()))
                .set(generateJavaCodeResult.getGenerateCoderType().equals(GenerateCoderType.OUTPUT), SpiderBusinessFunctionVersion::getOutputParamJavaClass, JSON.toJSONString(generateJavaCodeResult.getCodes()))
                .eq(SpiderBusinessFunctionVersion::getId, generateJavaCodeResult.getFunctionVersionId())
                .update();
    }

    private void writeJavaEntityHttp(GenerateJavaCodeResult generateJavaCodeResult) {
        spiderToolHttpService.lambdaUpdate()
                .set(generateJavaCodeResult.getGenerateCoderType().equals(GenerateCoderType.INPUT), SpiderToolHttp::getHttpFunctionParamClass, JSON.toJSONString(generateJavaCodeResult.getCodes()))
                .set(generateJavaCodeResult.getGenerateCoderType().equals(GenerateCoderType.OUTPUT), SpiderToolHttp::getHttpFunctionResultClass, JSON.toJSONString(generateJavaCodeResult.getCodes()))
                .eq(SpiderToolHttp::getId, generateJavaCodeResult.getHttpFunctionId()).update();
    }

    public void writeJsFunctionInfo(NodeJsFunctionInfo nodeJsFunctionInfo) {
        SpiderBusinessFunctionVersion spiderBusinessFunctionVersion = spiderBusinessFunctionVersionService.getById(nodeJsFunctionInfo.getFunctionVersionId());
        List<NodeInfo> nodeInfos = spiderBusinessFunctionVersion.getNodeInfos().getNodeInfos();
        if (Objects.nonNull(spiderBusinessFunctionVersion.getNodeParamInfo()) && CollectionUtils.isNotEmpty(spiderBusinessFunctionVersion.getNodeParamInfo().getNodeParamConfigList())) {
            List<NodeParamConfig> nodeParamConfigListOld = spiderBusinessFunctionVersion.getNodeParamInfo().getNodeParamConfigList();
            // nodeParamConfigListOld 基于nodeId 转成map
            Map<String, NodeParamConfig> nodeParamConfigMap = nodeParamConfigListOld.stream().collect(Collectors.toMap(NodeParamConfig::getNodeId, Function.identity()));
            List<NodeParamConfig> nodeParamConfigListNew = nodeJsFunctionInfo.getNodeParamConfigList();
            nodeParamConfigListNew.forEach(nodeParamConfig -> {
                // 直接替换
                nodeParamConfigMap.put(nodeParamConfig.getNodeId(), nodeParamConfig);
            });
            nodeJsFunctionInfo.setNodeParamConfigList(new ArrayList<>(nodeParamConfigMap.values()));
        }

        // nodeInfos 基于id 转成map
        Map<String, NodeInfo> nodeInfoMap = nodeInfos.stream().collect(Collectors.toMap(NodeInfo::getId, Function.identity()));
        nodeJsFunctionInfo.getNodeParamConfigList().forEach(nodeParamConfig -> {
            if (!nodeInfoMap.containsKey(nodeParamConfig.getNodeId())) {
                return;
            }
            NodeInfo nodeInfo = nodeInfoMap.get(nodeParamConfig.getNodeId());
            nodeParamConfig.setNodeName(nodeInfo.getName());
        });
        spiderBusinessFunctionVersionService.lambdaUpdate().set(SpiderBusinessFunctionVersion::getNodeParamInfo, JSON.toJSONString(nodeJsFunctionInfo))
                .eq(SpiderBusinessFunctionVersion::getId, nodeJsFunctionInfo.getFunctionVersionId()).
                update();
    }


}
