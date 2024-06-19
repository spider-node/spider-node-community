package cn.spider.framework.controller.election;

import cn.spider.framework.common.role.BrokerRole;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.controller.BrokerRoleManager;
import cn.spider.framework.controller.follower.FollowerManager;
import cn.spider.framework.controller.leader.LeaderManager;
import cn.spider.framework.controller.sdk.interfaces.LeaderHeartService;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.election
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-20  15:33
 * @Description: 选举leader
 * @Version: 1.0
 */
@Slf4j
public class ElectionLeader {

   // private LeaderManager leaderManager;

   // private FollowerManager followerManager;

   // private LeaderHeartService leaderHeartService;

    private final String ELECTION_LEADER = "ELECTION_LEADER";

    private Vertx vertx;

    private BrokerRoleManager brokerRoleManager;


    public ElectionLeader(Vertx vertx,
                          //LeaderManager leaderManager,
                          //FollowerManager followerManager,
                          //LeaderHeartService leaderHeartService,
                          BrokerRoleManager brokerRoleManager) {

        this.vertx = vertx;
       // this.leaderManager = leaderManager;
       // this.followerManager = followerManager;
       // this.leaderHeartService = leaderHeartService;
        this.brokerRoleManager = brokerRoleManager;
    }

    /**
     * 精选leader
     */
    public Future<Void> election() {
        return null;
    }
}
