package cn.spider.framework.domain.area.impl;

import cn.spider.framework.domain.area.agent.AgentVertxClient;
import cn.spider.framework.domain.area.sondomain.entity.*;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.domain.area.flowdata.entity.SpiderDataFlow;
import cn.spider.framework.domain.area.flowdata.service.ISpiderDataFlowService;
import cn.spider.framework.domain.area.node.NodeManger;
import cn.spider.framework.domain.area.node.data.*;
import cn.spider.framework.domain.area.node.data.enums.NodeStatus;
import cn.spider.framework.domain.area.node.entity.SpiderAreaFunction;
import cn.spider.framework.domain.area.node.entity.SpiderAreaFunctionVersion;
import cn.spider.framework.domain.area.node.service.ISpiderAreaFunctionVersionService;
import cn.spider.framework.domain.area.plugin.ApplicationPluginManager;
import cn.spider.framework.domain.area.sondomain.service.IAreaDomainBaseInfoService;
import cn.spider.framework.domain.sdk.data.*;
import cn.spider.framework.domain.sdk.interfaces.NodeInterface;
import cn.spider.framework.param.result.build.model.ReportParamInfo;
import cn.spider.node.framework.code.agent.sdk.data.CreateProjectResult;
import cn.spider.node.host.plugin.center.sdk.interfaces.HostPluginInterface;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.impl
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-27  12:52
 * @Description: 节点 - 实现类
 * @Version: 1.0
 */
@Slf4j
public class NodeInterfaceImpl implements NodeInterface {

    private NodeManger nodeManger;

    private ApplicationPluginManager pluginManager;

    private HostPluginInterface hostPluginInterface;

    private Executor spiderBusinessPool;

    private ISpiderAreaFunctionVersionService spiderAreaFunctionVersionService;

    private ISpiderDataFlowService spiderDataFlowService;

    private IAreaDomainBaseInfoService areaDomainBaseInfoService;

    private AgentVertxClient agentVertxClient;

    public NodeInterfaceImpl(NodeManger nodeManger,
                             ApplicationPluginManager pluginManager,
                             HostPluginInterface hostPluginInterface,
                             Executor spiderBusinessPool,
                             ISpiderAreaFunctionVersionService spiderAreaFunctionVersionService,
                             ISpiderDataFlowService spiderDataFlowService,
                             IAreaDomainBaseInfoService areaDomainBaseInfoService,
                             AgentVertxClient agentVertxClient) {
        this.nodeManger = nodeManger;
        this.pluginManager = pluginManager;
        this.hostPluginInterface = hostPluginInterface;
        this.spiderBusinessPool = spiderBusinessPool;
        this.spiderAreaFunctionVersionService = spiderAreaFunctionVersionService;
        this.spiderDataFlowService = spiderDataFlowService;
        this.areaDomainBaseInfoService = areaDomainBaseInfoService;
        this.agentVertxClient = agentVertxClient;
    }

    @Override
    public Future<Void> insertNode(JsonObject data) {
        CreateNodeModel model = JSON.parseObject(data.toString(), CreateNodeModel.class);
        // 进行拆分出参与入参
        Node node = JSON.parseObject(data.toString(), Node.class);
        if (Objects.nonNull(model.getMethodParam())) {
            if (model.getMethodParam().containsKey("param")) {
                JsonObject param = new JsonObject(model.getMethodParam().getJSONObject("param").toString());
                node.setParamMapping(param);
            }
            if (model.getMethodParam().containsKey("result")) {
                JsonObject resultFinal = new JsonObject(model.getMethodParam().getJSONObject("result").toString());
                node.setResultMapping(resultFinal);
            }
        }

        return nodeManger.createNode(node);
    }

