package cn.spider.framework.controller.election;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.controller.leader.LeaderManager;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
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
    private CuratorFramework client;

    private final String ELECTION_LEADER = "/spider_node_leader";

    private Vertx vertx;

    private LeaderLatch latch;

    private LeaderManager leaderManager;

    public ElectionLeader(Vertx vertx, LeaderManager leaderManager) {
        this.vertx = vertx;
        LocalMap<String, String> localMap = this.vertx.sharedData().getLocalMap("config");
        String zk_addr = localMap.get("zk-addr");
        log.info("zk_addr:{}", zk_addr);
        this.client = CuratorFrameworkFactory.builder()
                .connectString(zk_addr)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        // 等待客户端连接成功
        while (!client.getZookeeperClient().isConnected()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Waiting for ZooKeeper connection...");
        }
        System.out.println("Connected to ZooKeeper.");
        this.leaderManager = leaderManager;
        this.latch = new LeaderLatch(client, ELECTION_LEADER);
        // 注册leader选举成功或者降级的监听器
        initMonitor();
    }

    /**
     * 竞选leader
     */
    public void election() {
        try {
            log.info("发起选举");
            this.latch.start();
            // 尝试获取领导权
            try {
                if (latch.await(30, TimeUnit.SECONDS)) { // 增加等待时间
                    System.out.println("Leadership acquired.");
                } else {
                    System.out.println("Leadership not acquired.");
                }
            } catch (Exception e) {
                System.err.println("Interrupted while waiting for leadership: " + e.getMessage());
            }
        } catch (Exception e) {
            log.error("election error", ExceptionMessage.getStackTrace(e));
        }
    }

    public void initMonitor() {
        this.latch.addListener(new LeaderLatchListener() {
            @Override
            public void isLeader() {
                log.info("---选择倒leader了");
                //启动leader责任的spider-node角色
                leaderManager.startLeader();
            }

            @Override
            public void notLeader() {
                System.out.println("I am not the leader anymore.");
                // 说明被挤掉了，卸载掉，对应的spider-node角色
                leaderManager.reduceFollower();
            }
        });

    }
}
