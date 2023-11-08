package cn.spider.framework.flow.bus;

import io.vertx.core.Future;
import io.vertx.core.Promise;

import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.bus
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-22  18:48
 * @Description: TODO
 * @Version: 1.0
 */
public class SpiderFuture {
    private String startEventId;

    private Promise<Void> promise;

    private Future<Void> future;

    private Boolean isSub;

    private Map<String,NodeFuture> nodeFutureMap;

    public SpiderFuture(String startEventId, Boolean isSub) {
        this.startEventId = startEventId;
        this.isSub = isSub;
        this.promise = Promise.promise();
        this.future = this.promise.future();
        this.nodeFutureMap = new HashMap<>();
    }


    public String getStartEventId() {
        return startEventId;
    }

    public void setStartEventId(String startEventId) {
        this.startEventId = startEventId;
    }

    public Promise<Void> getPromise() {
        return promise;
    }

    public void setPromise(Promise<Void> promise) {
        this.promise = promise;
    }

    public Future<Void> getFuture() {
        return future;
    }

    public void setFuture(Future<Void> future) {
        this.future = future;
    }

    public Boolean getSub() {
        return isSub;
    }

    public void setSub(Boolean sub) {
        isSub = sub;
    }

    public Map<String, NodeFuture> getNodeFutureMap() {
        return nodeFutureMap;
    }

    public void setNodeFutureMap(Map<String, NodeFuture> nodeFutureMap) {
        this.nodeFutureMap = nodeFutureMap;
    }
}
