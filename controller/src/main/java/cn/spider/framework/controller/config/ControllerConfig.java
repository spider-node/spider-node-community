package cn.spider.framework.controller.config;

import cn.spider.framework.common.event.EventConfig;
import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.controller.BrokerRoleManager;
import cn.spider.framework.controller.ControllerVerticle;
import cn.spider.framework.controller.broker.BrokerManager;
import cn.spider.framework.controller.broker.SystemRoleManager;
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
import org.springframework.context.annotation.ComponentScan;
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
@ComponentScan("cn.spider.framework.controller.*")
@Configuration
public class ControllerConfig {
    @Bean
    public Vertx buildVertx() {
        return ControllerVerticle.clusterVertx;
    }

    @Bean
    public FollowerManager buildFollowerManager(Vertx vertx,
                                                LeaderHeartService leaderHeartService,
                                                BrokerRoleManager brokerRoleManager,
                                                ControllerTimer timer,
                                                EventBus eventBus,BrokerManager brokerManager) {
        return new FollowerManager(vertx, leaderHeartService, brokerRoleManager, timer, eventBus,brokerManager);
    }

    @Bean
    public LeaderManager buildLeaderManager(EventManager eventManager, Vertx vertx, ControllerTimer timer, BrokerRoleManager brokerRoleManager, BrokerManager brokerManager) {
        return new LeaderManager(eventManager, vertx, timer, brokerRoleManager, brokerManager);
    }

    @Bean
    public NetServer createNetServer(Vertx vertx) {
        NetServer server = vertx.createNetServer();
        return server;
    }


    @Bean
    public ElectionLeader buildElectionLeader(Vertx vertx,
                                              LeaderManager leaderManager) {
        return new ElectionLeader(vertx, leaderManager);
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
    public ControllerTimer buildControllerTimer(Vertx vertx, BrokerManager brokerManager) {
        return new ControllerTimer(vertx, brokerManager);
    }

    @Bean
    public EventBus buildEventBus(Vertx vertx) {
        return vertx.eventBus();
    }

    @Bean
    public BrokerManager buildBrokerManager(SystemRoleManager systemRoleManager,Vertx vertx) {
        return new BrokerManager(systemRoleManager,vertx);
    }

    @Bean
    public SystemRoleManager buildSystemRoleManager(Vertx vertx) {
        return new SystemRoleManager(vertx);
    }

    @Bean
    public BrokerHeartService buildBrokerHeartService() {
        return new BrokerHeartServiceImpl();
    }

    @Bean
    public BrokerInfoService buildBrokerInfoService(BrokerManager brokerManager) {
        return new BrokerInfoServiceImpl(brokerManager);
    }

}
