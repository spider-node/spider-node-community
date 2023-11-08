package cn.spider.framework.spider.log.es.service;

import cn.spider.framework.log.sdk.data.QueryFlowElementExample;
import cn.spider.framework.log.sdk.data.QueryFlowElementExampleResponse;
import cn.spider.framework.spider.log.es.client.EsIndexTypeId;
import cn.spider.framework.spider.log.es.domain.SpiderFlowElementExampleLog;
import io.vertx.core.Future;

import java.util.List;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.spider.log.es.service
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-18  17:10
 * @Description: 流程实例的节点
 * @Version: 1.0
 */
public interface SpiderFlowElementExampleService {

    void upsertBatchFlowElementExampleLog(List<SpiderFlowElementExampleLog> logs);

    QueryFlowElementExampleResponse queryFlowElementExampleLog(QueryFlowElementExample queryFlowElementExample);

    void deleteIndex();


}
