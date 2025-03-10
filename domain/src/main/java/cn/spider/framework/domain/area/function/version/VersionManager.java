package cn.spider.framework.domain.area.function.version;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.container.sdk.data.UnloadBpmnParam;
import cn.spider.framework.container.sdk.interfaces.ContainerService;
import cn.spider.framework.domain.area.agent.AgentVertxClient;
import cn.spider.framework.domain.area.data.AiNodeInfo;
import cn.spider.framework.domain.area.data.NodeInfo;
import cn.spider.framework.domain.area.data.NodeInfos;
import cn.spider.framework.domain.area.data.enums.BpmnStatus;
import cn.spider.framework.domain.area.data.enums.FunctionNodeType;
import cn.spider.framework.domain.area.flowdata.entity.SpiderDataFlow;
import cn.spider.framework.domain.area.flowdata.service.ISpiderDataFlowService;
import cn.spider.framework.domain.area.function.data.FunctionParamConfigModel;
import cn.spider.framework.domain.area.function.data.GenerateJavaCodeParam;
import cn.spider.framework.domain.area.function.data.GenerateJavaCodeResult;
import cn.spider.framework.domain.area.function.entity.SpiderBusinessFunctionVersion;
import cn.spider.framework.domain.area.function.enums.GenerateCoderType;
import cn.spider.framework.domain.area.function.service.ISpiderBusinessFunctionVersionService;
import cn.spider.framework.domain.area.function.version.data.FunctionVersionModel;
import cn.spider.framework.domain.area.function.version.data.QueryFunctionVersionResult;
import cn.spider.framework.domain.area.function.version.data.QueryVersionFunctionParam;
import cn.spider.framework.domain.area.function.version.data.VersionStopStartParam;
import cn.spider.framework.domain.area.function.version.data.enums.VersionStatus;
import cn.spider.framework.domain.area.node.NodeManger;
import cn.spider.framework.domain.area.node.entity.SpiderAreaFunctionVersion;
import cn.spider.framework.domain.sdk.data.NotifyAnalysisResultModel;
import cn.spider.framework.domain.sdk.data.RefreshBpmnParam;
import cn.spider.framework.domain.sdk.data.enums.UploadBpmnStatus;
import cn.spider.node.host.plugin.center.sdk.data.QueryInputParam;
import cn.spider.node.host.plugin.center.sdk.interfaces.HostPluginInterface;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.vertx.core.Future;
import io.vertx.core.Promise;
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

    private NodeManger nodeManger;

    private AgentVertxClient agentVertxClient;

    private ISpiderBusinessFunctionVersionService spiderBusinessFunctionVersionService;

    public VersionManager(MySQLPool client, ContainerService containerService, ISpiderBusinessFunctionVersionService spiderBusinessFunctionVersionService,NodeManger nodeManger,AgentVertxClient agentVertxClient) {
        this.client = client;
        this.containerService = containerService;
        this.spiderBusinessFunctionVersionService = spiderBusinessFunctionVersionService;
        this.nodeManger = nodeManger;
        this.agentVertxClient = agentVertxClient;
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

        if (StringUtils.isNotEmpty(param.getStatus())) {
            sql.append(" and `status` = #{status} ");
        }

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
        parameters.put("status", VersionStatus.START.name());
        parameters.put("bpmnStatus", BpmnStatus.DEPLOY.name());
        SqlTemplate
                .forQuery(client, "SELECT * FROM spider_business_function_version where status = #{status} and bpmn_status = #{bpmnStatus}")
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

    private HostPluginInterface hostPluginInterface;

    private ISpiderDataFlowService spiderDataFlowService;

    /**
     * @param id 功能版本的id
     *           构造为流程中各个节点构造参数
     */
    public void paramBuild(String id) throws ExecutionException, InterruptedException {
        SpiderBusinessFunctionVersion spiderBusinessFunctionVersion = spiderBusinessFunctionVersionService.getById(id);
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
            Future<JsonObject> inputParamFunctionVersion = hostPluginInterface.queryInputParam(JsonObject.mapFrom(queryInputParam));
            JsonObject inputParams = inputParamFunctionVersion.toCompletionStage().toCompletableFuture().get();
            List<SpiderAreaFunctionVersion> areaFunctionVersions = nodeManger.queryDomainFunctionVersionByIds(domainFunctionNodeInfoListVersionId);
            // areaFunctionVersions按照id进行转map
            Map<String, SpiderAreaFunctionVersion> areaFunctionVersionMap = areaFunctionVersions
                    .stream()
                    .collect(Collectors.toMap(SpiderAreaFunctionVersion::getId, Function.identity()));
            for (NodeInfo nodeInfo : domainFunctionNodeInfoList) {
                SpiderAreaFunctionVersion functionVersion = areaFunctionVersionMap.get(nodeInfo.getFunctionVersionId());
                JSONObject resultAnalysis = functionVersion.getResultAnalysis();
                List<NotifyAnalysisResultModel> analysisResults = resultAnalysis.getJSONArray("analysisResult").toJavaList(NotifyAnalysisResultModel.class);
                Map<String, List<Map<String, String>>> tableFiledMap = new HashMap<>();
                for (NotifyAnalysisResultModel analysisResult : analysisResults) {
                    if (tableFiledMap.containsKey(analysisResult.getTable())) {
                        tableFiledMap.get(analysisResult.getTable()).addAll(analysisResult.getFields());
                        continue;
                    }
                    tableFiledMap.put(analysisResult.getTable(), analysisResult.getFields());
                }
                AiNodeInfo aiNodeInfo = new AiNodeInfo(tableFiledMap, inputParams.getString(nodeInfo.getFunctionVersionId()), nodeInfo.getId(), nodeInfo.getName(), nodeInfo.getFunctionVersionId());
                aiNodeInfoList.add(aiNodeInfo);
            }
        }
        SpiderDataFlow spiderDataFlow = spiderDataFlowService.getById(spiderBusinessFunctionVersion.getDataFlowId());
        String bpmnString = spiderBusinessFunctionVersion.getBpmnXml();
    }

    public void configToJavaEntity(String functionVersionId) {
        SpiderBusinessFunctionVersion spiderBusinessFunctionVersion = spiderBusinessFunctionVersionService.getById(functionVersionId);
        Map<String, List<FunctionParamConfigModel>> runObjectConfig = spiderBusinessFunctionVersion.getRunObjectConfig();
        GenerateJavaCodeParam generateJavaCodeParam = new GenerateJavaCodeParam(functionVersionId, runObjectConfig, GenerateCoderType.INPUT);
        agentVertxClient.jsonToJavaEntity(JsonObject.mapFrom(generateJavaCodeParam));
        Map<String, List<FunctionParamConfigModel>> resultObjectConfig = spiderBusinessFunctionVersion.getResultObjectConfig();
        GenerateJavaCodeParam generateJavaCodeParam2 = new GenerateJavaCodeParam(functionVersionId, resultObjectConfig, GenerateCoderType.OUTPUT);
        agentVertxClient.jsonToJavaEntity(JsonObject.mapFrom(generateJavaCodeParam2));
    }

    public void writeJavaEntity(JsonObject param) {
        GenerateJavaCodeResult generateJavaCodeResult = param.mapTo(GenerateJavaCodeResult.class);
        spiderBusinessFunctionVersionService.lambdaUpdate()
                .set(generateJavaCodeResult.getGenerateCoderType().equals(GenerateCoderType.INPUT), SpiderBusinessFunctionVersion::getInputParamJavaClass, JSON.toJSONString(generateJavaCodeResult.getCodes()))
                .set(generateJavaCodeResult.getGenerateCoderType().equals(GenerateCoderType.OUTPUT), SpiderBusinessFunctionVersion::getOutputParamJavaClass, JSON.toJSONString(generateJavaCodeResult.getCodes()))
                .eq(SpiderBusinessFunctionVersion::getId, generateJavaCodeResult.getFunctionVersionId())
                .update();
    }


}
