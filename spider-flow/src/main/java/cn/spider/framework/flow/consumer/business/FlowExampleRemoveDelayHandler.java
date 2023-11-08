package cn.spider.framework.flow.consumer.business;

import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.flow.engine.example.ExampleDestroyManager;
import cn.spider.framework.flow.timer.data.FlowDelayExample;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import org.apache.commons.lang3.StringUtils;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.consumer.business
 * @Author: dengdongsheng
 * @CreateTime: 2023-07-05  16:23
 * @Description: f
 * @Version: 1.0
 */
public class FlowExampleRemoveDelayHandler {
    private EventBus eventBus;

    private ExampleDestroyManager exampleDestroyManager;

    private EventType eventType = EventType.FLOW_EXAMPLE_REMOVE_DELAY;

    private String localBrokerName;

    public FlowExampleRemoveDelayHandler(EventBus eventBus, Vertx vertx,ExampleDestroyManager exampleDestroyManager) {
        this.eventBus = eventBus;
        this.exampleDestroyManager = exampleDestroyManager;
        this.localBrokerName = BrokerInfoUtil.queryBrokerName(vertx);
        registerConsumer();
    }

    public void registerConsumer(){
        MessageConsumer<String> consumer = eventBus.consumer(eventType.queryAddr());
        consumer.handler(message -> {
            FlowDelayExample destroyClassData = JSON.parseObject(message.body(),FlowDelayExample.class);
            if(!StringUtils.equals(localBrokerName,destroyClassData.getBrokerName())){
                return;
            }
            exampleDestroyManager.addExampleData(destroyClassData.getExampleId());
        });
    }
}
