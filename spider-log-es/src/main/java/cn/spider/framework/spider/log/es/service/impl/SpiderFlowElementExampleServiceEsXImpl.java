package cn.spider.framework.spider.log.es.service.impl;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.log.sdk.data.FlowElementExample;
import cn.spider.framework.log.sdk.data.QueryFlowElementExample;
import cn.spider.framework.log.sdk.data.QueryFlowElementExampleResponse;
import cn.spider.framework.spider.log.es.domain.SpiderFlowElementExampleLog;
import cn.spider.framework.spider.log.es.esx.EsContext;
import cn.spider.framework.spider.log.es.esx.EsQuery;
import cn.spider.framework.spider.log.es.esx.model.EsData;
import cn.spider.framework.spider.log.es.service.SpiderFlowElementExampleService;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class SpiderFlowElementExampleServiceEsXImpl implements SpiderFlowElementExampleService {

    private EsContext esContext;

    private final String index = "bms-spider-element-v7";

    private final String createIndexJson = "{\"mappings\":{\"properties\":{\"id\":{\"type\":\"keyword\"},\"requestId\":{\"type\":\"keyword\"},\"flowElementName\":{\"type\":\"keyword\"},\"flowElementId\":{\"type\":\"keyword\"},\"functionId\":{\"type\":\"keyword\"},\"requestParam\":{\"type\":\"text\"},\"functionName\":{\"type\":\"keyword\"},\"returnParam\":{\"type\":\"text\"},\"exception\":{\"type\":\"keyword\"},\"status\":{\"type\":\"keyword\"},\"startTime\":{\"type\":\"long\"},\"endTime\":{\"type\":\"long\"},\"finalEndTime\":{\"type\":\"long\"},\"transactionGroupId\":{\"type\":\"keyword\"},\"branchId\":{\"type\":\"keyword\"},\"transactionStatus\":{\"type\":\"keyword\"},\"transactionOperate\":{\"type\":\"keyword\"}}}}";

    public SpiderFlowElementExampleServiceEsXImpl(EsContext esContext) {
        try {
            this.esContext = esContext;
            if (this.esContext.indiceExist(index)) {
                return;
            }
            // 构建对于的索引
            this.esContext.indiceCreate(index,createIndexJson);
        } catch (IOException e) {
            log.info("create_index_fail {}",ExceptionMessage.getStackTrace(e));
        }

    }

    @Override
    public void upsertBatchFlowElementExampleLog(List<SpiderFlowElementExampleLog> logs) {
        if (CollectionUtils.isEmpty(logs)) {
            return;
        }
        Map<String, List<SpiderFlowElementExampleLog>> flowElementMap = logs.stream().collect(Collectors.groupingBy(SpiderFlowElementExampleLog::getId));
        List<SpiderFlowElementExampleLog> logsNew = Lists.newArrayList();

        try {
            EsData<SpiderFlowElementExampleLog> result = esContext.indice(index)
                    .where(c -> c.useScore().terms("id", flowElementMap.keySet()))
                    .limit(500)
                    .selectList(SpiderFlowElementExampleLog.class);
            List<SpiderFlowElementExampleLog> logsNews = result.getList();
            Map<String, SpiderFlowElementExampleLog> flowElementsMap = logsNews.stream().collect(Collectors.toMap(SpiderFlowElementExampleLog::getId, Function.identity(), (v1, v2) -> v2));

            for (String key : flowElementMap.keySet()) {

                Map<String, Object> spiderFlowMap = Maps.newHashMap();
                if (flowElementsMap.containsKey(key)) {
                    SpiderFlowElementExampleLog elementExampleLog = flowElementsMap.get(key);
                    Map<String, Object> ben2Map =
                            JSON.parseObject(JSON.toJSONString(elementExampleLog), Map.class);
                    spiderFlowMap.putAll(ben2Map);
                }
                List<SpiderFlowElementExampleLog> logList = flowElementMap.get(key);
                for (SpiderFlowElementExampleLog logNew : logList) {
                    Map<String, Object> ben2Map =
                            JSON.parseObject(JSON.toJSONString(logNew), Map.class);
                    spiderFlowMap.putAll(ben2Map);
                }

                if(spiderFlowMap.containsKey("startTime") && spiderFlowMap.containsKey("endTime")){
                    Long startTime = (Long) spiderFlowMap.get("startTime");
                    Long endTime = (Long) spiderFlowMap.get("endTime");
                    spiderFlowMap.put("finalEndTime",endTime - startTime);
                }
                // 设置执行时间差
                logsNew.add(JSON.parseObject(JSON.toJSONString(spiderFlowMap), SpiderFlowElementExampleLog.class));
            }
            esContext.indice(index).insertList(logsNew);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public QueryFlowElementExampleResponse queryFlowElementExampleLog(QueryFlowElementExample queryFlowElementExample) {
        QueryFlowElementExampleResponse response = new QueryFlowElementExampleResponse();
        try {
            Integer start = (queryFlowElementExample.getPage() -1) * queryFlowElementExample.getSize();
            EsQuery query = buildQueryEsQuery(queryFlowElementExample);
            EsData<FlowElementExample> result = query
                    .andByAsc("startTime")
                    .minScore(1)
                    .limit(start, queryFlowElementExample.getSize())
                    .selectList(FlowElementExample.class);
            List<FlowElementExample> flowElementExamples = result.getList();
            for(FlowElementExample flowElementExample : flowElementExamples){
                flowElementExample.setTakeTime(flowElementExample.getFinalEndTime());
            }
            response.setElementExampleList(result.getList());
            response.setTotal(result.getTotal());
        } catch (IOException e) {
            response.setTotal(0);
            log.info("查询es失败-fail {}", ExceptionMessage.getStackTrace(e));
        }
        return response;
    }

    @Override
    public void deleteIndex() {

    }

    private EsQuery buildQueryEsQuery(QueryFlowElementExample queryFlowElementExample) {

        if(Objects.isNull(queryFlowElementExample.getStartTime())){
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime before = now.plusDays(-10);
            queryFlowElementExample.setStartTime(Timestamp.valueOf(before).getTime());
        }

        return esContext.indice(index)
                .where(c -> c.useScore()
                        .must()
                        .rangeIf(Objects.nonNull(queryFlowElementExample.getStartTime()),"startTime", t -> t.gt(queryFlowElementExample.getStartTime()))
                        .termIf(StringUtils.isNotEmpty(queryFlowElementExample.getRequestId()), "requestId", queryFlowElementExample.getRequestId())
                        .matchIf(StringUtils.isNotEmpty(queryFlowElementExample.getRequestParam()), "requestParam", queryFlowElementExample.getRequestParam())
                        .matchIf(StringUtils.isNotEmpty(queryFlowElementExample.getReturnParam()), "returnParam", queryFlowElementExample.getReturnParam())
                        .termIf(StringUtils.isNotEmpty(queryFlowElementExample.getFunctionName()), "functionName", queryFlowElementExample.getFunctionName())
                        .termIf(StringUtils.isNotEmpty(queryFlowElementExample.getFunctionId()), "functionId", queryFlowElementExample.getFunctionId())
                        .rangeIf(Objects.nonNull(queryFlowElementExample.getGtTakeTime()), "takeTime", t -> t.gt(queryFlowElementExample.getGtTakeTime()))
                        .rangeIf(Objects.nonNull(queryFlowElementExample.getLtTakeTime()), "takeTime", t -> t.lt(queryFlowElementExample.getLtTakeTime()))
                );
    }

}
