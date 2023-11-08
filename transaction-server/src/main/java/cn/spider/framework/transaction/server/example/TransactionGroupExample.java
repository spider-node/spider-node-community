package cn.spider.framework.transaction.server.example;

import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.EndTransactionData;
import cn.spider.framework.common.event.enums.TransactionType;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.linker.sdk.data.*;
import cn.spider.framework.linker.sdk.interfaces.LinkerService;
import cn.spider.framework.transaction.sdk.data.TransactionOperateResponse;
import cn.spider.framework.transaction.sdk.data.TransactionOperateStatus;
import cn.spider.framework.transaction.sdk.data.enums.TransactionStatus;
import cn.spider.framework.transaction.server.TransactionServerVerticle;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.transaction.server.example
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-11  00:44
 * @Description: 事务组的实例
 * @Version: 1.0
 */
@Slf4j
public class TransactionGroupExample {

    /**
     * 事务组id
     */
    private String groupId;

    /**
     * 事务组
     */
    private List<TransactionExample> transactionExampleList;

    /**
     * 通知调用方事务执行状态
     */
    private Promise<JsonObject> promise;

    private EventManager eventManager;

    public TransactionGroupExample(String groupId, List<TransactionExample> transactionExampleList, Promise<JsonObject> promise) {
        this.groupId = groupId;
        this.transactionExampleList = transactionExampleList;
        this.promise = promise;
        this.eventManager = TransactionServerVerticle.factory.getBean(EventManager.class);
        // 初始化错误次数
    }

    public void runCommit(LinkerService linkerService) {
        for(TransactionExample transactionExample : this.transactionExampleList){
            commit(transactionExample, linkerService);
        }
    }

    public void runRollBack(LinkerService linkerService) {
        for(TransactionExample transactionExample : this.transactionExampleList){
            rollBack(transactionExample, linkerService);
        }
    }

    public void commit(TransactionExample example, LinkerService linkerService) {
        log.info("事务执行内容 {}",JSON.toJSONString(example));
        LinkerServerRequest linkerServerRequest = buildRequestEntity(example, TransactionalType.SUBMIT);
        JsonObject request = JsonObject.mapFrom(linkerServerRequest);
        Future<JsonObject>  commitResult = linkerService.submittals(request);

        EndTransactionData endTransactionData = EndTransactionData.builder()
                .transactionGroupId(example.getTransactionGroupId())
                .requestId(example.getRequestId())
                .flowElementId(example.getTaskId())
                .branchId(example.getBranchId())
                .transactionOperate(TransactionType.COMMIT)
                .build();


        commitResult.onSuccess(suss -> {
            JsonObject result = suss;
            log.info("事务返回的结果为 事务返回信息为 {}",result.toString());
            LinkerServerResponse responseNew = result.getJsonObject("data").mapTo(LinkerServerResponse.class);
            if(responseNew.getResultCode().equals(ResultCode.SUSS)){
                log.info("事务执行成功");
                example.setTransactionStatus(TransactionStatus.COMMIT_SUSS);
                endTransactionData.setTransactionStatus(cn.spider.framework.common.event.enums.TransactionStatus.SUSS);
                eventManager.sendMessage(EventType.END_TRANSACTION,endTransactionData);
            }else {
                example.setTransactionStatus(TransactionStatus.COMMIT_FAIL);
                example.recordFailNum();
                // 进行注册3秒一次进行retry->每隔十秒一次
                if (example.getFailNum() >= 10) {
                    log.error("执行错误");
                    commit(example, linkerService);
                    return;
                }
                example.setTransactionStatus(TransactionStatus.ROLL_BACK_FAIL);
                eventManager.sendMessage(EventType.END_TRANSACTION,endTransactionData);
            }
            checkTransactionIsFinish();
        }).onFailure(fail -> {
            log.error("transaction-BranchId {} xid {} 提交失败 {}",example.getBranchId(),example.getTransactionGroupId(), ExceptionMessage.getStackTrace(fail));
            example.setTransactionStatus(TransactionStatus.COMMIT_FAIL);
            example.recordFailNum();
            // 进行注册3秒一次进行retry->每隔十秒一次
            if (example.getFailNum() >= 10) {
                commit(example, linkerService);
                log.error("执行错误");
                return;
            }
            example.setTransactionStatus(TransactionStatus.ROLL_BACK_FAIL);
            eventManager.sendMessage(EventType.END_TRANSACTION,endTransactionData);
            checkTransactionIsFinish();
        });
    }

