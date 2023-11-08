package cn.spider.framework.controller.consumer;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.EventData;
import cn.spider.framework.controller.BrokerRoleManager;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.consumer
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-18  13:26
 * @Description: 接受leader的定时通知
 * @Version: 1.0
 */
@Slf4j
public class AcceptLeaderInfoHandler {
    private EventBus eventBus;

    private EventManager eventManager;

    private EventType eventType = EventType.LEADER_GENERATE;

    private BrokerRoleManager brokerRoleManager;

    private Vertx vertx;

    private RedissonClient redissonClient;


    public AcceptLeaderInfoHandler(EventBus eventBus,
                                   EventManager eventManager,
                                   BrokerRoleManager brokerRoleManager,
                                   Vertx vertx,
                                   RedissonClient redissonClient) {
        this.eventBus = eventBus;
        this.eventManager = eventManager;
        this.brokerRoleManager = brokerRoleManager;
        this.vertx = vertx;
       // this.followerManager = followerManager;
        this.redissonClient = redissonClient;
        registerConsumer();
    }

    public void registerConsumer() {
        MessageConsumer<String> consumer = eventBus.consumer(eventType.queryAddr());
        consumer.handler(message -> {
            if(true){
               return;
            }
        });
    }

    public void sendEvent(){
        // 加锁
        RLock rlock = redissonClient.getLock(Constant.NOTIFY_CEREBRAL_FISSURE);
        try {
            if(!rlock.tryLock()){
                return;
            }
            eventManager.sendMessage(EventType.LEADER_CEREBRAL_FISSURE, new EventData());
        } finally {
            rlock.unlock();
        }
    }
}
