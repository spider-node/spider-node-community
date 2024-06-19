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

    private NetClient client;

    private String followerName;

    private String followerIp;

    private EventBus eventBus;

    // 监听leader创建事件
    private MessageConsumer<String> consumerLeaderCreate;

    private LeaderService leaderService;

    private Vertx vertx;

    private ServiceBinder binder;

    private MessageConsumer<JsonObject> followerHeartConsumer;

    private LeaderHeartService leaderHeartService;

    private BrokerRoleManager brokerRoleManager;

    private ControllerTimer timer;

    private EventManager eventManager;


    public FollowerManager(Vertx vertx,
                           LeaderHeartService leaderHeartService,
                           BrokerRoleManager brokerRoleManager,
                           ControllerTimer timer,
                           EventBus eventBus,
                           EventManager eventManager) {
        NetClientOptions options = new NetClientOptions()
                .setLogActivity(true)
                .setConnectTimeout(10000);
        this.client = vertx.createNetClient(options);
        this.followerIp = BrokerInfoUtil.queryBrokerIp(vertx);
        this.followerName = BrokerInfoUtil.queryBrokerName(vertx);
        this.vertx = vertx;
        this.eventBus = eventBus;
        this.timer = timer;
        this.eventManager = eventManager;
        String leaderServiceAddr = this.followerName + LeaderService.ADDRESS;
        this.leaderService = LeaderService.createProxy(vertx, leaderServiceAddr);
        this.binder = new ServiceBinder(vertx);
        this.leaderHeartService = leaderHeartService;
        this.brokerRoleManager = brokerRoleManager;
    }

    public void init() {
        informLeaderConsumer();
        log.info("follower-init {}", this.followerIp);
        // 告知本节点为FOLLOWER
        brokerRoleManager.setUp(BrokerRole.FOLLOWER);
        // 获取leader信息
        leaderConnect();
        //// 向leader上报
       // timer.registerFollowerVisitLeader();
        // 注册
        String followerHeartAddr = this.followerName + FollowerHeartService.ADDRESS;
        FollowerHeartService followerHeartService = new FollowerHeartServiceImpl();
        this.followerHeartConsumer = this.binder.setAddress(followerHeartAddr)
                .register(FollowerHeartService.class, followerHeartService);
    }

    public void stop() {
        //this.timer.cancelFollowerVisit();
        // 取消监听
        this.consumerLeaderCreate.unregister();
        this.leader = Leader.builder()
                .brokerName(this.followerName)
                .brokerIp(this.followerIp)
                .build();
        // 卸载
        this.followerHeartConsumer.unregister();
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
            //log.info("接受到leader的信息为 {}", message.body());
            NotifyLeaderCommissionData commissionData = JSON.parseObject(message.body(), NotifyLeaderCommissionData.class);
            if (Objects.nonNull(this.leader) && this.leader.getBrokerIp().equals(commissionData.getBrokerIp())) {
                return;
            }
            this.leader = Leader.builder()
                    .brokerIp(commissionData.getBrokerIp())
                    .brokerName(commissionData.getBrokerName())
                    .build();
        });
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


    /**
     * 监听关闭-说明leader断开
     */
    public void monitorSocket() {
        // 监听客户端的退出连接
        leader.getSocket().closeHandler(close -> {
            log.info("leader-断开 {}", leader.getBrokerIp());
            leader.setSocket(null);
            // leader已经断开
            campaignLeader();
        });
    }

    /**
     * 重新选举leader
     */
    public void campaignLeader() {

    }

    public void upgradeLeader() {
        try {
            log.info("竞争leader-suss {}", followerIp);
            // 直接设置- 晋升为leader
            // 启动当前节点的leader角色
            LeaderManager leaderManager = ControllerVerticle.factory.getBean(LeaderManager.class);
            leaderManager.init();
            brokerRoleManager.setUp(BrokerRole.LEADER);
            // 关闭follower信息
            this.stop();
            log.info("竞争leader-释放锁 {}", followerIp);
        } catch (BeansException e) {
            log.info("竞争leader-fail {}", ExceptionMessage.getStackTrace(e));
            log.info("竞争leader-释放锁 {}", followerIp);
            return;
        }
        Future<Void> upgrade = leaderService.upgrade();
        upgrade.onSuccess(upgradeSuss -> {
            log.info("重置leader-suss-broker {}", BrokerInfoUtil.queryBrokerName(vertx));
        }).onFailure(fail -> {
            log.error("重置leader-fail {}", ExceptionMessage.getStackTrace(fail));
        });
    }

    public String queryLeaderInfo(){
        if(Objects.isNull(this.leader)){
            return null;
        }
        return this.leader.getBrokerName();
    }

}
