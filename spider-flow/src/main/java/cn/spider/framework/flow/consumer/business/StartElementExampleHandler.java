package cn.spider.framework.flow.consumer.business;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.StartElementExampleData;
import cn.spider.framework.flow.engine.StoryEngine;
import cn.spider.framework.flow.engine.example.FlowExampleManager;
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
 * @CreateTime: 2023-04-25  17:15
 * @Description: 流程实例开始同步handler
 * @Version: 1.0
 */
@Component
public class StartElementExampleHandler implements InitializingBean {

    @Resource
    private EventBus eventBus;

    @Resource
    private TranscriptManager transcriptManager;

    @Resource
    private StoryEngine storyEngine;

    private EventType eventType = EventType.ELEMENT_START;

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
            StartElementExampleData elementExampleData = JSON.parseObject(message.body(), StartElementExampleData.class);
            FlowExampleManager exampleManager = this.storyEngine.getFlowExampleManager();
            exampleManager.syncTranscriptStartElementExample(elementExampleData, brokerName);
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.registerConsumer();
    }
}
