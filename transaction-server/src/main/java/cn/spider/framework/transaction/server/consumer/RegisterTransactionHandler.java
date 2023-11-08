package cn.spider.framework.transaction.server.consumer;
import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.RegisterTransactionData;
import cn.spider.framework.transaction.server.TransactionManager;
import cn.spider.framework.transaction.server.transcript.TranscriptManager;
import com.alibaba.fastjson.JSON;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.transaction.server.consumer
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-25  19:18
 * @Description: 事件
 * @Version: 1.0
 */
@Component
public class RegisterTransactionHandler implements InitializingBean {

    @Resource
    private EventBus eventBus;

    @Resource
    private TranscriptManager transcriptManager;

    @Resource
    private TransactionManager transactionManager;

    private EventType eventType;

    public void registerConsumer() {
        MessageConsumer<String> consumer = eventBus.consumer(eventType.queryAddr());
        consumer.handler(message -> {
            MultiMap multiMap = message.headers();
            String brokerName = multiMap.get(Constant.BROKER_NAME);
            // 校验该本节点是否为 brokerName的功能follower
            if (!transcriptManager.checkIsTranscript(brokerName)) {
                return;
            }
            RegisterTransactionData data = JSON.parseObject(message.body(), RegisterTransactionData.class);
            // 注册事务
            transactionManager.registerSyncTransaction(data.getRequestId(), data.getTransactionGroupId(), data.getTaskId(), data.getWorkerName());
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.eventType = EventType.REGISTER_TRANSACTION;
        registerConsumer();
    }
}
