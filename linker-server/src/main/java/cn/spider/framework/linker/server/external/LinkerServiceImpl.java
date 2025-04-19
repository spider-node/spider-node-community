package cn.spider.framework.linker.server.external;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.common.utils.TaskKeyUtil;
import cn.spider.framework.container.sdk.interfaces.FlowService;
import cn.spider.framework.domain.sdk.data.FlowElementModel;
import cn.spider.framework.domain.sdk.data.FlowExampleModel;
import cn.spider.framework.domain.sdk.interfaces.FunctionInterface;
import cn.spider.framework.linker.sdk.data.*;
import cn.spider.framework.linker.sdk.interfaces.LinkerService;
import cn.spider.framework.linker.server.baseinfo.BaseManager;
import cn.spider.framework.linker.server.http.HttpActuator;
import cn.spider.framework.linker.server.http.data.HttpTestData;
import cn.spider.framework.linker.server.socket.ClientInfo;
import cn.spider.framework.linker.server.socket.WorkerRegisterManager;
import cn.spider.framework.proto.grpc.TransferRequest;
import cn.spider.framework.proto.grpc.TransferResponse;
import cn.spider.framework.proto.grpc.VertxTransferServerGrpc;
import cn.spider.node.host.plugin.center.sdk.data.QueryFunctionInfo;
import cn.spider.node.host.plugin.center.sdk.data.QueryFunctionInfoResult;
import cn.spider.node.host.plugin.center.sdk.interfaces.HostPluginInterface;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @program: spider-node
 * @description: 对外提供能力的实现类
 * @author: dds
 * @create: 2023-03-02 13:34
 */
@Slf4j
public class LinkerServiceImpl implements LinkerService {

    private FunctionInterface functionInterface;

    private WorkerRegisterManager workerRegisterManager;

    private HostPluginInterface hostPluginInterface;

    private BaseManager baseManager;

    private FlowService flowService;

    private HttpActuator httpActuator;

    private final String REQUEST = "request";

    private final String FUNCTION_ID = "functionId";


    public LinkerServiceImpl(FunctionInterface functionInterface, WorkerRegisterManager workerRegisterManager, HostPluginInterface hostPluginInterface, BaseManager baseManager, HttpActuator httpActuator, FlowService flowService) {
        this.functionInterface = functionInterface;
        this.workerRegisterManager = workerRegisterManager;
        this.hostPluginInterface = hostPluginInterface;
        this.baseManager = baseManager;
        this.httpActuator = httpActuator;
        this.flowService = flowService;
    }

    /**
     * 版本号的缓存
     */
    private final Cache<String, String> cache = CacheBuilder.newBuilder()
            //设置cache的初始大小为10，要合理设置该值
            .initialCapacity(10)
            //设置并发数为10，即同一时间最多只能有10个线程往cache执行写入操作
            .concurrencyLevel(2)
            //设置cache中的数据在写入之后的存活时间为10分钟
            .expireAfterWrite(1, TimeUnit.MINUTES)
            //构建cache实例
            .build();

    /**
     * 执行入口
     *
     * @param param
     * @return
     */
    @Override
    public Future<JsonObject> submittals(JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();
        LinkerServerRequest linkerServerRequest = JSON.parseObject(param.toString(), LinkerServerRequest.class);
        // 如果为虚拟执行
        if (linkerServerRequest.getRetryType().equals(Constant.VIRTUALLY)) {
            JsonObject queryHistoryParam = new JsonObject();
            queryHistoryParam.put(Constant.REQUEST_ID, linkerServerRequest.getParentRequestId());
            functionInterface.queryRunHistoryElementData(queryHistoryParam)
                    .onSuccess(suss -> {
                        FlowExampleModel flowExampleModel = suss.mapTo(FlowExampleModel.class);
                        log.info("--------执行虚拟数据ComponentName {} service {}", linkerServerRequest.getFunctionRequest().getComponentName(), linkerServerRequest.getFunctionRequest().getServiceName());
                        if (CollectionUtils.isEmpty(flowExampleModel.getFlowElementModelList())) {
                            promise.fail("没有找到对应的节点数据");
                            return;
                        }
                        Optional<FlowElementModel> optionalFlowElementModel = flowExampleModel
                                .getFlowElementModelList()
                                .stream()
                                .filter(item -> item.getFlowElementId().equals(linkerServerRequest.getNowNodeId()) && StringUtils.isNotEmpty(item.getRequestParam()))
                                .findFirst();

                        if (!optionalFlowElementModel.isPresent()) {
                            promise.fail("没有找到需要执行的对应的节点数据");
                            return;
                        }
                        FlowElementModel flowElementModel = optionalFlowElementModel.get();
                        LinkerServerResponse responseNew = buildLinkerServerVirtuallyResponse(flowElementModel.getReturnParam());
                        promise.complete(new JsonObject().put(Constant.DATA, JsonObject.mapFrom(responseNew)));
                    }).onFailure(fail -> {
                        log.error(fail.getMessage());
                        promise.fail(fail);
                    });
            return promise.future();
        }
        JsonObject request = new JsonObject(linkerServerRequest.getFunctionRequest().getParam());
        switch (linkerServerRequest.getFunctionRequest().getFunctionType()) {
            case Constant.BUSINESS_FUNCTION:

                businessFunctionRun(request, linkerServerRequest.getFunctionRequest().getFunctionVersionId(), promise);
                break;
            case Constant.DOMAIN_FUNCTION:
                runBusinessRequest(linkerServerRequest.getFunctionRequest(), promise, param);
                break;
            case Constant.HTTP_FUNCTION:
                runHttpFunction(request, promise, linkerServerRequest.getFunctionRequest().getHttpUrl(), linkerServerRequest.getFunctionRequest().getHttpType(), linkerServerRequest.getFunctionRequest().getHttpHeader());
                break;
        }


        // 解析请求，是走功能请求，还是事务操作的请求
        return promise.future();
    }

