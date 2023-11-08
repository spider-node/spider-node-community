package cn.spider.framework.controller.leader;

import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.FollowerDeathData;
import cn.spider.framework.common.event.data.NotifyLeaderCommissionData;
import cn.spider.framework.common.event.data.TranscriptChangeData;
import cn.spider.framework.common.role.BrokerRole;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.container.sdk.interfaces.ContainerService;
import cn.spider.framework.container.sdk.interfaces.LeaderService;
import cn.spider.framework.controller.BrokerRoleManager;
import cn.spider.framework.controller.ControllerVerticle;
import cn.spider.framework.controller.data.RegisterLeaderRequest;
import cn.spider.framework.controller.follower.FollowerManager;
import cn.spider.framework.controller.impl.LeaderHeartServiceImpl;
import cn.spider.framework.controller.sdk.data.FollowerInfo;
import cn.spider.framework.controller.sdk.interfaces.FollowerHeartService;
import cn.spider.framework.controller.sdk.interfaces.LeaderHeartService;
import cn.spider.framework.controller.sockt.BrokerClientInfo;
import cn.spider.framework.controller.timer.ControllerTimer;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import io.vertx.serviceproxy.ServiceBinder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.leader
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-19  19:04
 * @Description: leader的管理
 * @Version: 1.0
 */
@Slf4j
public class LeaderManager {

    private Map<String, BrokerClientInfo> followerMap;

    private EventManager eventManager;

    private String brokerName;

    private String brokerIp;

    private Integer transcriptNum;

    private ServiceBinder binder;

    /**
     * 保存 follower
     */
    private Map<String, FollowerHeartService> followerHeartServiceMap;

    /**
     * 报错 副本的隐射关系
     */
    private Map<String, Set<String>> transcriptRelationMap;

    private Vertx vertx;

    private ControllerTimer timer;

    private BrokerRoleManager brokerRoleManager;

    private MessageConsumer<JsonObject> leaderConsumer;

    private LeaderService leaderService;

    public String getBrokerName() {
        return brokerName;
    }

    public String getBrokerIp() {
        return brokerIp;
    }

    public LeaderManager(EventManager eventManager, Vertx vertx, ControllerTimer timer, BrokerRoleManager brokerRoleManager) {
        this.followerMap = Maps.newHashMap();
        this.eventManager = eventManager;
        this.brokerName = BrokerInfoUtil.queryBrokerName(vertx);
        this.brokerIp = BrokerInfoUtil.queryBrokerIp(vertx);
        this.timer = timer;
        // 获取集群配置的副本数量
        this.transcriptNum = BrokerInfoUtil.queryTranscriptNum(vertx);
        this.binder = new ServiceBinder(vertx);
        this.followerHeartServiceMap = Maps.newHashMap();
        this.transcriptRelationMap = Maps.newHashMap();
        this.vertx = vertx;
        this.brokerRoleManager = brokerRoleManager;
        String leaderServiceAddr = this.brokerName + LeaderService.ADDRESS;
        this.leaderService = LeaderService.createProxy(vertx, leaderServiceAddr);
    }

    public List<BrokerClientInfo> queryFollowerInfo() {
        return this.followerMap.values().stream().collect(Collectors.toList());
    }

    public void init() {
        log.info("发布监听接口的ip {}", this.brokerIp);
        // 发起接口监听
        log.info("服务器启动成功");
        // 通知 集群各个节点，我是leader
        notifyFollowerMyIsLeader();
        // 注册 -leader
        registerLeaderHeartConsumer();
        // 注册延迟。通知大家，我是leader
       // this.timer.notifyMeIsLeader();
        // leader跟大家通信
        //this.timer.leaderCommunicationFollower();
        // 设置本届点为leader
        brokerRoleManager.setUp(BrokerRole.LEADER);
    }

    /**
     * 停止本节点为leader的身份
     */
    public void stop() {
        this.followerMap.clear();
        this.followerHeartServiceMap.clear();
        if (Objects.nonNull(this.leaderConsumer)) {
            // 卸载该服务提供的能力
            leaderConsumer.unregister();
        }
        //this.timer.cancelMeIsLeader();
        //this.timer.cancelCommunicationFollower();
    }

    /**
     * 降级为follower
     */
    public void reduceFollower() {
        FollowerManager followerManager = ControllerVerticle.factory.getBean(FollowerManager.class);
        // 移除本身具备的能力
        this.stop();
        // 降级
        this.leaderService.relegation();
        // 初始化follower的能力
        followerManager.init();
    }


    private void registerLeaderHeartConsumer() {
        LeaderHeartService leaderHeartService = new LeaderHeartServiceImpl(this);
        String leaderHeartAddr = LeaderHeartService.ADDRESS;
        // 发布 LeaderHeartService的消费者
        this.leaderConsumer = this.binder.setAddress(leaderHeartAddr)
                .register(LeaderHeartService.class, leaderHeartService);
    }

