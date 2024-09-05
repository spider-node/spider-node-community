package cn.spider.node.host.plugin.center.event;

import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.HostApplicationOnlineData;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.node.host.plugin.center.application.HostApplicationManager;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import org.apache.commons.lang3.StringUtils;

public class HostApplicationOnlineHandler {
    private EventBus eventBus;

    private EventType eventType = EventType.HOST_ONLINE;

    private String localBrokerName;

    private HostApplicationManager applicationManager;

    public HostApplicationOnlineHandler(EventBus eventBus, HostApplicationManager applicationManager, Vertx vertx) {
        this.eventBus = eventBus;
        this.applicationManager = applicationManager;
        this.localBrokerName = BrokerInfoUtil.queryBrokerName(vertx);
        registerConsumer();
    }

    public void registerConsumer() {
        MessageConsumer<String> consumer = eventBus.consumer(eventType.queryAddr());
        consumer.handler(message -> {
            HostApplicationOnlineData onlineData = JSON.parseObject(message.body(), HostApplicationOnlineData.class);
            if (!StringUtils.equals(localBrokerName, onlineData.getBrokerName())) {
                return;
            }
            //  处理上线
            applicationManager.online(onlineData.getIp());
        });
    }
}
