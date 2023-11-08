package cn.spider.framework.transaction.server.impl;

import cn.spider.framework.linker.sdk.data.TransactionalType;
import cn.spider.framework.transaction.sdk.data.NotifyTranscriptsChange;
import cn.spider.framework.transaction.sdk.data.RegisterTransactionRequest;
import cn.spider.framework.transaction.sdk.data.RegisterTransactionResponse;
import cn.spider.framework.transaction.sdk.data.TransactionOperateRequest;
import cn.spider.framework.transaction.sdk.interfaces.TransactionInterface;
import cn.spider.framework.transaction.server.TransactionManager;
import cn.spider.framework.transaction.server.transcript.TranscriptManager;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.transaction.server.impl
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-07  18:18
 * @Description: 提供事务能力的实现类
 * @Version: 1.0
 */
public class TransactionInterfaceImpl implements TransactionInterface {

    private TransactionManager transactionManager;

    private TranscriptManager transcriptManager;

    public TransactionInterfaceImpl(TransactionManager transactionManager,TranscriptManager transcriptManager) {
        this.transactionManager = transactionManager;
        this.transcriptManager = transcriptManager;
    }

    @Override
    public Future<JsonObject> registerTransaction(JsonObject data) {
        RegisterTransactionRequest request = data.mapTo(RegisterTransactionRequest.class);
        RegisterTransactionResponse response = transactionManager.registerTransaction(request.getRequestId(),request.getGroupId(),request.getTaskId(),request.getWorkerName());
        return Future.succeededFuture(JsonObject.mapFrom(response));
    }

    /**
     * 只会给出 groupId,做整体提交
     * @param data
     * @return
     */
    @Override
    public Future<JsonObject> commit(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        TransactionOperateRequest request = data.mapTo(TransactionOperateRequest.class);
        transactionManager.transactionOperate(request.getGroupId(),promise, TransactionalType.SUBMIT);
        return promise.future();
    }

    /**
     * 只会给出 groupId,做出整体回滚
     * @param data
     * @return
     */
    @Override
    public Future<JsonObject> rollBack(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        TransactionOperateRequest request = data.mapTo(TransactionOperateRequest.class);
        this.transactionManager.transactionOperate(request.getGroupId(),promise, TransactionalType.ROLLBACK);
        return promise.future();
    }

    @Override
    public Future<Void> replaceTranscripts(JsonObject data) {
        NotifyTranscriptsChange notifyTranscriptsChange = data.mapTo(NotifyTranscriptsChange.class);
        transcriptManager.replace(notifyTranscriptsChange.getTranscript());
        return Future.succeededFuture();
    }
}
