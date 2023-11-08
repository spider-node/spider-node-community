package cn.spider.framework.controller.consumer;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.BrokerInfoData;
import cn.spider.framework.common.event.data.EndFlowExampleEventData;
import cn.spider.framework.common.role.BrokerRole;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.controller.broker.BrokerManager;
import cn.spider.framework.controller.broker.data.BrokerInfo;
import com.alibaba.fastjson.JSON;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import org.apache.commons.lang3.StringUtils;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.consumer
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-18  18:23
 * @Description: TODO
 * @Version: 1.0
 */
public class BrokerInfoAsyncHandler {
    private EventBus eventBus;

    private String localBrokerName;

    private String localBrokerIp;

    private BrokerManager brokerManager;

    private EventType eventType = EventType.BROKER_ASYNC_INFO;

    public BrokerInfoAsyncHandler(EventBus eventBus, BrokerManager brokerManager, Vertx vertx) {
        this.eventBus = eventBus;
        this.brokerManager = brokerManager;
        this.localBrokerIp = BrokerInfoUtil.queryBrokerIp(vertx);
        this.localBrokerName = BrokerInfoUtil.queryBrokerName(vertx);
        registerConsumer();
    }

    public void registerConsumer() {
        MessageConsumer<String> consumer = eventBus.consumer(eventType.queryAddr());
        consumer.handler(message -> {
            // 当本节点是follower的时候，不做任何处理
            MultiMap multiMap = message.headers();
            String brokerName = multiMap.get(Constant.BROKER_NAME);
            // 校验该本节点是否为 brokerName的功能follower
            if (StringUtils.equals(brokerName, localBrokerName)) {
                return;
            }
            BrokerInfoData brokerInfoData = JSON.parseObject(message.body(), BrokerInfoData.class);
            if(StringUtils.isEmpty(brokerInfoData.getBrokerIp())){
                return;
            }
            brokerManager.syncBroker(BrokerInfo.builder().brokerIp(brokerInfoData.getBrokerIp()).brokerName(brokerInfoData.getBrokerName()).build());
        });
    }


}