    @Override
    public Future<Void> updateNode(JsonObject data) {
        Node node = JSON.parseObject(data.toString(), Node.class);
        JSONObject methodParam = JSONObject.parseObject(data.getJsonObject("methodParam").toString());

        if (methodParam.containsKey("param")) {
            node.setParamMapping(new JsonObject(methodParam.getJSONObject("param").toString()));
        }
        if (methodParam.containsKey("result")) {
            node.setResultMapping(new JsonObject(methodParam.getJSONObject("result").toString()));
        }
        return nodeManger.updateNode(node);
    }

    @Override
    public Future<Void> distributeNode(JsonObject data) {

        return null;
    }

    @Override
    public Future<Void> updateParam(JsonObject data) {

        return null;
    }

    @Override
    public Future<JsonObject> queryJsonObject(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        nodeManger.queryNode(data.mapTo(QueryNodeParam.class)).onSuccess(suss -> {
            List<Node> nodes = suss;
            JsonArray array = new JsonArray();
            for (Node node : nodes) {
                JsonObject object = JsonObject.mapFrom(node);
                if (Objects.nonNull(node.getParamMapping())) {
                    NodeParamConfigModel nodeParamConfigList = JSON.parseObject(node.getParamMapping().toString(), NodeParamConfigModel.class);
                    object.put("paramMapping", JsonObject.mapFrom(nodeParamConfigList));
                }
                if (Objects.nonNull(node.getResultMapping())) {
                    NodeParamConfigModel nodeParamConfigList = JSON.parseObject(node.getResultMapping().toString(), NodeParamConfigModel.class);
                    object.put("resultMapping", JsonObject.mapFrom(nodeParamConfigList));
                }
                array.add(object);
            }
            promise.complete(new JsonObject().put("nodes", array));
        }).onFailure(fail -> {
            promise.fail(fail);
        });
        return promise.future();
    }

    /**
     * 根据 - 组件名称和服务名称查询节点信息
     *
     * @param data
     * @return
     */
    @Override
    public Future<JsonObject> queryBaseNodes(JsonObject data) {
        QueryBaseNodeParam param = data.mapTo(QueryBaseNodeParam.class);
        FunctionInfo spiderAreaFunctionVersion = nodeManger.queryNodeVersion(param.getTaskComponent(), param.getTaskService(), param.getVersion());
        return Future.succeededFuture(JsonObject.mapFrom(spiderAreaFunctionVersion));
    }


