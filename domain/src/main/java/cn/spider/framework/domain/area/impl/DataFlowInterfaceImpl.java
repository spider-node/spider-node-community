package cn.spider.framework.domain.area.impl;

import cn.spider.framework.domain.area.flowdata.data.QueryFlowDataInfoParam;
import cn.spider.framework.domain.area.flowdata.data.QueryFlowDataParam;
import cn.spider.framework.domain.area.flowdata.data.QueryFlowDataResult;
import cn.spider.framework.domain.area.flowdata.data.UpdateFlowDataStatus;
import cn.spider.framework.domain.area.flowdata.entity.SpiderDataFlow;
import cn.spider.framework.domain.area.flowdata.service.ISpiderDataFlowService;
import cn.spider.framework.domain.sdk.interfaces.DataFlowInterface;
import com.alibaba.fastjson.JSONObject;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public class DataFlowInterfaceImpl implements DataFlowInterface {

    private ISpiderDataFlowService spiderDataFlowService;

    public DataFlowInterfaceImpl(ISpiderDataFlowService spiderDataFlowService) {
        this.spiderDataFlowService = spiderDataFlowService;
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


}
