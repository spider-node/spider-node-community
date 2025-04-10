package cn.spider.framework.domain.area.handler;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.BizHostOfflineData;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.domain.area.agent.AgentVertxClient;
import cn.spider.framework.domain.area.agent.data.UninstallBizParam;
import com.alibaba.fastjson.JSON;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 接受基于功能再宿主机中下线事件
 */
@Slf4j
public class BizUninstallHandler {
    private EventBus eventBus;

    private EventType eventType = EventType.SCALE_DOWN_NOTIFY_HOST;

    private AgentVertxClient agentVertxClient;

    private Integer uninstallPort;

    private String localBrokerName;

    public BizUninstallHandler(AgentVertxClient agentVertxClient, Vertx vertx) {
        this.eventBus = vertx.eventBus();
        this.agentVertxClient = agentVertxClient;
        this.uninstallPort = 1238;
        this.localBrokerName = BrokerInfoUtil.queryBrokerName(vertx);

        registerConsumer();
    }

    public void registerConsumer() {
        eventBus.consumer(eventType.queryAddr(), message -> {
            try {
                MultiMap multiMap = message.headers();
                String brokerName = multiMap.get(Constant.BROKER_NAME);
                // 校验该本节点是否为 brokerName的功能follower
                if (!this.localBrokerName.equals(brokerName)) {
                    return;
                }
                BizHostOfflineData data = JSON.parseObject(message.body().toString(), BizHostOfflineData.class);
                UninstallBizParam param = new UninstallBizParam(data.getBizName(), data.getBizVersion());
                agentVertxClient.uninstallBiz(JsonObject.mapFrom(param), data.getIp(), uninstallPort);
            } catch (Exception e) {
                log.info("BizUninstallHandler_error", ExceptionMessage.getStackTrace(e));
            }
        });
    }
}