    /**
     * follower上报信息
     *
     * @param followerInfo
     */
    public void registerFollower(FollowerInfo followerInfo) {
        String ip = followerInfo.getFollowerIp();
        log.info("follower-加入集群 ip {}", followerInfo.getFollowerIp());
        if (followerMap.containsKey(ip)) {
            return;
        }
        BrokerClientInfo info = BrokerClientInfo.builder()
                .brokerIp(followerInfo.getFollowerIp())
                .brokerName(followerInfo.getFollowerName())
                .virtuallyIp(ip)
                .build();
        followerMap.put(info.getVirtuallyIp(), info);

        String followerHeartAddr = followerInfo.getFollowerName() + FollowerHeartService.ADDRESS;
        // 注册 生产者
        FollowerHeartService heartService = FollowerHeartService.createProxy(vertx, followerHeartAddr);
        this.followerHeartServiceMap.put(followerInfo.getFollowerIp(), heartService);
    }

    public void monitor() {
        if (this.followerHeartServiceMap.size() == 0) {
            log.info("没有follower");
            return;
        }
        /**
         * 进行心跳通信，如果失败就移除（正常的情况下，下次监听会注册上来）
         */
        this.followerHeartServiceMap.forEach((key, value) -> {
            FollowerHeartService heartService = value;
            try {
                heartService.detection().onFailure(fail -> {
                    this.followerHeartServiceMap.remove(key);
                    this.followerMap.remove(key);
                });
            } catch (Exception e) {
                this.followerHeartServiceMap.remove(key);
                this.followerMap.remove(key);
            }
        });
    }


    /**
     * 通知集群其他存活节点，有节点挂了
     *
     * @param brokerClientInfo
     */
    public void notifyAllFollowerLeave(BrokerClientInfo brokerClientInfo) {
        FollowerDeathData followerDeathData = FollowerDeathData.builder()
                .brokerName(brokerClientInfo.getBrokerName())
                .build();
        // 跟该节点的follower进行通信-失败的情况下,进行集群通知
        eventManager.sendMessage(EventType.FOLLOWER_DEATH, followerDeathData);
    }

    /**
     * 副本分配- 副本分配的时机
     */
    public void allocateReplicas(String brokerName, Boolean isRegister) {
        if (this.transcriptNum == 0) {
            return;
        }
        // 注册
        if (isRegister) {
            // 说明是新注册的
            if (!this.transcriptRelationMap.containsKey(brokerName)) {
                this.transcriptRelationMap.put(brokerName, new HashSet<>(this.transcriptNum));
            }
            Set<String> transcriptKeys = this.transcriptRelationMap.keySet();
            for (String key : transcriptKeys) {
                Set<String> transcripts = this.transcriptRelationMap.get(key);
                int size = transcripts.size();
                if (size >= 2 || StringUtils.equals(key, brokerName) || transcripts.contains(brokerName)) {
                    continue;
                }
                transcripts.add(brokerName);
                break;
            }
            // 构建自己的副本
            Set<String> transcripts = this.transcriptRelationMap.get(brokerName);
            if (transcripts.size() >= 2) {
                return;
            }
            Collection<Set<String>> brokerNames = this.transcriptRelationMap.values();
            for (String key : transcriptKeys) {
                Integer appear = 0;
                for (Set<String> value : brokerNames) {
                    if (value.contains(key)) {
                        appear++;
                    }
                }
                if (appear < 2) {
                    transcripts.add(key);
                    if (transcripts.size() >= 2) {
                        return;
                    }
                }
            }
            sendTranscriptChange();
            // 重发事件
            return;
        }
        this.transcriptRelationMap.remove(brokerName);

        this.transcriptRelationMap.forEach((key, value) -> {
            Set<String> brokerNames = value;
            brokerNames.remove(brokerName);
        });
        sendTranscriptChange();
        // 重发时间
    }

    /**
     * 副本变化发送相关事件
     */
    private void sendTranscriptChange() {
        this.transcriptRelationMap.forEach((key, value) -> {
            eventManager.sendMessage(EventType.TRANSCRIPT_CHANGE, TranscriptChangeData.builder()
                    .transcript(value).brokerName(key)
                    .build());
        });
    }

    /**
     * 通知follower我是leader
     */
    public void notifyFollowerMyIsLeader() {
        NotifyLeaderCommissionData commissionData = NotifyLeaderCommissionData.builder()
                .brokerName(this.brokerName)
                .brokerIp(this.brokerIp)
                .build();
        // 通知follower-> 我是当前集群的leader
        eventManager.sendMessage(EventType.LEADER_GENERATE, commissionData);
    }
}
