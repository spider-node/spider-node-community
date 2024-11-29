package cn.spider.framework.controller.broker;

import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.BrokerInfoData;
import cn.spider.framework.common.role.BrokerRole;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.controller.broker.data.BrokerInfo;
import cn.spider.framework.controller.sdk.data.SpiderServerInfo;
import cn.spider.framework.controller.sdk.interfaces.BrokerHeartService;
import com.google.common.collect.Lists;
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

    private BrokerRole brokerRole;

    private SystemRoleManager systemRoleManager;

    /**
     * broker的brokerName
     */
    private String brokerName;

    /**
     * broker的brokerIp
     */
    private String brokerIp;


    public BrokerManager(SystemRoleManager systemRoleManager,Vertx vertx) {
        this.brokerRole = BrokerRole.FOLLOWER;
        this.systemRoleManager = systemRoleManager;
        this.brokerName = BrokerInfoUtil.queryBrokerName(vertx);
        this.brokerIp = BrokerInfoUtil.queryBrokerIp(vertx);

    }

    public List<SpiderServerInfo> queryBrokerInfo() {
        SpiderServerInfo spiderServerInfo = new SpiderServerInfo();
        spiderServerInfo.setBrokerIp(this.brokerIp);
        spiderServerInfo.setBrokerName(this.brokerName);
        return Lists.newArrayList(spiderServerInfo);
    }

    /**
     * 设置角色
     */
    public void setupLeaderRole() {
        this.brokerRole = BrokerRole.LEADER;

        starSystemLeaderRole();
    }

    /**
     * 启动系统基础角色信息
     */
    public void startBrokerBaseSystemRole() {
        // 启动broker
        systemRoleManager.startBaseSystemRole();
    }

    public void starSystemLeaderRole() {
        // 启动broker
        systemRoleManager.StartLeaderSystemRole();
    }

    // 卸载leader对应的角色
    public void destroySystemLeaderRole() {
        // 启动broker
        systemRoleManager.destroySystemLeaderRole();
    }

}
