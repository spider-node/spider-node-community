package cn.spider.framework.spider.log.es.service.impl;

import cn.spider.framework.log.sdk.data.FlowExample;
import cn.spider.framework.log.sdk.data.QueryFlowExample;
import cn.spider.framework.log.sdk.data.QueryFlowExampleResponse;
import cn.spider.framework.spider.log.es.dao.SpiderFlowExampleLogDao;
import cn.spider.framework.spider.log.es.domain.SpiderFlowExampleLog;
import cn.spider.framework.spider.log.es.service.SpiderFlowExampleLogService;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.spider.log.es.service.impl
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-18  13:10
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
public class SpiderFlowExampleLogServiceEsImpl implements SpiderFlowExampleLogService {

    private ElasticsearchRestTemplate template;

    private SpiderFlowExampleLogDao spiderFlowExampleLogDao;

    public SpiderFlowExampleLogServiceEsImpl(ElasticsearchRestTemplate template, SpiderFlowExampleLogDao spiderFlowExampleLogDao) {
        this.template = template;
        this.spiderFlowExampleLogDao = spiderFlowExampleLogDao;
    }

    @PostConstruct
    public void init() {
        template.createIndex(SpiderFlowExampleLog.class);
    }

    /**
     * 批量新增
     *
     * @param logs
     */
    public void upsetBatchFlowExampleLog(List<SpiderFlowExampleLog> logs) {
        if (CollectionUtils.isEmpty(logs)) {
            return;
        }
        Map<String, List<SpiderFlowExampleLog>> flowExampleLogMap = logs.stream().collect(Collectors.groupingBy(SpiderFlowExampleLog::getId));
        List<SpiderFlowExampleLog> logsNew = Lists.newArrayList();
        for (String key : flowExampleLogMap.keySet()) {
            Map<String, Object> spiderFlowMap = Maps.newHashMap();
            List<SpiderFlowExampleLog> spiderFlowExampleLogs = flowExampleLogMap.get(key);
            for (SpiderFlowExampleLog spiderFlowExampleLog : spiderFlowExampleLogs) {
                Map<String, Object> ben2Map =
                        JSON.parseObject(JSON.toJSONString(spiderFlowExampleLog), Map.class);
                spiderFlowMap.putAll(ben2Map);
            }
            logsNew.add(JSON.parseObject(JSON.toJSONString(spiderFlowMap), SpiderFlowExampleLog.class));
        }
        spiderFlowExampleLogDao.saveAll(logsNew);
    }


    public QueryFlowExampleResponse queryFlowExampleLog(QueryFlowExample queryFlowExample) {
        QueryFlowExampleResponse response = new QueryFlowExampleResponse();
        Pageable pageable = PageRequest.of(queryFlowExample.getPage()-1, queryFlowExample.getSize(), Sort.Direction.DESC, "startTime");
        Page<SpiderFlowExampleLog> flowElementExamplePage = spiderFlowExampleLogDao.search(buildFlowExample(queryFlowExample), pageable);
        List<SpiderFlowExampleLog> flowExampleLogs = flowElementExamplePage.getContent();
        List<FlowExample> flowExampleList = flowExampleLogs.stream().map(item -> {
            FlowExample flowExample = new FlowExample();
            BeanUtils.copyProperties(item, flowExample);
            if(Objects.isNull(flowExample.getStartTime()) || Objects.isNull(flowExample.getEndTime())){
                return flowExample;
            }
            flowExample.setTakeTime(flowExample.getEndTime() - flowExample.getStartTime());
            return flowExample;
        }).collect(Collectors.toList());
        response.setTotal(flowElementExamplePage.getTotalElements());
        response.setFlowExampleList(flowExampleList);
        return response;
    }

    @Override
    public void deleteIndex() {
        template.deleteIndex(SpiderFlowExampleLog.class);
    }

    private BoolQueryBuilder buildFlowExample(QueryFlowExample queryFlowExample) {

        BoolQueryBuilder defaultQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(queryFlowExample.getBusinessParam())) {
            defaultQueryBuilder.should(QueryBuilders.queryStringQuery(queryFlowExample.getBusinessParam()).field("requestParam"));
        }

        if (StringUtils.isNotEmpty(queryFlowExample.getId())) {
            defaultQueryBuilder.should(QueryBuilders.queryStringQuery(queryFlowExample.getId()).field("id"));
        }

        if(StringUtils.isNotEmpty(queryFlowExample.getBrokerName())){
            defaultQueryBuilder.should(QueryBuilders.termQuery("brokerName", queryFlowExample.getBrokerName()));
        }

        if (StringUtils.isNotEmpty(queryFlowExample.getFunctionName())) {
            defaultQueryBuilder.should(QueryBuilders.termQuery("functionName", queryFlowExample.getFunctionName()));
        }

        if (StringUtils.isNotEmpty(queryFlowExample.getFunctionId())) {
            defaultQueryBuilder.should(QueryBuilders.termQuery("functionId", queryFlowExample.getFunctionId()));
        }
        // 出发时间
        if (Objects.nonNull(queryFlowExample.getStartTime())) {
            defaultQueryBuilder.should(QueryBuilders.rangeQuery("startTime").gt(queryFlowExample.getStartTime()));
        }

        if (Objects.nonNull(queryFlowExample.getEndTime())) {
            defaultQueryBuilder.should(QueryBuilders.rangeQuery("endTime").lte(queryFlowExample.getEndTime()));
        }
        // 耗时
        if (Objects.nonNull(queryFlowExample.getGtTakeTime())) {
            defaultQueryBuilder.should(QueryBuilders.rangeQuery("takeTime").gt(queryFlowExample.getGtTakeTime()));
        }

        if (Objects.nonNull(queryFlowExample.getLtTakeTime())) {
            defaultQueryBuilder.should(QueryBuilders.rangeQuery("takeTime").lt(queryFlowExample.getGtTakeTime()));
        }
        return defaultQueryBuilder;
    }
}
