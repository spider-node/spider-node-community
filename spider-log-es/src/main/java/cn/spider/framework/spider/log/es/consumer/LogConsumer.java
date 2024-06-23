package cn.spider.framework.spider.log.es.consumer;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.*;
import cn.spider.framework.common.role.EventTypeRole;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.log.sdk.enums.ExampleType;
import cn.spider.framework.spider.log.es.domain.ElementExampleLog;
import cn.spider.framework.spider.log.es.domain.SpiderFlowElementExampleLog;
import cn.spider.framework.spider.log.es.domain.SpiderFlowExampleLog;
import cn.spider.framework.spider.log.es.domain.SpiderLog;
import cn.spider.framework.spider.log.es.queue.QueueManager;
import com.alibaba.fastjson.JSON;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.spider.log.es.consumer
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-18  22:48
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
public class LogConsumer {
    private EventBus eventBus;

    private QueueManager queueManager;


    private String localBrokerName;

    public LogConsumer(QueueManager queueManager, EventBus eventBus, Vertx vertx) {
        this.eventBus = eventBus;
        this.queueManager = queueManager;
        this.localBrokerName = BrokerInfoUtil.queryBrokerName(vertx);
        consumer();
    }

    public void consumer() {
        EventType[] eventTypes = EventType.values();
        for (EventType eventType : eventTypes) {
            if (eventType.getEventTypeRole().equals(EventTypeRole.SYSTEM)) {
                continue;
            }
            MessageConsumer<String> consumer = eventBus.consumer(eventType.queryAddr());
            consumer.handler(message -> {
                MultiMap multiMap = message.headers();
                String eventName = multiMap.get(Constant.EVENT_NAME);
                String brokerName = multiMap.get(Constant.BROKER_NAME);
                // 只处理本节点发送的日志信息
                if (!StringUtils.equals(brokerName, localBrokerName)) {
                    return;
                }
                ElementExampleLog elementExampleLog = ElementExampleLog.builder().build();
                String data = message.body();
                //log.info("获取到写入日志的参数 {}",data);
                switch (eventName) {
                    case "start_flow_example":
                        elementExampleLog.setExampleType(ExampleType.FLOW);
                        SpiderLog spiderFlowExampleLog = buildStartFlowExample(data, brokerName);
                        elementExampleLog.setExampleLog(spiderFlowExampleLog);
                        break;
                    case "end_flow_example":
                        elementExampleLog.setExampleType(ExampleType.FLOW);
                        SpiderLog spiderFlowExampleLogs = buildEndFlowExample(data, brokerName);
                        elementExampleLog.setExampleLog(spiderFlowExampleLogs);
                        break;
                    case "start_element_example":
                        elementExampleLog.setExampleType(ExampleType.ELEMENT);
                        SpiderLog spiderFlowElementExampleLog = buildStartElementExample(data);
                        elementExampleLog.setExampleLog(spiderFlowElementExampleLog);
                        break;
                    case "end_element_example":
                        elementExampleLog.setExampleType(ExampleType.ELEMENT);
                        SpiderLog endElementLog = buildEndElementExample(data);
                        elementExampleLog.setExampleLog(endElementLog);
                        break;
                    case "run_transaction":
                        elementExampleLog.setExampleType(ExampleType.ELEMENT);
                        SpiderLog runTransaction = buildRunTransaction(data);
                        elementExampleLog.setExampleLog(runTransaction);
                        break;
                    case "end_transaction":
                        elementExampleLog.setExampleType(ExampleType.ELEMENT);
                        SpiderLog endTransaction = buildEndTransaction(data);
                        elementExampleLog.setExampleLog(endTransaction);

                }
                queueManager.insertQueue(JSON.toJSONString(elementExampleLog));
                message.reply("log-receive");
            });
        }

    }

