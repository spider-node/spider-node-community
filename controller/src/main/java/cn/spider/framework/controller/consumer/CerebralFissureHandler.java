package cn.spider.framework.controller.consumer;

import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.role.BrokerRole;
import cn.spider.framework.controller.BrokerRoleManager;
import cn.spider.framework.controller.follower.FollowerManager;
import cn.spider.framework.controller.leader.LeaderManager;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.consumer
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-18  12:28
 * @Description: 知道当前脑裂了，节点接受到该消息，身份会恢复成follower
 * @Version: 1.0
 */
@Slf4j
public class CerebralFissureHandler {

    private EventBus eventBus;

   // private LeaderManager leaderManager;

    private BrokerRoleManager brokerRoleManager;

    public CerebralFissureHandler(EventBus eventBus,
                                  //LeaderManager leaderManager,
                                  BrokerRoleManager brokerRoleManager) {
        this.eventBus = eventBus;
       // this.leaderManager = leaderManager;
        this.brokerRoleManager = brokerRoleManager;
        registerConsumer();
    }

    private EventType eventType = EventType.LEADER_CEREBRAL_FISSURE;


    public void registerConsumer() {
        MessageConsumer<String> consumer = eventBus.consumer(eventType.queryAddr());
        consumer.handler(message -> {
            // 当本节点是follower的时候，不做任何处理
            if(this.brokerRoleManager.queryBrokerRole().equals(BrokerRole.FOLLOWER)){
                return;
            }
            // 降级为follower
            //leaderManager.reduceFollower();
        });
    }

}
