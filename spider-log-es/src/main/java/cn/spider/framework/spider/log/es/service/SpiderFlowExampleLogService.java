package cn.spider.framework.spider.log.es.service;


import cn.spider.framework.log.sdk.data.QueryFlowExample;
import cn.spider.framework.log.sdk.data.QueryFlowExampleResponse;
import cn.spider.framework.spider.log.es.client.EsIndexTypeId;
import cn.spider.framework.spider.log.es.domain.SpiderFlowExampleLog;
import io.vertx.core.Future;

import java.util.List;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.spider.log.es.service
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-17  22:47
 * @Description: 实例日志操作类的接口类
 * @Version: 1.0
 */

public interface SpiderFlowExampleLogService {

    void upsetBatchFlowExampleLog(List<SpiderFlowExampleLog> logs);


    QueryFlowExampleResponse queryFlowExampleLog(QueryFlowExample queryFlowExample);

    void deleteIndex();

}
