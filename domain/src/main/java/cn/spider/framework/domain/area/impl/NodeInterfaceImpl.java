package cn.spider.framework.domain.area.impl;

import cn.spider.framework.domain.area.data.QueryParamConfigParam;
import cn.spider.framework.domain.area.node.NodeManger;
import cn.spider.framework.domain.area.node.data.CreateNodeModel;
import cn.spider.framework.domain.area.node.data.Node;
import cn.spider.framework.domain.area.node.data.QueryNodeParam;
import cn.spider.framework.domain.sdk.data.NodeParamConfigModel;
import cn.spider.framework.domain.sdk.data.NodeParamConfigResult;
import cn.spider.framework.domain.sdk.data.QueryBaseNodeParam;
import cn.spider.framework.domain.sdk.data.RefreshAreaParam;
import cn.spider.framework.domain.sdk.interfaces.NodeInterface;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
        CreateNodeModel model = JSON.parseObject(data.toString(), CreateNodeModel.class);
        // 进行拆分出参与入参
        Node node = JSON.parseObject(data.toString(), Node.class);
        if (Objects.nonNull(model.getMethodParam())) {
            if (model.getMethodParam().containsKey("param")) {
                JSONArray paramMapping = model.getMethodParam().getJSONObject("param").getJSONArray("fieldInjectDefList");
                JsonObject param = new JsonObject().put("nodeParamConfigs", paramMapping);
                node.setParamMapping(param);
            }
            if (model.getMethodParam().containsKey("result")) {
                JsonObject resultFinal = new JsonObject().put("nodeParamConfigs", model.getMethodParam().getJSONArray("result"));
                node.setResultMapping(resultFinal);
            }
        }
        return nodeManger.createNode(node);
    }

    @Override
    public Future<Void> updateNode(JsonObject data) {
        Node node = JSON.parseObject(data.toString(),Node.class);
        node.setParamMapping(data.getJsonObject("paramMapping"));
        node.setResultMapping(data.getJsonObject("resultMapping"));
        return nodeManger.updateNode(node);
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
            List<Node> nodes = suss;
            JsonArray array = new JsonArray();
            for (Node node : nodes) {
                JsonObject object = JsonObject.mapFrom(node);
                if(Objects.nonNull(node.getParamMapping())){
                    NodeParamConfigModel nodeParamConfigList = JSON.parseObject(node.getParamMapping().toString(), NodeParamConfigModel.class);
                    object.put("paramMapping",JsonObject.mapFrom(nodeParamConfigList));
                }
                if(Objects.nonNull(node.getResultMapping())){
                    NodeParamConfigModel nodeParamConfigList = JSON.parseObject(node.getResultMapping().toString(), NodeParamConfigModel.class);
                    object.put("resultMapping",JsonObject.mapFrom(nodeParamConfigList));
                }
                array.add(object);
            }
            promise.complete(new JsonObject().put("nodes", array));
        }).onFailure(fail -> {
            promise.fail(fail);
        });
        return promise.future();
    }

    /**
     * 根据 - 组件名称和服务名称查询节点信息
     *
     * @param data
     * @return
     */
    @Override
    public Future<JsonObject> queryBaseNodes(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        QueryBaseNodeParam param = data.mapTo(QueryBaseNodeParam.class);
        nodeManger.queryNodeByComTaskService(param.getTaskComponent(), param.getTaskService())
                .onSuccess(suss -> {
                    promise.complete(JsonObject.mapFrom(suss));
                }).onFailure(fail -> {
                    promise.fail(fail);
                });
        return promise.future();
    }


    /**
     * 刷新参数
     *
     * @param param
     * @return
     */
    @Override
    public Future<Void> refreshParam(JsonObject param) {
        try {
            RefreshAreaParam areaParam = JSON.parseObject(param.toString(), RefreshAreaParam.class);
            nodeManger.refreshNodeParam(areaParam);
            return Future.succeededFuture();
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
    }

    @Override
    public Future<JsonObject> queryParamConfig(JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();
        QueryBaseNodeParam params = param.mapTo(QueryBaseNodeParam.class);
        nodeManger.queryNodeByComTaskService(params.getTaskComponent(), params.getTaskService())
                .onSuccess(suss -> {
                    Node node = suss;
                    NodeParamConfigResult result = new NodeParamConfigResult();
                    if (Objects.nonNull(node.getParamMapping())) {
                        JsonObject paramConfig = node.getParamMapping();
                        result.setNodeParamConfigs(paramConfig.getMap().values());
                    }
                    if (Objects.nonNull(node.getResultMapping())) {
                        JsonObject resultConfig = node.getParamMapping();
                        result.setNodeResultConfigs(resultConfig.getMap().values());
                    }
                    promise.complete(JsonObject.mapFrom(result));
                }).onFailure(fail -> {
                    promise.fail(fail);
                });

        return promise.future();
    }
}
