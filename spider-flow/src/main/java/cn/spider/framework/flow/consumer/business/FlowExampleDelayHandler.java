package cn.spider.framework.flow.consumer.business;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.flow.engine.StoryEngine;
import cn.spider.framework.flow.engine.example.FlowExampleManager;
import cn.spider.framework.flow.engine.example.data.FlowExample;
import cn.spider.framework.flow.timer.data.FlowDelayExample;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.consumer.business
 * @Author: dengdongsheng
 * @CreateTime: 2023-07-05  16:04
 * @Description: 流程实例延迟节点
 * @Version: 1.0
 */
public class FlowExampleDelayHandler {
    private EventBus eventBus;

    private FlowExampleManager flowExampleManager;

    private EventType eventType = EventType.FLOW_EXAMPLE_DELAY;

    private String localBrokerName;

    public FlowExampleDelayHandler(EventBus eventBus, Vertx vertx,StoryEngine storyEngine) {
        this.eventBus = eventBus;
        this.flowExampleManager = storyEngine.getFlowExampleManager();
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
            FlowExample flowExample = flowExampleManager.queryFlowExample(destroyClassData.getExampleId());
            if(Objects.isNull(flowExample)){
                return;
            }
            flowExampleManager.pollRunFlowExample(flowExample);
        });
    }
}