    /**
     * 刷新参数
     *
     * @param param
     * @return
     */
    @Override
    public Future<Void> refreshParam(JsonObject param) {
        try {
            ReportParamInfo areaParam = param.mapTo(ReportParamInfo.class);
            nodeManger.refreshNodeParam(areaParam);
            return Future.succeededFuture();
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
    }

    @Override
    public Future<JsonObject> queryParamConfig(JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();
        QueryBaseNodeParam params = param.mapTo(QueryBaseNodeParam.class);
        nodeManger.queryNodeByComTaskService(params.getTaskComponent(), params.getTaskService())
                .onSuccess(suss -> {
                    Node node = suss;
                    NodeParamConfigResult result = new NodeParamConfigResult();
                    if (Objects.nonNull(node.getParamMapping())) {
                        JsonObject paramConfig = node.getParamMapping();
                        result.setNodeParamConfigs(paramConfig.getMap().values());
                    }
                    if (Objects.nonNull(node.getResultMapping())) {
                        JsonObject resultConfig = node.getParamMapping();
                        result.setNodeResultConfigs(resultConfig.getMap().values());
                    }
                    promise.complete(JsonObject.mapFrom(result));
                }).onFailure(fail -> {
                    promise.fail(fail);
                });
        return promise.future();
    }

    @Override
    public Future<JsonObject> areaNodeBase() {
        return pluginManager.queryAllAreaInfo();
    }

    /**
     * 构建并且-发起部署申请
     *
     * @param param 构建应用插件的参数信息
     */
    @Override
    public Future<JsonObject> deployCode(JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();
        String domainFunctionVersionId = param.getString("domainFunctionVersionId");
        SpiderAreaFunctionVersion spiderAreaFunctionVersion = spiderAreaFunctionVersionService.getById(domainFunctionVersionId);
        param.put("version", spiderAreaFunctionVersion.getVersion());
        pluginManager.buildPlugin(param).onSuccess(buildSuss -> {
            CreateProjectResult projectResult = buildSuss.mapTo(CreateProjectResult.class);
            if (StringUtils.isNotEmpty(projectResult.getErrorStackTrace())) {
                log.info("部署失败 {}", param.toString());
                promise.fail(projectResult.getErrorStackTrace());
                return;
            }
            promise.complete();

            spiderBusinessPool.execute(() -> {
                // 修改版本新增状态为编译完成
                spiderAreaFunctionVersionService.lambdaUpdate()
                        .set(SpiderAreaFunctionVersion::getStatus, NodeStatus.COMPILE)
                        .eq(SpiderAreaFunctionVersion::getId, domainFunctionVersionId)
                        .update();
                // 构造基础信息成功- 开始发起部署
                hostPluginInterface.pluginOnline(new JsonObject().put("functionId", projectResult.getId())).onFailure(fail -> {
                    log.warn("发起部署失败 {}", ExceptionMessage.getStackTrace(fail));
                });
            });

        }).onFailure(buildFail -> {
            log.info("构建失败 {}", ExceptionMessage.getStackTrace(buildFail));
            promise.fail(buildFail);
        });
        return promise.future();
    }

    @Override
    public Future<Void> initAreaBase(JsonObject param) {
        return pluginManager.initAreaBase(param);
    }

    @Override
    public Future<Void> initRag() {
        return pluginManager.initAiRag();
    }

    @Override
    public Future<JsonObject> querySonAreaBaseInfo(JsonObject param) {
        return pluginManager.querySonBaseInfo(param);
    }

    @Override
    public Future<JsonObject> queryDomainFunction(JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            try {
                QueryDomainFunctionParam queryDomainFunctionParam = param.mapTo(QueryDomainFunctionParam.class);
                QueryDomainFunctionResult result = nodeManger.queryDomainFunction(queryDomainFunctionParam);
                promise.complete(JsonObject.mapFrom(result));
            } catch (Exception e) {
                promise.fail(e);
                log.error("queryDomainFunction error", e);
            }
        });
        return promise.future();
    }

    @Override
    public Future<Void> updateDomainFunction(JsonObject param) {
        Promise<Void> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            try {
                SpiderAreaFunction spiderAreaFunction = param.mapTo(SpiderAreaFunction.class);
                nodeManger.upsertDomainFunction(spiderAreaFunction);
                promise.complete();
            } catch (Exception e) {
                promise.fail(e);
                log.info(ExceptionMessage.getStackTrace(e));
            }
        });

        return promise.future();
    }

    @Override
    public Future<JsonObject> queryDomainFunctionVersion(JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            try {
                QueryDomainFunctionVersionResult result = nodeManger.queryDomainFunctionVersion(param.mapTo(QueryDomainFunctionVersionParam.class));
                promise.complete(JsonObject.mapFrom(result));
            } catch (Exception e) {
                promise.fail(e);
                log.error("queryDomainFunctionVersion error", e);
            }
        });
        return promise.future();
    }

    @Override
    public Future<Void> writeTableAnalysisInfo(JsonObject param) {
        Promise<Void> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
           /* try {
                AnalysisTableInfo analysisTableInfo = param.mapTo(AnalysisTableInfo.class);
                TableAnalysisModel tableAnalysisInfo = new TableAnalysisModel();
                tableAnalysisInfo.setTableInfo(analysisTableInfo.getTableInfo());
                String tableAnalysisInfoJson = JSON.toJSONString(tableAnalysisInfo);
                spiderAreaFunctionVersionService.lambdaUpdate().set(SpiderAreaFunctionVersion::getTableAnalysisInfo, tableAnalysisInfoJson)
                        .eq(SpiderAreaFunctionVersion::getId, analysisTableInfo.getDomainFunctionVersionId()).update();
                promise.future();
            } catch (Exception e) {
                promise.fail(e);
                throw new RuntimeException(e);
            }*/
        });
        return promise.future();
    }

    @Override
    public Future<Void> analysisParam(JsonObject param) {
        Promise<Void> promise = Promise.promise();
        String functionVersionId = param.getString("functionVersionId");
        SpiderAreaFunctionVersion functionVersion = spiderAreaFunctionVersionService.getById(functionVersionId);
        Integer dataFlowId = functionVersion.getDataFlowId();
        SpiderDataFlow flow = spiderDataFlowService.getById(dataFlowId);
        List<Integer> baseIds = JSONObject.parseArray(flow.getSonAreaIds(), Integer.class);
        List<AreaDomainBaseInfo> areaDomainBaseInfos = areaDomainBaseInfoService.lambdaQuery().in(AreaDomainBaseInfo::getId, baseIds).list();
        Map<String, Object> analysisParamDataInfo = new HashMap<>();

        // 使用areaDomainBaseInfos进行转map,key为 DomainObjectEntityName + ":" + version ,value为 DomainObject
        Map<String, String> areaTableFlieds = areaDomainBaseInfos.stream().collect(Collectors.toMap(item -> item.getDomainObjectEntityName() + ":" + item.getVersion(), item -> item.getDomainObject()));
        analysisParamDataInfo.put("tableInfo", areaTableFlieds);
        JsonObject queryParam = new JsonObject();
        queryParam.put("domainFunctionVersionId", functionVersionId);
        hostPluginInterface.queryFunctionVersion(queryParam)
                .onSuccess(suss -> {
                    JsonObject functionVersionInfo = suss;
                    analysisParamDataInfo.put("coder", functionVersionInfo.getString("areaFunctionResultClass"));
                    analysisParamDataInfo.put("functionVersionId", functionVersionId);
                    agentVertxClient.analysisParam(JsonObject.mapFrom(analysisParamDataInfo));
                    promise.complete();
                })
                .onFailure(fail -> {
                    promise.fail(fail);
                });
        return promise.future();
    }

    @Override
    public Future<Void> notifyAiAnalysis(JsonObject param) {
        Promise<Void> promise = Promise.promise();
        NotifyAnalysisResultParam notifyAnalysisResultParam = JSON.parseObject(param.toString(), NotifyAnalysisResultParam.class);
        spiderBusinessPool.execute(() -> {
            // 解析 notifyAnalysisResultParam.getAnalysisResult();
            NotifyAnalysisResultInfo notifyAnalysisResultInfo = new NotifyAnalysisResultInfo(notifyAnalysisResultParam.getAnalysisResult());
            spiderAreaFunctionVersionService.lambdaUpdate()
                    .set(SpiderAreaFunctionVersion::getResultAnalysis, JSON.toJSONString(notifyAnalysisResultInfo))
                    .eq(SpiderAreaFunctionVersion::getId, notifyAnalysisResultParam.getFunctionVersionId()).update();
            promise.complete();
        });
        return promise.future();
    }

    @Override
    public Future<JsonObject> queryAnalysisParam(JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();
        QueryAnalysisResultParam queryAnalysisResult = param.mapTo(QueryAnalysisResultParam.class);
        spiderBusinessPool.execute(() -> {
            SpiderAreaFunctionVersion spiderAreaFunctionVersion = spiderAreaFunctionVersionService.getById(queryAnalysisResult.getFunctionVersionId());
            JsonObject resultAnalysis = Objects.nonNull(spiderAreaFunctionVersion.getResultAnalysis()) ?
                    new JsonObject(JSONObject.toJSONString(spiderAreaFunctionVersion.getResultAnalysis())) :
                    new JsonObject();
            QueryAnalysisResult queryAnalysisResultParam = new QueryAnalysisResult(resultAnalysis);
            promise.complete(JsonObject.mapFrom(queryAnalysisResultParam));
        });
        return promise.future();
    }
}