    private void runHttpFunction(JsonObject param, Promise<JsonObject> promise, String url, String httpType, Map<String, String> header) {
        switch (httpType) {
            case Constant.GET:
                httpActuator.get(url, param, promise, header);
                break;
            case Constant.POST:
                httpActuator.post(url, param, promise, header);
                break;
        }
    }


    private void businessFunctionRun(JsonObject request, String functionId, Promise<JsonObject> promise) {
        JsonObject param = new JsonObject();
        param.put(REQUEST, request);
        param.put(FUNCTION_ID, functionId);
        Future<JsonObject> resultFuture = flowService.startFlowV2(param);
        LinkerServerResponse linkerServerResponse = new LinkerServerResponse();
        resultFuture.onSuccess(suss -> {
            linkerServerResponse.setResultCode(ResultCode.SUSS);
            linkerServerResponse.setResultData(JSONObject.parseObject(suss.toString()));
            promise.complete(JsonObject.mapFrom(linkerServerResponse));
        }).onFailure(fail -> {
            linkerServerResponse.setResultCode(ResultCode.FAIL);
            linkerServerResponse.setExceptional(ExceptionMessage.getStackTrace(fail));
            promise.complete(JsonObject.mapFrom(linkerServerResponse));
        });
    }

    @Override
    public Future<JsonObject> transaction(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        runTransaction(promise, data);
        return promise.future();
    }


    @Override
    public Future<JsonObject> queryHostApplication(JsonObject data) {

        return Future.succeededFuture(new JsonObject());
    }

    @Override
    public Future<JsonObject> queryTaskDeploy(JsonObject data) {
        QueryTaskDeployParam queryTaskDeployParam = JSON.parseObject(data.toString(), QueryTaskDeployParam.class);
        log.info("查询任务部署信息，组件：{} 服务：{} 版本：{}", queryTaskDeployParam.getTaskComponent(), queryTaskDeployParam.getTaskService(), queryTaskDeployParam.getVersion());
        Set<String> ips = baseManager.queryIpByFunctionKey(queryTaskDeployParam.getTaskComponent(), queryTaskDeployParam.getTaskService(), queryTaskDeployParam.getVersion());
        QueryTaskDeployResult queryTaskDeployResult = new QueryTaskDeployResult(ips);
        return Future.succeededFuture(JsonObject.mapFrom(queryTaskDeployResult));
    }

    @Override
    public Future<JsonObject> httpTest(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        HttpTestData httpTestData = data.mapTo(HttpTestData.class);
        switch (httpTestData.getHttpType()) {
            case Constant.GET:
                httpActuator.get(httpTestData.getHttpUrl(), httpTestData.getParam(), promise, httpTestData.getHttpHeader());
                break;
            case Constant.POST:
                httpActuator.post(httpTestData.getHttpUrl(), httpTestData.getParam(), promise, httpTestData.getHttpHeader());
                break;
        }
        return promise.future();
    }

