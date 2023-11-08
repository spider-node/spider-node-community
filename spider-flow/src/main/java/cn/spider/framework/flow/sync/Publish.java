package cn.spider.framework.flow.sync;

import cn.spider.framework.common.utils.BrokerInfoUtil;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import org.apache.commons.lang3.StringUtils;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.sync
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-13  20:54
 * @Description: TODO
 * @Version: 1.0
 */
public class Publish {

    private EventBus eventBus;

    private String brokerRole;

    private final String BROKER_LEADER = "leader";

    public Publish(Vertx vertx) {
        this.eventBus = vertx.eventBus();
        this.brokerRole = BrokerInfoUtil.queryBrokerName(vertx);
    }

    /**
     * 推送事件消息 只有leader才会进行推送
     *
     * @param addr
     * @param body
     */
    public void push(String addr, String body) {
        // 当前broker角色不是leader的情况下，不进行发生消息
        if (!StringUtils.equals(brokerRole, BROKER_LEADER)) {
            return;
        }
        eventBus.publish(addr, body);
    }

}
