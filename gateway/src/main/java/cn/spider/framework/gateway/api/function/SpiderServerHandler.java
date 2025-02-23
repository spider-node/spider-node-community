package cn.spider.framework.gateway.api.function;

import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.container.sdk.interfaces.BusinessService;
import cn.spider.framework.container.sdk.interfaces.ContainerService;
import cn.spider.framework.container.sdk.interfaces.FlowService;
import cn.spider.framework.controller.sdk.interfaces.BrokerInfoService;
import cn.spider.framework.domain.sdk.interfaces.*;
import cn.spider.framework.gateway.common.ResponseData;
import cn.spider.framework.log.sdk.interfaces.LogInterface;
import cn.spider.framework.param.result.build.enventData.EscalationData;
import cn.spider.node.host.plugin.center.sdk.interfaces.HostPluginInterface;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.gateway.api.function
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-22  13:33
 * @Description: spider-跟ui交互接口
 * @Version: 1.0
 */
@Slf4j
public class SpiderServerHandler {

    private Router router;

    private ContainerService containerService;

    private FlowService flowService;

    private BusinessService businessService;

    private LogInterface logInterface;

    private BrokerInfoService brokerInfoService;

    private String clusterMode;

    private AreaInterface areaInterface;

    private FunctionInterface functionInterface;

    private NodeInterface nodeInterface;

    private VersionInterface versionInterface;

    private Boolean isUseSpiderNewStart;

    private EventManager eventManager;

    private DataFlowInterface dataFlowInterface;

    private AiTaskInterface aiTaskInterface;

    private HostPluginInterface hostPluginInterface;

    public SpiderServerHandler(ContainerService containerService,
                               FlowService flowService,
                               BusinessService businessService,
                               LogInterface logInterface,
                               BrokerInfoService brokerInfoService,
                               AreaInterface areaInterface,
                               FunctionInterface functionInterface,
                               NodeInterface nodeInterface,
                               VersionInterface versionInterface,
                               Vertx vertx,
                               EventManager eventManager, DataFlowInterface dataFlowInterface,
                               AiTaskInterface aiTaskInterface, HostPluginInterface hostPluginInterface) {
        this.containerService = containerService;
        this.flowService = flowService;
        this.businessService = businessService;
        this.logInterface = logInterface;
        this.brokerInfoService = brokerInfoService;
        this.areaInterface = areaInterface;
        this.versionInterface = versionInterface;
        this.functionInterface = functionInterface;
        this.nodeInterface = nodeInterface;
        this.isUseSpiderNewStart = BrokerInfoUtil.queryStartSpiderNode(vertx);
        this.eventManager = eventManager;
        this.dataFlowInterface = dataFlowInterface;
        this.aiTaskInterface = aiTaskInterface;
        this.hostPluginInterface = hostPluginInterface;
    }

    public void init(Router router) {
        this.router = router;
        deployBpmnFunction();
        unloadFunction();
        startFlow();
        deployClass();
        registerFunction();
        queryElementInfo();
        queryFlowExampleInfo();
        destroyBpmn();
        cleanRedisList();
        selectBpmn();
        selectJar();
        selectFunction();
        functionStateChange();
        deleteFunction();
        deleteAllFunction();
        health();
        startClass();
        startBpmn();
        querySpiderServerInfo();
        queryExampleNumber();
        refreshBpmn();
        queryVersion();
        updateVersion();
        createVersion();
        createVersionV2();
        updateNode();
        queryNode();
        createNode();

        queryFunction();
        startStopFunction();

        updateFunction();

        createFunction();

        queryArea();

        refreshSdk();

        updateAreaSdk();

        updateArea();

        createArea();

        stopStartVersion();

        queryNodeConfig();

        retryStartFlow();

        escalationInfo();

        queryAreaInfo();

        initAreaBaseInfo();

        queryAllAreaInfo();

        initRag();

        querySonAreaInfo();

        deployPlugin();

        queryFlowData();
        upsertFlowData();
        querySonAreaV2();
        querySonAreaBase();
        updateFlowDataStatus();
        queryFlowDataInfo();
        querySonAreaInfos();
        upsertSonAreaInfo();
        queryDatasource();
        queryTables();
        singleStartFlow();
        upsertDomain();
        queryDatasourcePage();
        upsertDatasource();
        queryBusinessFunctionV2();
        upsertBusinessFunctionV2();
        querySonDomainFunction();
        upsertDomainFunction();
        queryFunctionVersion();
        queryDomainFunctionVersion();
        querySonDomainVersion();
        updateSonDomainField();
        upsertDomainFunctionVersion();
        createCoder();
        runCase();
        restartCase();
        queryFunctionCode();
        queryDeployInfo();
        queryCaseInfo();
        syncAiCoderStep();
        updateCoder();
        scalePlugin();
        queryTaskStep();
        notifyDataFlowAnalysis();
        generateDataFlowInfo();
        writeTableAnalysisInfo();
        analysisParam();
        notifyAiAnalysisResult();
        queryParamConfig();
        demandAiParse();
    }

