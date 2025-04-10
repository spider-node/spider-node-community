package cn.spider.framework.transaction.server.consumer;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.StartElementExampleData;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.transaction.sdk.data.RegisterTransactionRequest;
import cn.spider.framework.transaction.server.TransactionManager;
import com.alibaba.fastjson.JSON;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class RegisterTransactionHandlerV2 {

    private EventBus eventBus;

    /**
     * 事务管理器
     */
    private TransactionManager transactionManager;

    /**
     * 事件类型
     */
    private EventType eventType;

    private String localBrokerName;

    public RegisterTransactionHandlerV2(EventBus eventBus, TransactionManager transactionManager, Vertx vertx) {
        this.eventBus = eventBus;
        this.transactionManager = transactionManager;
        this.eventType = EventType.ELEMENT_START;
        this.localBrokerName = BrokerInfoUtil.queryBrokerName(vertx);
        registerConsumer();
    }

    /**
     * 注册事务
     */
    public void registerConsumer() {
        MessageConsumer<String> consumer = eventBus.consumer(eventType.queryAddr());
        consumer.handler(message -> {
            MultiMap multiMap = message.headers();
            String brokerName = multiMap.get(Constant.BROKER_NAME);
            // 校验该本节点是否为 brokerName的功能follower
            if (!this.localBrokerName.equals(brokerName)) {
                return;
            }
            StartElementExampleData data = JSON.parseObject(message.body(), StartElementExampleData.class);
            // 当getTransactionGroupId为空,getRunType 为虚拟运行,则本节点不需要事务
            if (StringUtils.isEmpty(data.getTransactionGroupId()) || (StringUtils.isNotEmpty(data.getRunType()) && data.getRunType().equals(Constant.VIRTUALLY))) {
                // 说明本节点不需要事务
                return;
            }
            log.info("registerTransactionHandlerV2,brokerName:{},data:{}", brokerName, message.body());
            RegisterTransactionRequest registerTransaction = new RegisterTransactionRequest();
            registerTransaction.setGroupId(data.getTransactionGroupId());
            registerTransaction.setRequestId(data.getRequestId());
            registerTransaction.setResourceId(data.getDatasourceId());
            registerTransaction.setTaskId(data.getFlowElementId());
            transactionManager.registerTransactionV2(registerTransaction);
        });
    }


}
