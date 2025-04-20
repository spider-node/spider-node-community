package cn.spider.framework.transaction.server.consumer;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.EndElementExampleData;
import cn.spider.framework.common.event.enums.ElementStatus;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.transaction.server.TransactionManager;
import cn.spider.framework.transaction.server.example.enums.TaskStatus;
import com.alibaba.fastjson.JSON;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 接受task任务执行的结果 - 保存，在接受 整体执行结果后，判断提交,那些事务组,回滚那些事务组
 */
@Slf4j
public class TaskRunResultBackHandler {
    private EventBus eventBus;

    /**
     * 事务管理器
     */
    private TransactionManager transactionManager;

    /**
     * 事件类型
     */
    private EventType eventType;

    /**
     * 本地broker名称
     */
    private String localBrokerName;


    public TaskRunResultBackHandler(EventBus eventBus, TransactionManager transactionManager, Vertx vertx) {
        this.eventBus = eventBus;
        this.transactionManager = transactionManager;
        this.localBrokerName = BrokerInfoUtil.queryBrokerName(vertx);
        // 监听节点执行结束
        this.eventType = EventType.ELEMENT_END;
        registerConsumer();
    }

    public void registerConsumer() {
        MessageConsumer<String> consumer = eventBus.consumer(eventType.queryAddr());
        consumer.handler(message -> {
            MultiMap multiMap = message.headers();
            String brokerName = multiMap.get(Constant.BROKER_NAME);
            // 校验该本节点是否为 brokerName的功能follower
            if (!this.localBrokerName.equals(brokerName)) {
                return;
            }
            EndElementExampleData data = JSON.parseObject(message.body(), EndElementExampleData.class);
            if (StringUtils.isEmpty(data.getTransactionGroupId())) {
                // 说明本节点不需要事务
                return;
            }
            log.info("收到节点执行结果：{}", message.body());
            ElementStatus status = data.getStatus();
            // 修改每个节点的状态信息
            transactionManager.updateTransactionTaskStatus(data.getTransactionGroupId(), data.getFlowElementId(), status.equals(ElementStatus.SUSS) ? TaskStatus.SUCCESS : TaskStatus.FAIL,data.getRequestId(),data.getWorkerName());
        });

    }
}
