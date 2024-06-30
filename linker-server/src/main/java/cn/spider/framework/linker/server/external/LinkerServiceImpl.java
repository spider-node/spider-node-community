package cn.spider.framework.linker.server.external;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.domain.sdk.data.FlowElementModel;
import cn.spider.framework.domain.sdk.data.FlowExampleModel;
import cn.spider.framework.domain.sdk.interfaces.FunctionInterface;
import cn.spider.framework.linker.sdk.data.*;
import cn.spider.framework.linker.sdk.interfaces.LinkerService;
import cn.spider.framework.linker.sdk.interfaces.VertxRpcTaskInterface;
import cn.spider.framework.linker.server.socket.ClientInfo;
import cn.spider.framework.linker.server.socket.ClientRegisterCenter;
import cn.spider.framework.proto.grpc.TransferRequest;
import cn.spider.framework.proto.grpc.TransferResponse;
import cn.spider.framework.proto.grpc.VertxTransferServerGrpc;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @program: spider-node
 * @description: 对外提供能力的实现类
 * @author: dds
 * @create: 2023-03-02 13:34
 */
@Slf4j
public class LinkerServiceImpl implements LinkerService {

    private ClientRegisterCenter clientRegisterCenter;

    private Boolean isVertxRpc;

    private Map<String, VertxRpcTaskInterface> rpcTaskInterfaceMap;

    private Vertx vertx;

    private FunctionInterface functionInterface;

    public LinkerServiceImpl(ClientRegisterCenter clientRegisterCenter, Vertx vertx,FunctionInterface functionInterface) {
        this.clientRegisterCenter = clientRegisterCenter;
        String rpcType = BrokerInfoUtil.queryRpcType(vertx);
        this.isVertxRpc = rpcType.equals("vertxRpc");
        this.rpcTaskInterfaceMap = new HashMap<>();
        this.vertx = vertx;
        this.functionInterface = functionInterface;
    }

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
                        log.info("--------执行虚拟数据ComponentName {} service {}",linkerServerRequest.getFunctionRequest().getComponentName(),linkerServerRequest.getFunctionRequest().getServiceName());
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

        // 解析请求，是走功能请求，还是事务操作的请求
        switch (linkerServerRequest.getExecutionType()) {
            case FUNCTION:
                runBusinessRequest(linkerServerRequest.getFunctionRequest(), promise, param);
                break;
            case TRANSACTION:
                runTransaction(linkerServerRequest.getTransactionalRequest(), promise, param);
                break;
        }
        return promise.future();
    }

    /**
     * 执行业务请求
     *
     * @param functionRequest 功能请求参数信息
     * @param promise
     * @param param 请求参数
     */
    private void runBusinessRequest(FunctionRequest functionRequest, Promise<JsonObject> promise, JsonObject param) {
        //grpc调用
        ClientInfo clientInfo = clientRegisterCenter.queryClientInfo(functionRequest.getWorkerName());
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

    }

    /**
     * 执行事务请求
     *
     * @param request
     * @param promise
     * @param param
     */
    private void runTransaction(TransactionalRequest request, Promise<JsonObject> promise, JsonObject param) {
        // vertx-rpc调用
        if (isVertxRpc) {
            String workerName = request.getWorkerName();
            if (!rpcTaskInterfaceMap.containsKey(workerName)) {
                String addr = workerName + VertxRpcTaskInterface.ADDRESS;
                VertxRpcTaskInterface vertxRpcTaskInterface = VertxRpcTaskInterface.createProxy(vertx, addr);
                rpcTaskInterfaceMap.put(workerName, vertxRpcTaskInterface);
            }
            VertxRpcTaskInterface vertxRpcTaskInterface = rpcTaskInterfaceMap.get(workerName);
            Future<JsonObject> future = vertxRpcTaskInterface.run(param);
            future.onSuccess(suss -> {
                LinkerServerResponse responseNew = suss.mapTo(LinkerServerResponse.class);
                promise.complete(new JsonObject().put(Constant.DATA, JsonObject.mapFrom(responseNew)));
            }).onFailure(fail -> {
                log.error(fail.getMessage());
                promise.fail(fail);
            });

        } else {
            ClientInfo clientInfo = clientRegisterCenter.queryClientInfo(request.getWorkerName());
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
