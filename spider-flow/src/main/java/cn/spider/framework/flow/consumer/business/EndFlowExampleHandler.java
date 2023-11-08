package cn.spider.framework.flow.consumer.business;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.EndFlowExampleEventData;
import cn.spider.framework.flow.engine.StoryEngine;
import cn.spider.framework.flow.transcript.TranscriptManager;
import com.alibaba.fastjson.JSON;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.consumer.business
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-26  16:15
 * @Description: 告知流程实例结束
 * @Version: 1.0
 */
@Component
public class EndFlowExampleHandler implements InitializingBean {

    @Resource
    private EventBus eventBus;

    @Resource
    private StoryEngine storyEngine;

    @Resource
    private TranscriptManager transcriptManager;

    private EventType eventType = EventType.END_FLOW_EXAMPLE;

    public void registerConsumer() {
        MessageConsumer<String> consumer = eventBus.consumer(eventType.queryAddr());
        consumer.handler(message -> {
            if(true){
                return;
            }
            MultiMap multiMap = message.headers();
            String brokerName = multiMap.get(Constant.BROKER_NAME);
            // 校验该本节点是否为 brokerName的功能follower
            if (!transcriptManager.checkIsTranscript(brokerName)) {
                return;
            }
            EndFlowExampleEventData data = JSON.parseObject(message.body(), EndFlowExampleEventData.class);
            this.storyEngine.getFlowExampleManager().syncTranscriptFlowExampleEnd(brokerName, data.getRequestId());
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.registerConsumer();
    }
}
