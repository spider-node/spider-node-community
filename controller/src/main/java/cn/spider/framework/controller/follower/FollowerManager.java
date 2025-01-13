package cn.spider.framework.controller.follower;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.NotifyLeaderCommissionData;
import cn.spider.framework.common.role.BrokerRole;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.container.sdk.interfaces.LeaderService;
import cn.spider.framework.controller.BrokerRoleManager;
import cn.spider.framework.controller.ControllerVerticle;
import cn.spider.framework.controller.broker.BrokerManager;
import cn.spider.framework.controller.impl.FollowerHeartServiceImpl;
import cn.spider.framework.controller.leader.Leader;
import cn.spider.framework.controller.leader.LeaderManager;
import cn.spider.framework.controller.sdk.data.FollowerInfo;
import cn.spider.framework.controller.sdk.data.QueryLeaderInfoResult;
import cn.spider.framework.controller.sdk.interfaces.FollowerHeartService;
import cn.spider.framework.controller.sdk.interfaces.LeaderHeartService;
import cn.spider.framework.controller.timer.ControllerTimer;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.serviceproxy.ServiceBinder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.follower
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-19  19:04
 * @Description: 集群内-追随者管理
 * @Version: 1.0
 */
@Slf4j
public class FollowerManager {

    private Leader leader;
    private String followerName;

    private String followerIp;

    private EventBus eventBus;

    // 监听leader创建事件
    private MessageConsumer<String> consumerLeaderCreate;
    private ServiceBinder binder;

    private MessageConsumer<JsonObject> followerHeartConsumer;

    private LeaderHeartService leaderHeartService;

    private BrokerRoleManager brokerRoleManager;

    private BrokerManager brokerManager;

    private ControllerTimer timer;

    private Boolean isStart;

    public FollowerManager(Vertx vertx,
                           LeaderHeartService leaderHeartService,
                           BrokerRoleManager brokerRoleManager,
                           ControllerTimer timer,
                           EventBus eventBus, BrokerManager brokerManager) {
        this.followerIp = BrokerInfoUtil.queryBrokerIp(vertx);
        this.followerName = BrokerInfoUtil.queryBrokerName(vertx);
        this.eventBus = eventBus;
        this.timer = timer;
        this.binder = new ServiceBinder(vertx);
        this.leaderHeartService = leaderHeartService;
        this.brokerRoleManager = brokerRoleManager;
        this.isStart = false;
        this.brokerManager = brokerManager;
        this.leader = Leader.builder().build();
    }

    /**
     * 容器初始化完成之后初始化的内容
     */
    public void init() {
        // 基础需要启动的系统角色
        brokerManager.startBrokerBaseSystemRole();
        // 注册对leader事件的监听
        informLeaderConsumer();
    }

    public void startFollower() {
        if (this.isStart) {
            return;
        }
        log.info("follower-init {}", this.followerIp);
        // 告知本节点为FOLLOWER
        brokerRoleManager.setUp(BrokerRole.FOLLOWER);
        // 获取leader信息
        leaderConnect();
        // 注册 跟leader通信
        timer.registerFollowerVisitLeader();
        // 注册 FollowerHeartService服务
        String followerHeartAddr = this.followerName + FollowerHeartService.ADDRESS;
        FollowerHeartService followerHeartService = new FollowerHeartServiceImpl();
        this.followerHeartConsumer = this.binder.setAddress(followerHeartAddr)
                .register(FollowerHeartService.class, followerHeartService);
        // 设置follower的启动为true
        this.isStart = true;
    }

    /**
     * 调用该方法说明已经放弃了 follower已经升级为leader
     */
    public void stop() {
        // 撤销上报到leader信息
        timer.cancelFollowerVisit();
        // 卸载 followerHeartConsumer的服务
        this.followerHeartConsumer.unregister();
        // 重新定义-follower为false，为了下次可能升级为leader
        this.isStart = false;
    }

    /**
     * 注册监听-leader创建的事件
     */
    public void informLeaderConsumer() {
        this.consumerLeaderCreate = eventBus.consumer(EventType.LEADER_GENERATE.queryAddr());
        this.consumerLeaderCreate.handler(message -> {
            MultiMap multiMap = message.headers();
            String brokerName = multiMap.get(Constant.BROKER_NAME);
            if (StringUtils.equals(brokerName, this.followerName)) {
                return;
            }
            //startFollower();
            //log.info("接受到leader的信息为 {}", message.body());
            NotifyLeaderCommissionData commissionData = JSON.parseObject(message.body(), NotifyLeaderCommissionData.class);
            if (StringUtils.isNotEmpty(this.leader.getBrokerIp()) && this.leader.getBrokerIp().equals(commissionData.getBrokerIp())) {
                log.info("接受到leader事件-结束通知操作 {}", message.body());
                return;
            }
            log.info("接受到leader事件 {}", message.body());
            this.leader.setBrokerIp(commissionData.getBrokerIp());
            this.leader.setBrokerName(commissionData.getBrokerName());
            brokerManager.setLeader(this.leader);
        });
    }

    public void unregisterInformLeaderConsumer() {
        this.consumerLeaderCreate.unregister();
    }

    public void leaderConnect() {
        leaderHeartService.queryLeaderInfo().onSuccess(suss -> {
            QueryLeaderInfoResult leaderInfoResult = suss.mapTo(QueryLeaderInfoResult.class);
            //log.info("follower-leader-info {}", suss.toString());
            this.leader = Leader.builder()
                    .brokerIp(leaderInfoResult.getBrokerIp())
                    .brokerName(leaderInfoResult.getBrokerName())
                    .build();
        }).onFailure(fail -> {
            // 通信失败,以为着
            campaignLeader();
        });
    }

    public void keepLeaderHeart() {
        leaderHeartService.queryLeaderInfo()
                .onSuccess(suss -> {
                    QueryLeaderInfoResult leaderInfoResult = suss.mapTo(QueryLeaderInfoResult.class);
                    if (!StringUtils.equals(leaderInfoResult.getBrokerIp(), this.leader.getBrokerIp())) {
                        return;
                    }
                    FollowerInfo followerInfo = new FollowerInfo();
                    followerInfo.setFollowerIp(followerIp);
                    followerInfo.setFollowerName(followerName);
                    // 上报
                    leaderHeartService.escalationFollowerInfo(JsonObject.mapFrom(followerInfo));
                })
                .onFailure(fail -> {
                    campaignLeader();
                });
    }

    public void campaignLeader() {

    }

    public String queryLeaderInfo() {
        if (Objects.isNull(this.leader)) {
            return null;
        }
        return this.leader.getBrokerName();
    }

    public Leader queryLeader() {
        return this.leader;
    }

}
