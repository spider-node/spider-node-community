package cn.spider.framework.flow.consumer.system;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.DeployBpmnData;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.flow.resource.factory.StartEventFactory;
import com.alibaba.fastjson.JSON;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


public class SyncDeployBpmnHandler implements InitializingBean {

    private EventBus eventBus;

    private StartEventFactory startEventFactory;

    private Vertx vertx;

    private String localBrokerName;

    private EventType eventType = EventType.DEPLOY_BPMN;

    public SyncDeployBpmnHandler(EventBus eventBus, StartEventFactory startEventFactory, Vertx vertx) {
        this.eventBus = eventBus;
        this.startEventFactory = startEventFactory;
        this.vertx = vertx;
    }

    public void registerConsumer() {
        MessageConsumer<String> consumer = eventBus.consumer(eventType.queryAddr());
        consumer.handler(message -> {
            MultiMap multiMap = message.headers();
            String brokerName = multiMap.get(Constant.BROKER_NAME);
            if (StringUtils.equals(brokerName, localBrokerName)) {
                return;
            }
            String body = message.body();
            DeployBpmnData data = JSON.parseObject(body, DeployBpmnData.class);
            // 执行一次卸载，
            startEventFactory.destroyBpmn(data.getUrl());
            // 然后再执行加载
            startEventFactory.dynamicsLoaderBpmn(data.getUrl());
        });
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        registerConsumer();
        this.localBrokerName = BrokerInfoUtil.queryBrokerName(vertx);
    }
}
