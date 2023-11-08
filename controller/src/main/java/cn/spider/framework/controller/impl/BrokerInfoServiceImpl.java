package cn.spider.framework.controller.impl;

import cn.spider.framework.controller.broker.BrokerManager;
import cn.spider.framework.controller.sdk.data.QuerySpiderServerResult;
import cn.spider.framework.controller.sdk.data.SpiderServerInfo;
import cn.spider.framework.controller.sdk.interfaces.BrokerInfoService;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.impl
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-18  21:46
 * @Description: TODO
 * @Version: 1.0
 */
public class BrokerInfoServiceImpl implements BrokerInfoService {

    private BrokerManager brokerManager;

    public BrokerInfoServiceImpl(BrokerManager brokerManager) {
        this.brokerManager = brokerManager;
    }

    @Override
    public Future<JsonObject> queryBrokerInfo() {
        List<SpiderServerInfo> spiderServerInfos = brokerManager.queryBrokerInfo();
        QuerySpiderServerResult querySpiderServerResult = new QuerySpiderServerResult(spiderServerInfos);
        return Future.succeededFuture(JsonObject.mapFrom(querySpiderServerResult));
    }

}