    /**
     * 执行业务请求
     *
     * @param functionRequest 功能请求参数信息
     * @param promise
     * @param param           请求参数
     */
    private void runBusinessRequest(FunctionRequest functionRequest, Promise<JsonObject> promise, JsonObject param) {
        log.info("调用的参数信息为{}", JSON.toJSONString(functionRequest));
        // 先获取版本。
        queryVersion(functionRequest.getVersion(), functionRequest.getComponentName(), functionRequest.getServiceName()).onSuccess(versionSuss -> {
            //grpc调用
            ClientInfo clientInfo = null;
            try {
                functionRequest.setVersion(versionSuss);
                clientInfo = workerRegisterManager.queryClientInfo(functionRequest.getComponentName(), functionRequest.getServiceName(), functionRequest.getVersion(), functionRequest.getWorkerName(), functionRequest.getProviderType());
            } catch (Exception e) {
                promise.fail(e);
                log.info("submittals {}", ExceptionMessage.getStackTrace(e));
                return;
            }
            VertxTransferServerGrpc.TransferServerVertxStub serverVertxStub = clientInfo.getServerVertxStub();
            TransferRequest transferRequest = TransferRequest.newBuilder()
                    .setBody(param.toString())
                    .setHeader(Constant.SPIDER_FUNCTION)
                    .setTaskComponentName(functionRequest.getComponentName())
                    .setTaskComponentVersion(StringUtils.isEmpty(functionRequest.getVersion()) ? "v1" : functionRequest.getVersion())
                    .build();
            // 远程rpc调用
            Future<TransferResponse> response = serverVertxStub.instruct(transferRequest);
            // log.info("获取参数-------------立马调用远程 时间 {}",System.currentTimeMillis());
            response.onSuccess(suss -> {
                TransferResponse result = suss;
                LinkerServerResponse responseNew = buildLinkerServerResponse(result);
                promise.complete(new JsonObject().put(Constant.DATA, JsonObject.mapFrom(responseNew)));
            }).onFailure(fail -> {
                log.error(ExceptionMessage.getStackTrace(fail));
                promise.fail(fail);
            });
        }).onFailure(fail -> {
            log.info("查询版本失败 {}", ExceptionMessage.getStackTrace(fail));
            promise.fail(fail);
        });
    }

    private Future<String> queryVersion(String version, String taskComponent, String taskService) {
        if (StringUtils.isNotEmpty(version)) {
            return Future.succeededFuture(version);
        }
        String key = TaskKeyUtil.buildTaskKey(taskComponent, taskService);
        String versionOld = cache.getIfPresent(key);
        if (StringUtils.isNotEmpty(versionOld)) {
            return Future.succeededFuture(versionOld);
        }
        // 查询缓存 查询到就直接返回
        Promise<String> promise = Promise.promise();
        QueryFunctionInfo queryFunctionInfo = new QueryFunctionInfo();
        queryFunctionInfo.setTaskService(taskService);
        queryFunctionInfo.setTaskComponent(taskComponent);
        hostPluginInterface.queryFunctionVersion(JsonObject.mapFrom(queryFunctionInfo)).onSuccess(functionSuss -> {
            QueryFunctionInfoResult queryFunctionInfoResult = functionSuss.mapTo(QueryFunctionInfoResult.class);

            cache.put(key, queryFunctionInfoResult.getVersion());
            promise.complete(queryFunctionInfoResult.getVersion());
        }).onFailure(functionFail -> {
            promise.fail(functionFail);
        });
        return promise.future();
    }


    /**
     * 执行事务请求
     *
     * @param promise
     */
    private void runTransaction(Promise<JsonObject> promise, JsonObject param) {
        // vertx-rpc调用
        ClientInfo clientInfo = workerRegisterManager.queryRandom();
        VertxTransferServerGrpc.TransferServerVertxStub serverVertxStub = clientInfo.getServerVertxStub();
        TransferRequest transferRequest = TransferRequest.newBuilder()
                .setBody(param.toString())
                .setHeader(Constant.SPIDER_FUNCTION)
                .build();
        Future<TransferResponse> response = serverVertxStub.instruct(transferRequest);
        response.onSuccess(suss -> {
            TransferResponse result = suss;
            log.info("runTransaction-result {}", JSON.toJSONString(result));
            LinkerServerResponse responseNew = buildLinkerServerResponse(result);
            promise.complete(new JsonObject().put(Constant.DATA, JsonObject.mapFrom(responseNew)));
        }).onFailure(fail -> {
            log.error(fail.getMessage());
            promise.fail(fail);
        });
    }


    /**
     * 解析客户端响应参数
     *
     * @param result
     * @return
     */
    public LinkerServerResponse buildLinkerServerResponse(TransferResponse result) {
        LinkerServerResponse response = new LinkerServerResponse();
        response.setResultCode(result.getCode() == Constant.sussCode ? ResultCode.SUSS : ResultCode.FAIL);
        response.setExceptional(result.getMessage());
        response.setResultData(StringUtils.isEmpty(result.getData()) ? new JSONObject() : JSON.parseObject(result.getData()));
        return response;
    }

    /**
     * 解析客户端响应参数
     *
     * @param result
     * @return
     */
    public LinkerServerResponse buildLinkerServerVirtuallyResponse(String result) {
        LinkerServerResponse response = new LinkerServerResponse();
        response.setResultCode(ResultCode.SUSS);
        response.setResultData(StringUtils.isEmpty(result) ? new JSONObject() : JSON.parseObject(result));
        return response;
    }

}
