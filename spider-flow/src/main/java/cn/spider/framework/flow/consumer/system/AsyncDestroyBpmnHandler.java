package cn.spider.framework.flow.consumer.system;
import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.DestroyBpmnData;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.flow.resource.factory.StartEventFactory;
import com.alibaba.fastjson.JSON;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import org.apache.commons.lang3.StringUtils;


public class AsyncDestroyBpmnHandler {

    private EventBus eventBus;

    private Vertx vertx;

    private StartEventFactory startEventFactory;

    private EventType eventType = EventType.DESTROY_BPMN;

    private String localBrokerName;

    public AsyncDestroyBpmnHandler(EventBus eventBus, Vertx vertx, StartEventFactory startEventFactory) {
        this.eventBus = eventBus;
        this.vertx = vertx;
        this.startEventFactory = startEventFactory;
        this.localBrokerName = BrokerInfoUtil.queryBrokerName(vertx);
        registerConsumer();
    }

    public void registerConsumer() {
        MessageConsumer<String> consumer = eventBus.consumer(eventType.queryAddr());
        consumer.handler(message -> {
            MultiMap multiMap = message.headers();
            String brokerName = multiMap.get(Constant.BROKER_NAME);
            // 自身的事件不进行消费
            if(StringUtils.equals(brokerName,localBrokerName)){
                return;
            }
            DestroyBpmnData destroyBpmnData = JSON.parseObject(message.body(),DestroyBpmnData.class);
            startEventFactory.destroyBpmn(destroyBpmnData.getBpmnName());
        });
    }
}
