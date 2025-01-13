package cn.spider.framework.controller.broker;

import cn.spider.framework.common.role.BrokerRole;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.controller.leader.Leader;
import cn.spider.framework.controller.sdk.data.SpiderServerInfo;
import com.google.common.collect.Lists;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

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

    private Leader leader;


    public BrokerManager(SystemRoleManager systemRoleManager,Vertx vertx) {
        this.brokerRole = BrokerRole.FOLLOWER;
        this.systemRoleManager = systemRoleManager;
        this.brokerName = BrokerInfoUtil.queryBrokerName(vertx);
        this.brokerIp = BrokerInfoUtil.queryBrokerIp(vertx);

    }

    // 返回leader
    public List<SpiderServerInfo> queryBrokerInfo() {
        // 获取leader进行返回
        SpiderServerInfo spiderServerInfo = new SpiderServerInfo();
        if(brokerRole.equals(BrokerRole.LEADER)){
            spiderServerInfo.setBrokerIp(this.brokerIp);
            spiderServerInfo.setBrokerName(this.brokerName);
            return Lists.newArrayList(spiderServerInfo);
        }
        spiderServerInfo.setBrokerIp(leader.getBrokerIp());
        spiderServerInfo.setBrokerName(leader.getBrokerName());
       return Lists.newArrayList(spiderServerInfo);
    }

    public void setLeader(Leader leader) {
        this.leader = leader;
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
