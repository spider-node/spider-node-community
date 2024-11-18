package cn.spider.framework.domain.area.plugin;

import cn.spider.framework.domain.area.AreaManger;
import cn.spider.framework.domain.area.agent.AgentVertxClient;
import cn.spider.framework.domain.area.data.AreaModel;
import cn.spider.framework.domain.area.data.QueryAreaModel;
import cn.spider.node.framework.code.agent.sdk.data.AreaDocInfo;
import cn.spider.node.framework.code.agent.sdk.data.AreaInfo;
import cn.spider.node.framework.code.agent.sdk.data.InitAreaBaseResult;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Set;

@Slf4j
public class ApplicationPluginManager {
    // 加载领域
    private AgentVertxClient agentClient;

    private AreaManger areaManger;


    public ApplicationPluginManager(AgentVertxClient agentClient, AreaManger areaManger) {
        this.areaManger = areaManger;
        this.agentClient = agentClient;
    }

    /**
     * 查询 领域参数
     *
     * @return 领域的所有信息
     */
    public Future<JsonObject> queryAllAreaInfo() {
        Promise<JsonObject> promise = Promise.promise();
        QueryAreaModel queryAreaModel = new QueryAreaModel();
        queryAreaModel.setPage(1);
        queryAreaModel.setSize(100);
        areaManger.queryAreaModel(queryAreaModel).onSuccess(suss -> {
            List<AreaModel> areaModels = suss;
            agentClient.queryAllArea(new JsonObject()).onSuccess(areaInfoSuss -> {
                AreaDocInfo areaDocInfo = JSON.parseObject(areaInfoSuss.toString(), AreaDocInfo.class);
                if (CollectionUtils.isNotEmpty(areaModels)) {
                    List<AreaInfo> areaInfos = JSON.parseArray(JSON.toJSONString(areaModels), AreaInfo.class);
                    areaDocInfo.setAreaInfos(areaInfos);
                }
                promise.complete(JsonObject.mapFrom(areaDocInfo));

            }).onFailure(fail -> {
                promise.fail(fail);
            });
        }).onFailure(fail -> {
            promise.fail(fail);
        });
        return promise.future();
    }

    public Future<Void> initAiRag() {
        Promise<Void> promise = Promise.promise();
        Future<JsonObject> areaInfoFuture = queryAllAreaInfo();
        areaInfoFuture.onSuccess(areaInfoSuss -> {
            agentClient.initAiRag(areaInfoSuss).onSuccess(initSuss -> {
                promise.complete();
            }).onFailure(initFail -> {
                promise.fail(initFail);
            });
        }).onFailure(areaInfoFail -> {
            promise.fail(areaInfoFail);
        });
        return promise.future();
    }

    /**
     * 构建插件
     * 构建完成后，发起部署
     *
     * @param param 构建插件参数
     * @return 返回构建插件后的参数 包含，biz version等信息,
     */
    public Future<JsonObject> buildPlugin(JsonObject param) {
        return agentClient.buildPlugin(param);
    }

    /**
     * 初始化 子域表的新增与初始化
     *
     * @param param
     * @return 执行后的future
     */
    public Future<Void> initAreaBase(JsonObject param) {
        Promise<Void> promise = Promise.promise();
        agentClient.initAreaBase(param).onSuccess(initSuss -> {
            log.info("接口返回的数据 {}", initSuss.toString());
            AreaDocInfo areaDocInfo = new AreaDocInfo();
            InitAreaBaseResult initAreaBaseInfo = JSON.parseObject(initSuss.toString(), InitAreaBaseResult.class);
            areaDocInfo.setSonAreaInfos(Lists.newArrayList(initAreaBaseInfo.getSonArea()));
            areaDocInfo.setSonAreaCodeBases(Lists.newArrayList(initAreaBaseInfo.getAreaDomainInfo()));
            log.info("初始化后产生的数据 {}", JSON.toJSONString(areaDocInfo));
            promise.complete();
        }).onFailure(initFail -> {
            promise.fail(initFail);
        });
        return promise.future();
    }

    public Future<JsonObject> querySonBaseInfo(JsonObject param) {
        return agentClient.querySonBaseInfo(param);
    }

    public Future<Void> installPlugin(Set<String> applicationIps, JsonObject pluginParam) {
        return agentClient.installPlugin(applicationIps, pluginParam);
    }

    public Future<Void> unInstall(Set<String> applicationIps, JsonObject pluginParam) {
        return agentClient.unInstall(applicationIps, pluginParam);
    }
}
