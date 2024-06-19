package cn.spider.framework.gateway.api.function;

import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.container.sdk.interfaces.BusinessService;
import cn.spider.framework.container.sdk.interfaces.ContainerService;
import cn.spider.framework.container.sdk.interfaces.FlowService;
import cn.spider.framework.controller.sdk.interfaces.BrokerInfoService;
import cn.spider.framework.controller.sdk.interfaces.LeaderHeartService;
import cn.spider.framework.domain.sdk.interfaces.AreaInterface;
import cn.spider.framework.domain.sdk.interfaces.FunctionInterface;
import cn.spider.framework.domain.sdk.interfaces.NodeInterface;
import cn.spider.framework.domain.sdk.interfaces.VersionInterface;
import cn.spider.framework.gateway.common.ResponseData;
import cn.spider.framework.log.sdk.interfaces.LogInterface;
import cn.spider.framework.param.result.build.interfaces.ParamRefreshInterface;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;

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

    private LeaderHeartService leaderHeartService;

    private BrokerInfoService brokerInfoService;

    private String clusterMode;

    private AreaInterface areaInterface;

    private FunctionInterface functionInterface;

    private NodeInterface nodeInterface;

    private VersionInterface versionInterface;

    private Vertx vertx;

    private Boolean isUseSpiderNewStart;

    private ParamRefreshInterface paramRefreshInterface;

    public SpiderServerHandler(ContainerService containerService,
                               FlowService flowService,
                               BusinessService businessService,
                               LogInterface logInterface,
                               LeaderHeartService leaderHeartService,
                               BrokerInfoService brokerInfoService,
                               AreaInterface areaInterface,
                               FunctionInterface functionInterface,
                               NodeInterface nodeInterface,
                               VersionInterface versionInterface, ParamRefreshInterface paramRefreshInterface, Vertx vertx) {
        this.containerService = containerService;
        this.flowService = flowService;
        this.businessService = businessService;
        this.logInterface = logInterface;
        this.leaderHeartService = leaderHeartService;
        this.brokerInfoService = brokerInfoService;
        this.areaInterface = areaInterface;
        this.versionInterface = versionInterface;
        this.functionInterface = functionInterface;
        this.nodeInterface = nodeInterface;
        this.isUseSpiderNewStart = BrokerInfoUtil.queryStartSpiderNode(vertx);
        this.paramRefreshInterface = paramRefreshInterface;
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

        // 刷新-sdk
        refreshMethodRunParam();

        queryNodeConfig();

        retryStartFlow();

    }

    public void refreshMethodRunParam() {
        router.post("/refresh/method/param")
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    try {
                        JsonObject param = ctx.getBodyAsJson();
                        log.info("请求参数为 {}", param.toString());
                        paramRefreshInterface.refreshMethod(param).onSuccess(suss -> {
                            response.end(ResponseData.suss());
                        }).onFailure(fail -> {
                            response.end(ResponseData.fail(fail));
                        });
                    } catch (Exception e) {
                        log.error("查询失败", ExceptionMessage.getStackTrace(e));
                        response.end(ResponseData.fail(e));
                    }
                });
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


                   /* if (this.clusterMode.equals(Constant.N0_CENTER_MODE)) {

                        return;
                    }
                    leaderHeartService.querySpiderInfo().onSuccess(suss -> {
                        response.end(ResponseData.suss(suss));
                    }).onFailure(fail -> {
                        response.send(ResponseData.fail(fail));
                    });*/
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
}
