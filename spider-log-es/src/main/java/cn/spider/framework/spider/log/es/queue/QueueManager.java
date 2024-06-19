package cn.spider.framework.spider.log.es.queue;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.log.sdk.enums.ExampleType;
import cn.spider.framework.spider.log.es.domain.ElementExampleLog;
import cn.spider.framework.spider.log.es.domain.SpiderFlowElementExampleLog;
import cn.spider.framework.spider.log.es.domain.SpiderFlowExampleLog;
import cn.spider.framework.spider.log.es.service.SpiderFlowElementExampleService;
import cn.spider.framework.spider.log.es.service.SpiderFlowExampleLogService;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.spider.log.es.queue
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-18  21:01
 * @Description: 队列的管理
 * @Version: 1.0
 */
@Slf4j
public class QueueManager {
    //队列
    private BlockingQueue<String> flowExampleQueue;

    private Vertx vertx;

    // 流程实例的service
    private SpiderFlowElementExampleService spiderFlowElementExampleService;

    private SpiderFlowExampleLogService exampleLogService;

    private Executor spiderLogPool;

    public QueueManager(Vertx vertx,
                        SpiderFlowElementExampleService spiderFlowElementExampleService,
                        SpiderFlowExampleLogService exampleLogService,
                        Executor spiderLogPool) {
        this.vertx = vertx;
        this.flowExampleQueue = new ArrayBlockingQueue<>(6000, true);
        this.exampleLogService = exampleLogService;
        this.spiderFlowElementExampleService = spiderFlowElementExampleService;
        this.spiderLogPool = spiderLogPool;
        registerTimer();
    }

    /**
     * 加入队列
     *
     * @param param
     */
    public void insertQueue(String param) {
        flowExampleQueue.offer(param);
    }

    /**
     * 批量消费
     */
    public void consumerByBatch() {
        if (flowExampleQueue.size() == 0) {
            return;
        }
        spiderLogPool.execute(() -> {
            try {
                List<String> list = new ArrayList<>();
                Queues.drain(flowExampleQueue, list, 600, 6, TimeUnit.SECONDS);
                if (CollectionUtils.isEmpty(list)) {
                    return;
                }
                // 加入另外一个队列，保证是单个线程处理
                List<SpiderFlowElementExampleLog> elementExampleLogs = Lists.newArrayList();
                List<SpiderFlowExampleLog> flowExampleLogs = Lists.newArrayList();
                for (String value : list) {
                    JsonObject example = new JsonObject(value);
                    ElementExampleLog elementExampleLog = ElementExampleLog.builder()
                            .exampleType(ExampleType.valueOf(example.getString("exampleType")))
                            .build();
                    switch (elementExampleLog.getExampleType()) {
                        case FLOW:
                            flowExampleLogs.add(JSON.parseObject(example.getJsonObject("exampleLog").toString(), SpiderFlowExampleLog.class));
                            break;
                        case ELEMENT:
                            SpiderFlowElementExampleLog spiderFlowElementExampleLog = JSON.parseObject(example.getJsonObject("exampleLog").toString(), SpiderFlowElementExampleLog.class);
                            elementExampleLogs.add(spiderFlowElementExampleLog);
                            break;
                    }
                }

                spiderFlowElementExampleService.upsertBatchFlowElementExampleLog(elementExampleLogs);
                exampleLogService.upsetBatchFlowExampleLog(flowExampleLogs);
            } catch (Exception e) {
                log.error("缓存队列批量消费异常：{}", ExceptionMessage.getStackTrace(e));
            }
        });

        // 根据requestId+nodeId 查询出来整个流程的数据
    }

    public void registerTimer() {
        this.vertx.setPeriodic(1000 * 6, id -> {
            consumerByBatch();
        });
    }
}
