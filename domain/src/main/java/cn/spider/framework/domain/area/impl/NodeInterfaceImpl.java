package cn.spider.framework.domain.area.impl;

import cn.spider.framework.domain.area.node.NodeManger;
import cn.spider.framework.domain.area.node.data.Node;
import cn.spider.framework.domain.area.node.data.QueryNodeParam;
import cn.spider.framework.domain.sdk.interfaces.NodeInterface;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.impl
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-27  12:52
 * @Description: 节点 - 实现类
 * @Version: 1.0
 */

public class NodeInterfaceImpl implements NodeInterface {

    private NodeManger nodeManger;

    public NodeInterfaceImpl(NodeManger nodeManger) {
        this.nodeManger = nodeManger;
    }

    @Override
    public Future<Void> insertNode(JsonObject data) {
        return nodeManger.createNode(data.mapTo(Node.class));
    }

    @Override
    public Future<Void> updateNode(JsonObject data) {
        return nodeManger.updateNode(data.mapTo(Node.class));
    }

    @Override
    public Future<Void> distributeNode(JsonObject data) {

        return null;
    }

    @Override
    public Future<Void> updateParam(JsonObject data) {

        return null;
    }

    @Override
    public Future<JsonObject> queryJsonObject(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        nodeManger.queryNode(data.mapTo(QueryNodeParam.class)).onSuccess(suss -> {
            promise.complete(new JsonObject().put("nodes",new JsonArray(JSON.toJSONString(suss))));
        }).onFailure(fail -> {
            promise.fail(fail);
        });
        return promise.future();
    }
}
