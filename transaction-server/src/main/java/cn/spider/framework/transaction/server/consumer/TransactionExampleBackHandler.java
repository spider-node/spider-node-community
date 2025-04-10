package cn.spider.framework.transaction.server.consumer;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.EndFlowExampleEventData;
import cn.spider.framework.common.event.enums.FlowExampleStatus;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.transaction.server.TransactionManager;
import cn.spider.framework.transaction.server.example.enums.ExampleStatus;
import com.alibaba.fastjson.JSON;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;

/**
 * 接收 整体的流程实例结束事件
 */
public class TransactionExampleBackHandler {
    private EventBus eventBus;

    /**
     * 事务管理器
     */
    private TransactionManager transactionManager;

    /**
     * 事件类型
     */
    private EventType eventType;

    /**
     * 本地broker名称
     */
    private String localBrokerName;

    private Vertx vertx;

    public TransactionExampleBackHandler(EventBus eventBus, TransactionManager transactionManager, Vertx vertx) {
        this.eventBus = eventBus;
        this.transactionManager = transactionManager;
        // 监听实例的结束事件
        this.eventType = EventType.END_FLOW_EXAMPLE;
        this.localBrokerName = BrokerInfoUtil.queryBrokerName(vertx);
        this.vertx = vertx;
        registerConsumer();
    }

    public void registerConsumer() {
        MessageConsumer<String> consumer = eventBus.consumer(eventType.queryAddr());
        consumer.handler(message -> {
            MultiMap multiMap = message.headers();
            String brokerName = multiMap.get(Constant.BROKER_NAME);
            // 校验该本节点是否为 brokerName的功能follower
            if (!this.localBrokerName.equals(brokerName)) {
                return;
            }
            EndFlowExampleEventData endFlowExampleEventData = JSON.parseObject(message.body(), EndFlowExampleEventData.class);
            // 延迟500毫秒更新状态
            delayDecision(endFlowExampleEventData);
        });
    }

    /**
     * 延迟200毫秒更新状态
     * @param endFlowExampleEventData
     */
    private void delayDecision(EndFlowExampleEventData endFlowExampleEventData) {
        vertx.setTimer(100, handler -> {
            FlowExampleStatus status = endFlowExampleEventData.getStatus();
            ExampleStatus exampleStatus = status.equals(FlowExampleStatus.SUSS) ? ExampleStatus.SUSS : ExampleStatus.FAIL;
            transactionManager.updateExampleStatus(endFlowExampleEventData.getRequestId(), exampleStatus);
        });
    }
}
