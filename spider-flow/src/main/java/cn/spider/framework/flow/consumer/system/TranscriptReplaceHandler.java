package cn.spider.framework.flow.consumer.system;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.TranscriptChangeData;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.flow.transcript.TranscriptManager;
import com.alibaba.fastjson.JSON;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.consumer.system
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-27  19:17
 * @Description: 副本替换的handler
 * @Version: 1.0
 */
@Component
public class TranscriptReplaceHandler implements InitializingBean {

    @Resource
    private EventBus eventBus;

    @Resource
    private TranscriptManager transcriptManager;

    private String brokerName;

    @Resource
    private Vertx vertx;

    private EventType eventType = EventType.LEADER_REPLACE_CHANGE;

    public void registerConsumer(){
        MessageConsumer<String> consumer = eventBus.consumer(eventType.queryAddr());
        consumer.handler(message -> {
            TranscriptChangeData data = JSON.parseObject(message.body(), TranscriptChangeData.class);
            if(!StringUtils.equals(data.getBrokerName(),this.brokerName)){
                return;
            }
            // 替换副本信息
            transcriptManager.replace(data.getTranscript());
        });
    }




    @Override
    public void afterPropertiesSet() throws Exception {
        this.brokerName = BrokerInfoUtil.queryBrokerName(this.vertx);
        this.registerConsumer();
    }

}
