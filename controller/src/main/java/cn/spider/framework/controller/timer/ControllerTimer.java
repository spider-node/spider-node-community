package cn.spider.framework.controller.timer;

import cn.spider.framework.controller.ControllerVerticle;
import cn.spider.framework.controller.broker.BrokerManager;
import cn.spider.framework.controller.follower.FollowerManager;
import cn.spider.framework.controller.leader.LeaderManager;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

import java.util.Objects;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.timer
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-18  11:34
 * @Description:
 * @Version: 1.0
 */
public class ControllerTimer {
    private Vertx vertx;

   /* private FollowerManager followerManager;

    private LeaderManager leaderManager;

    private Long followerVisitId;

    private Long leaderPollId;

    private Long leaderCommunicationId;*/

    /**
     * broker管理者
     */
    private BrokerManager brokerManager;

    public ControllerTimer(Vertx vertx,BrokerManager brokerManager) {
        this.vertx = vertx;
        this.brokerManager = brokerManager;
    }

    /**
     * 轮询去探访leader
     */
    /*public void registerFollowerVisitLeader() {
        if (Objects.isNull(followerManager)) {
            this.followerManager = ControllerVerticle.factory.getBean(FollowerManager.class);
        }
        this.followerVisitId = this.vertx.setPeriodic(4000, handler -> {
            // 查询leader的信息
            this.followerManager.keepLeaderHeart();
        });
    }

    *//**
     * 当本节点升级leader之后，撤销
     *//*
    public void cancelFollowerVisit() {
        if (Objects.isNull(this.followerVisitId)) {
            return;
        }
        this.vertx.cancelTimer(followerVisitId);
    }

    *//***
     * 定时发事件告诉大家，我是leader
     *//*
    public void notifyMeIsLeader() {
        this.leaderPollId = this.vertx.setPeriodic(10 * 1000, handler -> {
            // 查询leader的信息
            if (Objects.isNull(this.leaderManager)) {
                this.leaderManager = ControllerVerticle.factory.getBean(LeaderManager.class);
            }
            // 通知大家我是leader
            this.leaderManager.notifyFollowerMyIsLeader();
        });
    }


    *//**
     * 当本节点降级之后，撤销consumer
     *//*
    public void cancelMeIsLeader() {
        if (Objects.isNull(this.leaderPollId)) {
            return;
        }
        this.vertx.cancelTimer(leaderPollId);
    }

    *//**
     *
     *//*
    public void leaderCommunicationFollower() {
        this.leaderCommunicationId = this.vertx.setPeriodic(5000, handler -> {
            if (Objects.isNull(this.leaderManager)) {
                this.leaderManager = ControllerVerticle.factory.getBean(LeaderManager.class);
            }
            this.leaderManager.monitor();
        });
    }

    public void cancelCommunicationFollower() {
        if (Objects.isNull(this.leaderCommunicationId)) {
            return;
        }
        this.vertx.cancelTimer(this.leaderCommunicationId);
    }*/

    public void monitorBroker() {
        this.vertx.setPeriodic(20 * 1000, handler -> {
            this.brokerManager.monitorBroker();
        });
    }

    public void sendBrokerInfo() {
        this.vertx.setPeriodic(15 * 1000, handler -> {
            this.brokerManager.sendBrokerInfo();
        });
    }
}
