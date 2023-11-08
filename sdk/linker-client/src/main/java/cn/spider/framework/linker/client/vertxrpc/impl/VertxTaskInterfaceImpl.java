package cn.spider.framework.linker.client.vertxrpc.impl;

import cn.spider.framework.linker.client.task.TaskManager;
import cn.spider.framework.linker.sdk.data.ExecutionType;
import cn.spider.framework.linker.sdk.data.LinkerServerRequest;
import cn.spider.framework.linker.sdk.interfaces.VertxRpcTaskInterface;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.linker.client.vertxrpc.impl
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-16  16:54
 * @Description: rpc处理业务请求类
 * @Version: 1.0
 */

public class VertxTaskInterfaceImpl implements VertxRpcTaskInterface {
    private TaskManager taskManager;

    public VertxTaskInterfaceImpl(TaskManager taskManager){
        this.taskManager = taskManager;
    }

    @Override
    public Future<JsonObject> run(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        LinkerServerRequest request = JSON.parseObject(data.toString(),LinkerServerRequest.class);
        request.setExecutionType(ExecutionType.FUNCTION);
        taskManager.runVertxRpc(request,promise);
        return promise.future();
    }

    @Override
    public Future<JsonObject> transactionOperate(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        LinkerServerRequest request = JSON.parseObject(data.toString(),LinkerServerRequest.class);
        request.setExecutionType(ExecutionType.TRANSACTION);
        taskManager.runVertxRpc(request,promise);
        return promise.future();
    }
}
