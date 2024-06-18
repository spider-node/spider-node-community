package cn.spider.framework.spider.log.es.service.impl;

import cn.spider.framework.log.sdk.data.QueryFlowElementExample;
import cn.spider.framework.log.sdk.data.QueryFlowElementExampleResponse;
import cn.spider.framework.log.sdk.data.QueryFlowExample;
import cn.spider.framework.log.sdk.data.QueryFlowExampleResponse;
import cn.spider.framework.log.sdk.interfaces.LogInterface;
import cn.spider.framework.spider.log.es.service.SpiderFlowElementExampleService;
import cn.spider.framework.spider.log.es.service.SpiderFlowExampleLogService;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.spider.log.es.service.impl
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-07  13:34
 * @Description: TODO
 * @Version: 1.0
 */

public class LogInterfaceImpl implements LogInterface {

    private SpiderFlowElementExampleService elementExampleService;

    private SpiderFlowExampleLogService spiderFlowExampleLogService;

    public LogInterfaceImpl(SpiderFlowElementExampleService elementExampleService, SpiderFlowExampleLogService spiderFlowExampleLogService) {
        this.elementExampleService = elementExampleService;
        this.spiderFlowExampleLogService = spiderFlowExampleLogService;
    }

    @Override
    public Future<JsonObject> queryFlowExample(JsonObject param) {
        QueryFlowExample queryFlowExample = param.mapTo(QueryFlowExample.class);
        if(queryFlowExample.getSize() == 0){
            queryFlowExample.setPage(1);
            queryFlowExample.setSize(10);
        }
        QueryFlowExampleResponse queryFlowExampleResponse = spiderFlowExampleLogService.queryFlowExampleLog(queryFlowExample);
        return Future.succeededFuture(JsonObject.mapFrom(queryFlowExampleResponse));
    }

    @Override
    public Future<JsonObject> queryElementExample(JsonObject param) {
        QueryFlowElementExample queryFlowElementExample = param.mapTo(QueryFlowElementExample.class);
        if(queryFlowElementExample.getPage() == 0){
            queryFlowElementExample.setPage(1);
            queryFlowElementExample.setSize(10);
        }
        QueryFlowElementExampleResponse response = elementExampleService.queryFlowElementExampleLog(queryFlowElementExample);
        return Future.succeededFuture(JsonObject.mapFrom(response));
    }
}
