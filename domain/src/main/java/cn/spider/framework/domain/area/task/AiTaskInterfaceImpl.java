package cn.spider.framework.domain.area.task;

import cn.spider.framework.domain.sdk.interfaces.AiTaskInterface;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

@Slf4j
public class AiTaskInterfaceImpl implements AiTaskInterface {

    private TaskManager taskManager;

    private Executor spiderBusinessPool;

    public AiTaskInterfaceImpl(TaskManager taskManager, Executor spiderBusinessPool) {
        this.taskManager = taskManager;
        this.spiderBusinessPool = spiderBusinessPool;
    }

    @Override
    public Future<Void> createCoder(JsonObject param) {
        Promise<Void> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            try {
                String versionId = param.getString("domainFunctionVersionId");
                taskManager.runDomainFunctionTask(versionId);
                promise.complete();
            } catch (Exception e) {
                promise.fail(e);
                log.error("createCoder error", e);
            }
        });
        return promise.future();
    }
}
