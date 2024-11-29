package cn.spider.node.host.plugin.center.event;

import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.HostApplicationOfflineData;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.node.host.plugin.center.application.HostApplicationManager;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import org.apache.commons.lang3.StringUtils;

public class HostApplicationOfflineHandler {
    private EventBus eventBus;

    private EventType eventType = EventType.HOST_OFFLINE;

    private String localBrokerName;

    private HostApplicationManager applicationManager;


    public HostApplicationOfflineHandler(EventBus eventBus, HostApplicationManager applicationManager, Vertx vertx) {
        this.eventBus = eventBus;
        this.applicationManager = applicationManager;
        this.localBrokerName = BrokerInfoUtil.queryBrokerName(vertx);
        registerConsumer();
    }

    public void registerConsumer() {
        MessageConsumer<String> consumer = eventBus.consumer(eventType.queryAddr());
        consumer.handler(message -> {
            HostApplicationOfflineData offlineData = JSON.parseObject(message.body(), HostApplicationOfflineData.class);
            if (!StringUtils.equals(localBrokerName, offlineData.getBrokerName())) {
                return;
            }
            // 宿主机下线了，，直接删除关联的插件
            applicationManager.deletePluginDeployInfo(offlineData.getIp());
            // 删除宿主机应用的地址
            applicationManager.deleteApplicationHost(offlineData.getIp());
            // 删除部署信息
            applicationManager.deleteSpiderApplicationTask(offlineData.getIp());
        });
    }
}
