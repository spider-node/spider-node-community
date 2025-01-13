package cn.spider.framework.domain.area.impl;

import cn.spider.framework.domain.area.agent.AgentVertxClient;
import cn.spider.framework.domain.area.data.AnalysisDataFowModel;
import cn.spider.framework.domain.area.flowdata.data.QueryFlowDataInfoParam;
import cn.spider.framework.domain.area.flowdata.data.QueryFlowDataParam;
import cn.spider.framework.domain.area.flowdata.data.QueryFlowDataResult;
import cn.spider.framework.domain.area.flowdata.data.UpdateFlowDataStatus;
import cn.spider.framework.domain.area.flowdata.entity.SpiderDataFlow;
import cn.spider.framework.domain.area.flowdata.service.ISpiderDataFlowService;
import cn.spider.framework.domain.area.task.TaskManager;
import cn.spider.framework.domain.sdk.data.DataFlowAnalysisModel;
import cn.spider.framework.domain.sdk.interfaces.DataFlowInterface;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class DataFlowInterfaceImpl implements DataFlowInterface {

    private ISpiderDataFlowService spiderDataFlowService;

    private AgentVertxClient agentVertxClient;

    private TaskManager taskManager;

    public DataFlowInterfaceImpl(ISpiderDataFlowService spiderDataFlowService,AgentVertxClient agentVertxClient, TaskManager taskManager) {
        this.spiderDataFlowService = spiderDataFlowService;
        this.agentVertxClient = agentVertxClient;
        this.taskManager = taskManager;
    }

    @Override
    public Future<JsonObject> queryDataFlow(JsonObject param) {
        QueryFlowDataParam queryFlowDataParam = param.mapTo(QueryFlowDataParam.class);
        QueryFlowDataResult queryFlowDataResult = spiderDataFlowService.querySpiderDataFlow(queryFlowDataParam);
        return Future.succeededFuture(JsonObject.mapFrom(queryFlowDataResult));
    }
    @Override
    public Future<JsonObject> queryDataFlowInfos(JsonObject param) {
        QueryFlowDataInfoParam queryFlowDataInfoParam = param.mapTo(QueryFlowDataInfoParam.class);
        SpiderDataFlow spiderDataFlow = spiderDataFlowService.lambdaQuery().eq(SpiderDataFlow :: getId,queryFlowDataInfoParam.getId()).one();
        return Future.succeededFuture(JsonObject.mapFrom(spiderDataFlow));
    }

    @Override
    public Future<Void> upsertDataFlow(JsonObject param) {
        SpiderDataFlow spiderDataFlow = JSONObject.parseObject(param.toString(),SpiderDataFlow.class);
        spiderDataFlowService.upsetFlowData(spiderDataFlow);
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> upsertDataFlowStatus(JsonObject param) {
        UpdateFlowDataStatus updateFlowDataStatus = param.mapTo(UpdateFlowDataStatus.class);
        spiderDataFlowService.lambdaUpdate()
                .set(SpiderDataFlow :: getStatus,updateFlowDataStatus.getStatus())
                .eq(SpiderDataFlow :: getId,updateFlowDataStatus.getId())
                .update();
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> upsertDataFlowParse(JsonObject param) {
        DataFlowAnalysisModel dataFlowAnalysisModel = param.mapTo(DataFlowAnalysisModel.class);
        spiderDataFlowService.lambdaUpdate()
                .set(SpiderDataFlow :: getDataFlowAnalysisModel,param.toString())
                .eq(SpiderDataFlow :: getId,dataFlowAnalysisModel.getFlowDataId())
                .update();
        return Future.succeededFuture();
    }

    /**
     * 请求ai应用，数据流，领域信息进行解析
     * @param param
     * @return
     */
    @Override
    public Future<Void> generateDataFlowInfo(JsonObject param) {
        SpiderDataFlow flow = spiderDataFlowService.getById(param.getInteger("flowId"));
        List<Integer> baseIds = JSONObject.parseArray(flow.getSonAreaIds(),Integer.class);
        List<JsonObject> domainInfo = taskManager.buildDomainInfo(Sets.newHashSet(baseIds));
        JSONObject dataFlow = flow.getData();
        AnalysisDataFowModel analysisDataFowModel = new AnalysisDataFowModel(flow.getId(), domainInfo, dataFlow);
        analysisDataFowModel.setDataFlowDesc(flow.getFlowDataDesc());
        agentVertxClient.analysisDataFlow(JsonObject.mapFrom(analysisDataFowModel));
        return Future.succeededFuture();
    }
}