    public void selectBpmn() {
        router.post("/select/bpmn")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    try {
                        JsonObject param = ctx.getBodyAsJson();
                        log.info("请求参数为 {}", param.toString());
                        containerService.queryBpmn(param).onSuccess(suss -> {
                            response.end(ResponseData.suss(suss));
                        }).onFailure(fail -> {
                            log.error("查询失败", ExceptionMessage.getStackTrace(fail));
                            response.end(ResponseData.fail(fail));
                        });
                    } catch (Exception e) {
                        log.error("查询失败", ExceptionMessage.getStackTrace(e));
                        response.end(ResponseData.fail(e));
                    }
                });
    }

    public void selectJar() {
        router.post("/select/jar")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    try {
                        JsonObject param = ctx.getBodyAsJson();
                        containerService.querySdk(param).onSuccess(suss -> {
                            response.end(ResponseData.suss(suss));
                        }).onFailure(fail -> {
                            response.end(ResponseData.fail(fail));
                        });
                    } catch (Exception e) {
                        response.end(ResponseData.fail(e));
                    }
                });
    }


    public void cleanRedisList() {
        router.post("/clean/redis-list")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    try {

                    } catch (Exception e) {
                        response.end(ResponseData.fail(e));
                    }
                });
    }

    /**
     * 部署bpmn
     */
    private void deployBpmnFunction() {
        router.post("/deploy/bpmn")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    Future<Void> registerFuture = containerService.deployBpmn(param);
                    registerFuture.onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.end(ResponseData.fail(fail));
                    });
                });
    }

    private void destroyBpmn() {
        router.post("/destroy/bpmn")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    Future<Void> registerFuture = containerService.destroyBpmn(param);
                    registerFuture.onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.end(ResponseData.fail(fail));
                    });
                });
    }


    private void startBpmn() {
        router.post("/start/bpmn")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    Future<Void> registerFuture = containerService.startBpmn(param);
                    registerFuture.onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.end(ResponseData.fail(fail));
                    });
                });
    }


    private void startFlow() {
        router.post("/start/function")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    /*if (!rateLimiter.tryAcquire(1)) {
                        response.end(ResponseData.fail("被限流了请重试"));
                        return;
                    }*/
                    JsonObject param = ctx.getBodyAsJson();
                    Future<JsonObject> flowFuture = this.isUseSpiderNewStart ? flowService.startFlowV2(param) : flowService.startFlow(param);
                    flowFuture.onSuccess(suss -> {
                        JsonObject result = suss;
                        response.end(ResponseData.suss(result));
                    }).onFailure(fail -> {
                        response.end(ResponseData.fail(fail));
                    });
                });
    }

    private void retryStartFlow() {
        router.post("/retry/retry_business_node")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    Future<JsonObject> flowFuture = flowService.startFlowRetry(param);
                    flowFuture.onSuccess(suss -> {
                        JsonObject result = suss;
                        response.end(ResponseData.suss(result));
                    }).onFailure(fail -> {
                        response.end(ResponseData.fail(fail));
                    });
                });
    }

    private void deployClass() {
        router.post("/deploy/class")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    Future<Void> registerFuture = containerService.loaderClass(param);
                    registerFuture.onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.end(ResponseData.fail(fail));
                    });
                });
    }


    private void startClass() {
        router.post("/start/class")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    Future<Void> registerFuture = containerService.startClass(param);
                    registerFuture.onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.end(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 部署功能
     */
    private void unloadFunction() {
        router.post("/unload/class")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    Future<Void> destroyrFuture = containerService.destroyClass(param);
                    destroyrFuture.onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.end(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 新增具体的业务功能
     */
    private void registerFunction() {
        router.post("/register/function")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    Future<JsonObject> registerFuture = businessService.registerFunction(param);
                    registerFuture.onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        log.error("/register/function注册失败 {}", ExceptionMessage.getStackTrace(fail));
                        response.end(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 删除功能
     */
    private void deleteFunction() {
        router.post("/delete/function")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    Future<Void> deleteFuture = businessService.deleteFunction(param);
                    deleteFuture.onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        log.error("/delete/function删除失败{}", ExceptionMessage.getStackTrace(fail));
                        response.end(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 修改功能状态
     */
    private void functionStateChange() {
        router.post("/state/function/change")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    Future<Void> stateChangeFuture = businessService.stateChange(param);
                    stateChangeFuture.onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        log.error("/state/function/change更改状态失败 {}", ExceptionMessage.getStackTrace(fail));
                        response.end(ResponseData.fail(fail));
                    });
                });
    }


    /**
     * 查询功能
     */
    private void selectFunction() {
        router.post("/query/function")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    Future<JsonObject> elementResponse = businessService.selectFunction(param);
                    elementResponse.onSuccess(suss -> {
                        response.end(ResponseData.sussJson(suss));
                    }).onFailure(fail -> {
                        log.error("/query/function查询失败 {}", ExceptionMessage.getStackTrace(fail));
                        response.end(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 查询节点信息
     */
    private void queryElementInfo() {
        router.post("/query/elementInfo")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    Future<JsonObject> elementResponse = logInterface.queryElementExample(param);
                    elementResponse.onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        log.error("/query/elementInfo查询失败 {}", ExceptionMessage.getStackTrace(fail));
                        response.end(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 查询节点详情
     */
    private void queryFlowExampleInfo() {
        router.post("/query/flowExampleInfo")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    Future<JsonObject> elementResponse = logInterface.queryFlowExample(param);
                    elementResponse.onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        log.error("/query/flowExampleInfo查询失败 {}", ExceptionMessage.getStackTrace(fail));
                        response.end(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 删除功能信息
     */
    private void deleteAllFunction() {
        router.post("/delete/all/function")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    Future<Void> deleteFuture = businessService.deleteAll();
                    deleteFuture.onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        log.error("/delete/all/function {}", ExceptionMessage.getStackTrace(fail));
                        response.end(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 健康检查
     */
    private void health() {
        router.get("/actuator/health")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    response.end(ResponseData.suss());
                });
    }

    private void querySpiderServerInfo() {
        router.post("/query/spider/server/info")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");

                    this.brokerInfoService.queryBrokerInfo().onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void queryExampleNumber() {
        router.post("/query/example/size")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    this.flowService.queryRunNumber().onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void buildMode(Vertx vertx) {
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");
        this.clusterMode = localMap.get("cluster_mode");
        // 去中心化模式
    }

    /**
     * 创建领域
     */
    private void createArea() {
        router.post("/create/area")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    areaInterface.insertArea(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 创建领域
     */
    private void updateArea() {
        router.post("/update/area")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    areaInterface.updateArea(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 上传域sdk
     */
    private void updateAreaSdk() {
        router.post("/update/area/sdk")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    areaInterface.updateSdk(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 刷新sdk
     */
    private void refreshSdk() {
        router.post("/refresh/area/sdk")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    areaInterface.refreshSdk(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 查询领域
     */
    private void queryArea() {
        router.post("/query/area")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    areaInterface.queryArea(param).onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 新增域业务功能
     */
    private void createFunction() {
        router.post("/create/business_function")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    functionInterface.insertFunction(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 修改域业务功能
     */
    private void updateFunction() {
        router.post("/update/business_function")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    functionInterface.updateFunction(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 查询业务功能
     */
    private void queryFunction() {
        router.post("/query/business_function")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    functionInterface.queryFunction(param).onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 功能启停
     */
    private void startStopFunction() {
        router.post("/start_stop/function")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    functionInterface.startStopFunction(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 新增域节点
     */
    private void createNode() {
        router.post("/create/area_node")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    nodeInterface.insertNode(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 新增域节点
     */
    private void queryNode() {
        router.post("/query/area_node")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    nodeInterface.queryJsonObject(param).onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 查询节点配置
     */
    private void queryNodeConfig() {
        router.post("/query/area_node_config")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    nodeInterface.queryParamConfig(param).onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 查询节点配置
     */
    private void initRag() {
        router.post("/init/rag")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    nodeInterface.initRag().onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }


    /**
     * 新增域节点
     */
    private void updateNode() {
        router.post("/update/area_node")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    nodeInterface.updateNode(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 新增版本
     */
    private void createVersion() {
        router.post("/create/version")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    versionInterface.insertVersion(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void createVersionV2() {
        router.post("/upsert/businessVersion_v2")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    versionInterface.insertVersionV2(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 新增版本
     */
    private void updateVersion() {
        router.post("/update/version")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    versionInterface.updateVersion(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 查询版本
     */
    private void queryVersion() {
        router.post("/query/version")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    versionInterface.queryVersion(param).onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 刷新-bpmn
     */
    private void refreshBpmn() {
        router.post("/refresh/bpmn")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    versionInterface.refreshVersion(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 启停-功能版本
     */
    private void stopStartVersion() {
        router.post("/stop_start/version")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    versionInterface.startOrStopVersion(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 部署代码
     */
    private void deployPlugin() {
        router.post("/deploy/plugin")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    log.info("deployPlugin-info {}", param.toString());
                    nodeInterface.deployCode(param).onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 查询-领域信息
     */
    private void queryAreaInfo() {
        router.post("/query/area_info")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    nodeInterface.queryBaseNodes(param).onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void queryAllAreaInfo() {
        router.post("/query/area_all_info")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    nodeInterface.areaNodeBase().onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    // 上报领域信息
    private void escalationInfo() {
        router.post("/escalation/area_info")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    log.info("上报的数据为 {}", param.toString());
                    EscalationData escalationData = param.mapTo(EscalationData.class);
                    eventManager.sendMessage(EventType.ESCALATION_AREA_INFO, escalationData);
                    // 刷新数据
                    nodeInterface.refreshParam(JsonObject.mapFrom(escalationData.getRefreshAreaParam())).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void initAreaBaseInfo() {
        router.post("/init/area_base_info")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    nodeInterface.initAreaBase(param).onSuccess(suss -> {
                        // 进行部署
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                        log.error("初始化失败 {}", fail);

                    });
                });
    }

    private void querySonAreaInfo() {
        router.post("/query/son_area")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    nodeInterface.querySonAreaBaseInfo(param).onSuccess(suss -> {
                        // 进行部署
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    // 查询数据流
    private void queryFlowData() {
        router.post("/query/flow_data")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    dataFlowInterface.queryDataFlow(param).onSuccess(suss -> {
                        // 进行部署
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void queryFlowDataInfo() {
        router.post("/query/flow_data_info")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    dataFlowInterface.queryDataFlowInfos(param).onSuccess(suss -> {
                        // 进行部署
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }


    // 新增或者修改数据流
    // 查询数据流
    private void upsertFlowData() {
        router.post("/upsert/flow_data")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    dataFlowInterface.upsertDataFlow(param).onSuccess(suss -> {
                        // 进行部署
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    // 查询子域信息
    private void querySonAreaV2() {
        router.post("/query/son_area_v2")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    areaInterface.querySonArea(param).onSuccess(suss -> {
                        // 进行部署
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    // 查询子域信息
    private void querySonAreaBase() {
        router.post("/query/son_area_base")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    areaInterface.querySonBase(param).onSuccess(suss -> {
                        // 进行部署
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 数据流启停
     */
    // 查询子域信息
    private void updateFlowDataStatus() {
        router.post("/data_flow/update_status")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    dataFlowInterface.upsertDataFlowStatus(param).onSuccess(suss -> {
                        // 进行部署
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void querySonAreaInfos() {
        router.post("/query/son_area_infos")
                .handler(ctx -> {
                    System.out.println("22222222");
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    areaInterface.querySonAreaInfos(param).onSuccess(suss -> {
                        // 进行部署
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void upsertSonAreaInfo() {
        router.post("/upsert/son_area_info")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    areaInterface.upsertSonAreaInfo(param).onSuccess(suss -> {
                        // 进行部署
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    //
    private void queryDatasource() {
        router.post("/query/datasource")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    areaInterface.queryDatasource(param).onSuccess(suss -> {
                        // 进行部署
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void queryTables() {
        router.post("/query/tables")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    areaInterface.queryTableInfo(param).onSuccess(suss -> {
                        // 进行部署
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void singleStartFlow() {
        router.post("/single_start_node")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    flowService.simpleStartNode(param).onSuccess(suss -> {
                        // 进行部署
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void upsertDomain() {
        router.post("/upsert_domain")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    areaInterface.upsertAreaV2(param).onSuccess(suss -> {
                        // 进行部署
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }
    // queryDatasourcePage

    /**
     * 查询数据源分页
     */
    private void queryDatasourcePage() {
        router.post("/query_datasource_page")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    areaInterface.queryDatasourcePage(param).onSuccess(suss -> {
                        // 进行部署
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void upsertDatasource() {
        router.post("/upsert/datasource")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    areaInterface.upsertDatasource(param).onSuccess(suss -> {
                        // 进行部署
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void queryBusinessFunctionV2() {
        router.post("/query_business_function_v2")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    functionInterface.queryBusinessFunctionV2(param).onSuccess(suss -> {
                        // 进行部署
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void upsertBusinessFunctionV2() {
        router.post("/upsert_business_function_v2")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    functionInterface.upsertBusinessFunctionV2(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void querySonDomainFunction() {
        router.post("/query/domain_function")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    nodeInterface.queryDomainFunction(param).onSuccess(suss -> {
                        // 进行部署
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void upsertDomainFunction() {
        router.post("/upsert/domain_function")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    nodeInterface.updateDomainFunction(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void queryFunctionVersion() {
        router.post("/query/business_function_version")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    versionInterface.queryVersionByFunctionId(param).onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void queryDomainFunctionVersion() {
        router.post("/query/domain_function_version")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    nodeInterface.queryDomainFunctionVersion(param).onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void querySonDomainVersion() {
        router.post("/query/son_domain_version")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    areaInterface.querySonDomainVersion(param).onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });

    }

    private void updateSonDomainField() {
        router.post("/update_son_domain_field")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    areaInterface.updateSonDomainField(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void upsertDomainFunctionVersion() {
        router.post("/upsert/domain_function_version")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    functionInterface.upsertDomainVersion(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void createCoder() {
        router.post("/create_coder")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    aiTaskInterface.createCoder(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void updateCoder() {
        router.post("/update_coder")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    log.info("updateCoder-param:{}", param);
                    aiTaskInterface.updateCoder(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 扩缩容插件
     */
    private void scalePlugin() {
        router.post("/scale_plugin")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    hostPluginInterface.scalePlugin(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void syncAiCoderStep() {
        router.post("/sync_ai_coder_step")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    aiTaskInterface.syncAiCoderStep(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void queryTaskStep() {
        router.post("/query/task_step")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    aiTaskInterface.queryTaskStep(param).onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void runCase() {
        router.post("/run_case")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    aiTaskInterface.startTestCase(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void restartCase() {
        router.post("/restart_case")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    aiTaskInterface.restartCase(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void queryFunctionCode() {
        router.post("/query_function_code")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    hostPluginInterface.queryFunctionVersion(param).onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void queryDeployInfo() {
        router.post("/query_deploy_info")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    hostPluginInterface.queryDeployInfo(param).onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void queryCaseInfo() {
        router.post("/query_case_info")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    aiTaskInterface.queryTestCaseInfo(param).onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * 通知数据流分析的结果
     */
    private void notifyDataFlowAnalysis() {
        router.post("/notify/domain_info")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    dataFlowInterface.upsertDataFlowParse(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });

    }

    /**
     * /generate_data_flow_info
     */
    private void generateDataFlowInfo() {
        router.post("/generate_data_flow_info")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    dataFlowInterface.generateDataFlowInfo(ctx.getBodyAsJson()).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    // writeTableAnalysisInfo
    private void writeTableAnalysisInfo() {
        router.post("/write_table_analysis_info")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    log.info("param=== {}", param.toString());
                    nodeInterface.writeTableAnalysisInfo(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void analysisParam() {
        router.post("/analysis_param")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    nodeInterface.analysisParam(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    private void notifyAiAnalysisResult() {
        router.post("/notify/ai_analysis_result")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    nodeInterface.notifyAiAnalysis(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    // /query/analysis_param
    private void queryParamConfig() {
        router.post("/query/analysis_param")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    nodeInterface.queryAnalysisParam(param).onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

    /**
     * demand解析
     */
    private void demandAiParse() {
        router.post("/demand_analysis")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    JsonObject param = ctx.getBodyAsJson();
                    aiTaskInterface.demandAiParse(param).onSuccess(suss -> {
                        response.end(ResponseData.suss());
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });
                });
    }

}