    private SpiderFlowExampleLog buildStartFlowExample(String data, String brokerName) {
        StartFlowExampleEventData startFlowExampleEventData = JSON.parseObject(data, StartFlowExampleEventData.class);
        String id = startFlowExampleEventData.getRequestId();
        return SpiderFlowExampleLog.builder()
                .id(id)
                .brokerName(brokerName)
                .functionId(startFlowExampleEventData.getFunctionId())
                .functionName(startFlowExampleEventData.getFunctionName())
                .requestParam(Objects.isNull(startFlowExampleEventData.getRequestParam()) ? "{}" : JSON.toJSONString(startFlowExampleEventData.getRequestParam()))
                .startTime(startFlowExampleEventData.getTime())
                .build();

    }

    private SpiderFlowExampleLog buildEndFlowExample(String data, String brokerName) {
        EndFlowExampleEventData elementExampleData = JSON.parseObject(data, EndFlowExampleEventData.class);
        String id = elementExampleData.getRequestId();
        return SpiderFlowExampleLog.builder()
                .id(id)
                .endTime(elementExampleData.getTime())
                .returnParam(Objects.nonNull(elementExampleData.getResult()) ? elementExampleData.getResult().toString() : "{}")
                .exception(elementExampleData.getException())
                .status(elementExampleData.getStatus().name())
                .transactionStatus(Objects.nonNull(elementExampleData.getTransactionStatus()) ? elementExampleData.getTransactionStatus().name() : null)
                .brokerName(brokerName)
                .build();
    }

    private SpiderFlowElementExampleLog buildStartElementExample(String data) {
        StartElementExampleData elementExampleData = JSON.parseObject(data, StartElementExampleData.class);
        String id = elementExampleData.getRequestId() + elementExampleData.getFlowElementId();
        return SpiderFlowElementExampleLog.builder()
                .id(id)
                .requestId(elementExampleData.getRequestId())
                .flowElementId(elementExampleData.getFlowElementId())
                .flowElementName(elementExampleData.getFlowElementName())
                .functionId(elementExampleData.getFunctionId())
                .functionName(elementExampleData.getFunctionName())
                .branchId(elementExampleData.getBranchId())
                .transactionGroupId(elementExampleData.getTransactionGroupId())
                .startTime(elementExampleData.getTime())
                .build();
    }

    private SpiderFlowElementExampleLog buildEndElementExample(String data) {
        EndElementExampleData elementExampleData = JSON.parseObject(data, EndElementExampleData.class);
        String id = elementExampleData.getRequestId() + elementExampleData.getFlowElementId();
        return SpiderFlowElementExampleLog.builder()
                .id(id)
                .requestId(elementExampleData.getRequestId())
                .endTime(elementExampleData.getTime())
                .flowElementId(elementExampleData.getFlowElementId())
                .requestParam(elementExampleData.getRequestParam())
                .returnParam(Objects.isNull(elementExampleData.getReturnParam()) ? "{}" :  JSON.toJSONString(elementExampleData.getReturnParam()))
                .exception(elementExampleData.getException())
                .status(elementExampleData.getStatus().name())
                .build();

    }

    private SpiderFlowElementExampleLog buildRunTransaction(String data) {
        StartTransactionData startTransactionData = JSON.parseObject(data, StartTransactionData.class);
        String id = startTransactionData.getRequestId() + startTransactionData.getFlowElementId();
        return SpiderFlowElementExampleLog.builder()
                .id(id)
                .transactionOperate(startTransactionData.getTransactionOperate().name())
                .build();

    }

    private SpiderFlowElementExampleLog buildEndTransaction(String data) {
        EndTransactionData endTransactionData = JSON.parseObject(data, EndTransactionData.class);
        String id = endTransactionData.getRequestId() + endTransactionData.getFlowElementId();
        return SpiderFlowElementExampleLog.builder()
                .id(id)
                .transactionStatus(endTransactionData.getTransactionStatus().name())
                .finalEndTime(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();
    }


}
