package cn.spider.framework.spider.log.es.service.impl;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.log.sdk.data.FlowExample;
import cn.spider.framework.log.sdk.data.QueryFlowExample;
import cn.spider.framework.log.sdk.data.QueryFlowExampleResponse;
import cn.spider.framework.spider.log.es.domain.SpiderFlowExampleLog;
import cn.spider.framework.spider.log.es.esx.EsContext;
import cn.spider.framework.spider.log.es.esx.EsQuery;
import cn.spider.framework.spider.log.es.esx.model.EsData;
import cn.spider.framework.spider.log.es.service.SpiderFlowExampleLogService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class SpiderFlowExampleLogServiceEsXImpl implements SpiderFlowExampleLogService {

    private EsContext esContext;

    private final String index = "bms-spider-log-v7";

    private final String createIndexJson = "{\"mappings\":{\"properties\":{\"id\":{\"type\":\"keyword\"},\"requestParam\":{\"type\":\"text\"},\"returnParam\":{\"type\":\"text\"},\"brokerName\":{\"type\":\"keyword\"},\"status\":{\"type\":\"keyword\"},\"exception\":{\"type\":\"keyword\"},\"transactionStatus\":{\"type\":\"keyword\"},\"functionName\":{\"type\":\"keyword\"},\"functionId\":{\"type\":\"keyword\"},\"startTime\":{\"type\":\"long\"},\"endTime\":{\"type\":\"long\"},\"takeTime\":{\"type\":\"long\"}}}}";

    public SpiderFlowExampleLogServiceEsXImpl(EsContext esContext) {
        try {
            this.esContext = esContext;
            if (this.esContext.indiceExist(index)) {
                return;
            }
            esContext.indiceCreate(index, createIndexJson);
        } catch (IOException e) {
            log.info("create_index {}", ExceptionMessage.getStackTrace(e));
        }
    }

    @Override
    public void upsetBatchFlowExampleLog(List<SpiderFlowExampleLog> logs) {
        if (CollectionUtils.isEmpty(logs)) {
            return;
        }

        try {
            Map<String, List<SpiderFlowExampleLog>> flowExampleLogMap = logs.stream().collect(Collectors.groupingBy(SpiderFlowExampleLog::getId));

            EsData<SpiderFlowExampleLog> result = esContext.indice(index)
                    .where(c -> c.useScore().terms("id", flowExampleLogMap.keySet()))
                    .limit(700)
                    .selectList(SpiderFlowExampleLog.class);

            Map<String, SpiderFlowExampleLog> spiderFlowExampleLogMap = result.getList().stream().collect(Collectors.toMap(SpiderFlowExampleLog::getId, Function.identity(), (v1, v2) -> v2));

            List<SpiderFlowExampleLog> insert = Lists.newArrayList();

            Map<String, Object> updateMap = new HashMap<>();

            for (String key : flowExampleLogMap.keySet()) {
                Map<String, Object> spiderFlowMap = Maps.newHashMap();
                if (spiderFlowExampleLogMap.containsKey(key)) {
                    SpiderFlowExampleLog spiderFlowExampleLog = spiderFlowExampleLogMap.get(key);

                    Map<String, Object> ben2Map =
                            JSON.parseObject(JSON.toJSONString(spiderFlowExampleLog), Map.class);
                    spiderFlowMap.putAll(ben2Map);
                }
                List<SpiderFlowExampleLog> spiderFlowExampleLogs = flowExampleLogMap.get(key);
                for (SpiderFlowExampleLog spiderFlowExampleLog : spiderFlowExampleLogs) {
                    Map<String, Object> ben2Map =
                            JSON.parseObject(JSON.toJSONString(spiderFlowExampleLog), Map.class);
                    spiderFlowMap.putAll(ben2Map);
                }
                if (spiderFlowMap.containsKey("startTime") && spiderFlowMap.containsKey("endTime")) {
                    Long startTime = (Long) spiderFlowMap.get("startTime");
                    Long endTime = (Long) spiderFlowMap.get("endTime");
                    spiderFlowMap.put("takeTime", endTime - startTime);
                }
                if(spiderFlowExampleLogMap.containsKey(key)){
                    updateMap.putAll(spiderFlowMap);
                }else {
                    insert.add(JSON.parseObject(JSON.toJSONString(spiderFlowMap), SpiderFlowExampleLog.class));
                }
            }
            if(!updateMap.isEmpty()){
                esContext.indice(index).upsertList(updateMap);
            }
            if(CollectionUtils.isNotEmpty(insert)){
                esContext.indice(index).insertList(insert);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public QueryFlowExampleResponse queryFlowExampleLog(QueryFlowExample queryFlowExample) {
        QueryFlowExampleResponse response = new QueryFlowExampleResponse();
        try {
            Integer start = (queryFlowExample.getPage() - 1) * queryFlowExample.getSize();
            EsData<FlowExample> result = buildEsQuery(queryFlowExample)
                    .minScore(1)
                    .andByDesc("startTime")
                    .limit(start, queryFlowExample.getSize())
                    .selectList(FlowExample.class);
            List<FlowExample> flowExampleList = result.getList();
            response.setFlowExampleList(flowExampleList);
            response.setTotal(result.getTotal());
        } catch (IOException e) {
            response.setTotal(0);
            log.info("查询.queryFlowExampleLog {}", ExceptionMessage.getStackTrace(e));
        }
        return response;
    }

    @Override
    public void deleteIndex() {

    }

    private EsQuery buildEsQuery(QueryFlowExample queryFlowExample) {

        if (Objects.isNull(queryFlowExample.getStartTime())) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime before = now.plusDays(-10);
            queryFlowExample.setStartTime(Timestamp.valueOf(before).getTime());
        }

        return esContext.indice(index)
                .where(c -> c.useScore()
                        .must()
                        .matchIf(StringUtils.isNotEmpty(queryFlowExample.getBusinessParam()), "requestParam", queryFlowExample.getBusinessParam())
                        .termIf(StringUtils.isNotEmpty(queryFlowExample.getId()), "id", queryFlowExample.getId())
                        .termIf(StringUtils.isNotEmpty(queryFlowExample.getFunctionName()), "functionName", queryFlowExample.getFunctionName())
                        .termIf(StringUtils.isNotEmpty(queryFlowExample.getFunctionId()), "functionId", queryFlowExample.getFunctionId())
                        .rangeIf(Objects.nonNull(queryFlowExample.getStartTime()), "startTime", t -> t.gt(queryFlowExample.getStartTime()))
                        .rangeIf(Objects.nonNull(queryFlowExample.getEndTime()), "startTime", t -> t.lt(queryFlowExample.getEndTime()))
                        .rangeIf(Objects.nonNull(queryFlowExample.getGtTakeTime()), "takeTime", t -> t.gt(queryFlowExample.getGtTakeTime()))
                        .rangeIf(Objects.nonNull(queryFlowExample.getLtTakeTime()), "takeTime", t -> t.lt(queryFlowExample.getLtTakeTime()))
                        .rangeIf(Objects.isNull(queryFlowExample.getStartTime()), "startTime", t -> t.lt(System.currentTimeMillis()))
                );

    }

}
