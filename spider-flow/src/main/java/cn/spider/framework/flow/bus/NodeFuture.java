package cn.spider.framework.flow.bus;

import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.bus
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-22  16:58
 * @Description: TODO
 * @Version: 1.0
 */
public class NodeFuture {
    /**
     * future 用作于节点执行后回调后的执行
     */
    private Future<Object> future;
    /**
     * 作用与回馈future的执行时机
     */
    private Promise<Object> promise;
    /**
     * 节点执行的唯一key
     */
    private String onlyKey;

    public NodeFuture(String key){
        this.onlyKey = key;
        this.promise = Promise.promise();
        this.future = this.promise.future();
    }

    public Future<Object> getFuture() {
        return future;
    }

    public void setFuture(Future<Object> future) {
        this.future = future;
    }

    public Promise<Object> getPromise() {
        return promise;
    }

    public void setPromise(Promise<Object> promise) {
        this.promise = promise;
    }

    public String getOnlyKey() {
        return onlyKey;
    }

    public void setOnlyKey(String onlyKey) {
        this.onlyKey = onlyKey;
    }
}