    private void checkTransactionIsFinish() {
        Optional<TransactionExample> transactionExample = transactionExampleList
                .stream()
                .filter(item -> item.getTransactionStatus().equals(TransactionStatus.INIT))
                .findFirst();
        if (transactionExample.isPresent()) {
            return;
        }
        List<TransactionOperateStatus> operateStatusList = transactionExampleList.stream().map(item -> {
            TransactionOperateStatus status = new TransactionOperateStatus();

            status.setTransactionStatus(item.getTransactionStatus());
            status.setTaskId(item.getTaskId());
            status.setRequestId(item.getRequestId());
            status.setTransactionGroupId(item.getTransactionGroupId());
            return status;
        }).collect(Collectors.toList());
        TransactionOperateResponse response = new TransactionOperateResponse();
        response.setGroupId(this.groupId);
        response.setOperateStatusList(operateStatusList);
        this.promise.complete(JsonObject.mapFrom(response));
    }

    public void rollBack(TransactionExample example, LinkerService linkerService) {

        LinkerServerRequest linkerServerRequest = buildRequestEntity(example, TransactionalType.ROLLBACK);
        JsonObject request = JsonObject.mapFrom(linkerServerRequest);
        Future<JsonObject> rollBackResult = linkerService.submittals(request);

        EndTransactionData endTransactionData = EndTransactionData.builder()
                .transactionGroupId(example.getTransactionGroupId())
                .requestId(example.getRequestId())
                .flowElementId(example.getTaskId())
                .branchId(example.getBranchId())
                .transactionOperate(TransactionType.ROLLBACK)
                .build();

        rollBackResult.onSuccess(suss -> {
            JsonObject result = suss;
            LinkerServerResponse responseNew = result.getJsonObject("data").mapTo(LinkerServerResponse.class);
            if(responseNew.getResultCode().equals(ResultCode.SUSS)){
                log.info("事务返回的结果成功");
                example.setTransactionStatus(TransactionStatus.ROLL_BACK_SUSS);
                endTransactionData.setTransactionStatus(cn.spider.framework.common.event.enums.TransactionStatus.SUSS);
                eventManager.sendMessage(EventType.END_TRANSACTION,endTransactionData);
            }else {
                example.setTransactionStatus(TransactionStatus.ROLL_BACK_FAIL);
                example.recordFailNum();
                // 进行注册3秒一次进行retry->每隔十秒一次
                if (example.getFailNum() >= 10) {
                   log.error("事务执行错误");
                    rollBack(example, linkerService);
                    return;
                }
                example.setTransactionStatus(TransactionStatus.ROLL_BACK_FAIL);
                eventManager.sendMessage(EventType.END_TRANSACTION,endTransactionData);
            }
            checkTransactionIsFinish();
        }).onFailure(fail -> {
            example.setTransactionStatus(TransactionStatus.ROLL_BACK_FAIL);
            example.recordFailNum();
            // 进行回滚重试
            if (example.getFailNum() >= 10) {
                rollBack(example, linkerService);
                return;
            }
            // 发送事务操作失败
            endTransactionData.setTransactionStatus(cn.spider.framework.common.event.enums.TransactionStatus.FAIL);
            eventManager.sendMessage(EventType.END_TRANSACTION,endTransactionData);
            checkTransactionIsFinish();
        });
    }

    private LinkerServerRequest buildRequestEntity(TransactionExample example, TransactionalType transactionalType) {
        // 参数中，移除末尾的 Promise<Object> promise
        LinkerServerRequest linkerServerRequest = new LinkerServerRequest();
        TransactionalRequest transactionalRequest = new TransactionalRequest();
        transactionalRequest.setTransactionId(example.getTransactionGroupId());
        transactionalRequest.setBranchId(example.getBranchId());
        transactionalRequest.setWorkerName(example.getWorkerName());
        transactionalRequest.setTransactionalType(transactionalType);
        linkerServerRequest.setExecutionType(ExecutionType.TRANSACTION);
        linkerServerRequest.setTransactionalRequest(transactionalRequest);
        return linkerServerRequest;
    }

}
