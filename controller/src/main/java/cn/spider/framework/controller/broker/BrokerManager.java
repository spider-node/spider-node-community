package cn.spider.framework.controller.broker;

import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.BrokerInfoData;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.controller.broker.data.BrokerInfo;
import cn.spider.framework.controller.sdk.data.SpiderServerInfo;
import cn.spider.framework.controller.sdk.interfaces.BrokerHeartService;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.broker
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-18  18:00
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
public class BrokerManager {
    private Map<String, BrokerInfo> brokerInfoMap;

    private String localBrokerName;

    private String localBrokerIp;

    private Vertx vertx;

    private EventManager eventManager;

    private EventType eventType;


    public BrokerManager(Vertx vertx, EventManager eventManager) {
        this.vertx = vertx;
        this.eventManager = eventManager;
        this.eventType = EventType.BROKER_ASYNC_INFO;
        this.localBrokerName = BrokerInfoUtil.queryBrokerName(vertx);
        this.brokerInfoMap = new HashMap<>();
        this.localBrokerIp = BrokerInfoUtil.queryBrokerIp(vertx);
    }

    // 修改广播的信息
    public void syncBroker(BrokerInfo info) {
        if (this.brokerInfoMap.containsKey(info.getBrokerIp()) || info.getBrokerName().equals(this.localBrokerIp)) {
            return;
        }
        String followerHeartAddr = info.getBrokerName() + BrokerHeartService.ADDRESS;
        // 注册 生产者
        BrokerHeartService heartService = BrokerHeartService.createProxy(vertx, followerHeartAddr);
        info.setBrokerHeartService(heartService);
        this.brokerInfoMap.put(info.getBrokerIp(), info);
    }

    // 轮询调用自己的兄弟，来判断，对方是不是被干掉了
    public void monitorBroker() {
        if (this.brokerInfoMap.size() == 0) {
            log.info("没有broker");
            return;
        }

        /**
         * 进行心跳通信，异步执行
         */

        this.brokerInfoMap.forEach((key, value) -> {
            BrokerInfo info = value;
            BrokerHeartService heartService = info.getBrokerHeartService();
            try {
                heartService.detection().onFailure(fail -> {
                    this.brokerInfoMap.remove(key);
                });
            } catch (Exception e) {
                this.brokerInfoMap.remove(key);
            }
        });

    }

    /**
     * 同步给大加，本broker的信息
     */
    public void sendBrokerInfo() {

        BrokerInfoData brokerInfoData = BrokerInfoData.builder()
                .brokerIp(this.localBrokerIp)
                .brokerName(this.localBrokerName)
                .build();
        eventManager.sendMessage(eventType, brokerInfoData);

    }

    public List<SpiderServerInfo> queryBrokerInfo() {
        List<SpiderServerInfo> spiderServerInfos = brokerInfoMap.values().stream().map(item -> {
            SpiderServerInfo spiderServerInfo = new SpiderServerInfo();
            spiderServerInfo.setBrokerIp(item.getBrokerIp());
            spiderServerInfo.setBrokerName(item.getBrokerName());
            return spiderServerInfo;
        }).collect(Collectors.toList());

        SpiderServerInfo spiderServerInfo = new SpiderServerInfo();
        spiderServerInfo.setBrokerName(this.localBrokerName);
        spiderServerInfo.setBrokerIp(this.localBrokerIp);
        spiderServerInfos.add(spiderServerInfo);
        return spiderServerInfos;
    }

}
