package cn.spider.framework.controller.config;

import cn.spider.framework.common.event.EventConfig;
import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.controller.BrokerRoleManager;
import cn.spider.framework.controller.ControllerVerticle;
import cn.spider.framework.controller.broker.BrokerManager;
import cn.spider.framework.controller.consumer.AcceptLeaderInfoHandler;
import cn.spider.framework.controller.consumer.BrokerInfoAsyncHandler;
import cn.spider.framework.controller.consumer.CerebralFissureHandler;
import cn.spider.framework.controller.election.ElectionLeader;
import cn.spider.framework.controller.follower.FollowerManager;
import cn.spider.framework.controller.impl.BrokerHeartServiceImpl;
import cn.spider.framework.controller.impl.BrokerInfoServiceImpl;
import cn.spider.framework.controller.impl.RoleServiceImpl;
import cn.spider.framework.controller.leader.LeaderManager;
import cn.spider.framework.controller.sdk.interfaces.BrokerHeartService;
import cn.spider.framework.controller.sdk.interfaces.BrokerInfoService;
import cn.spider.framework.controller.sdk.interfaces.LeaderHeartService;
import cn.spider.framework.controller.sdk.interfaces.RoleService;
import cn.spider.framework.controller.timer.ControllerTimer;
import cn.spider.framework.db.config.DbRocksConfig;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.net.NetServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.config
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-20  16:34
 * @Description:
 * @Version: 1.0
 */
@Import({EventConfig.class, DbRocksConfig.class})
@Configuration
public class ControllerConfig {
    @Bean
    public Vertx buildVertx() {
        return ControllerVerticle.clusterVertx;
    }

    public FollowerManager buildFollowerManager(Vertx vertx,
                                                LeaderHeartService leaderHeartService,
                                                BrokerRoleManager brokerRoleManager,
                                                ControllerTimer timer,
                                                EventBus eventBus,
                                                EventManager eventManager) {
        return new FollowerManager(vertx, leaderHeartService, brokerRoleManager, timer, eventBus, eventManager);
    }

    public LeaderManager buildLeaderManager(EventManager eventManager, Vertx vertx, ControllerTimer timer, BrokerRoleManager brokerRoleManager) {
        return new LeaderManager(eventManager, vertx, timer, brokerRoleManager);
    }

    @Bean
    public NetServer createNetServer(Vertx vertx) {
        NetServer server = vertx.createNetServer();
        return server;
    }



    public ElectionLeader buildElectionLeader(Vertx vertx,
                                             // FollowerManager followerManager,
                                              //LeaderManager leaderManager,
                                              // LeaderHeartService leaderHeartService,
                                              BrokerRoleManager brokerRoleManager
    ) {
        return new ElectionLeader(vertx, brokerRoleManager);
    }

    @Bean
    public LeaderHeartService buildLeaderHeartService(Vertx vertx) {
        String addr = LeaderHeartService.ADDRESS;
        return LeaderHeartService.createProxy(vertx, addr);
    }

    @Bean
    public BrokerRoleManager buildBrokerRoleManager() {
        return new BrokerRoleManager();
    }

    @Bean
    public RoleService buildRoleService(BrokerRoleManager brokerRoleManager) {
        return new RoleServiceImpl(brokerRoleManager);
    }

    @Bean
    public ControllerTimer buildControllerTimer(Vertx vertx,BrokerManager brokerManager) {
        return new ControllerTimer(vertx,brokerManager);
    }

    @Bean
    public EventBus buildEventBus(Vertx vertx) {
        return vertx.eventBus();
    }


    @Bean
    public CerebralFissureHandler buildCerebralFissureHandler(EventBus eventBus, BrokerRoleManager brokerRoleManager) {
        return new CerebralFissureHandler(eventBus, brokerRoleManager);
    }

    @Bean
    public AcceptLeaderInfoHandler buildAcceptLeaderInfoHandler(EventBus eventBus,
                                                                EventManager eventManager,
                                                                BrokerRoleManager brokerRoleManager,
                                                                Vertx vertx) {
        return new AcceptLeaderInfoHandler(eventBus, eventManager, brokerRoleManager, vertx);

    }

    @Bean
    public BrokerManager buildBrokerManager(Vertx vertx, EventManager eventManager){
        return new BrokerManager(vertx,eventManager);
    }

    @Bean
    public BrokerHeartService buildBrokerHeartService(){
        return new BrokerHeartServiceImpl();
    }

    @Bean
    public BrokerInfoAsyncHandler buildBrokerInfoAsyncHandler(EventBus eventBus, BrokerManager brokerManager, Vertx vertx){
        return new BrokerInfoAsyncHandler(eventBus,brokerManager,vertx);
    }

    @Bean
    public BrokerInfoService buildBrokerInfoService(BrokerManager brokerManager){
        return new BrokerInfoServiceImpl(brokerManager);
    }

}
