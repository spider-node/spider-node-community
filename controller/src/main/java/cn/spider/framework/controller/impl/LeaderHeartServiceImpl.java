package cn.spider.framework.controller.impl;

import cn.spider.framework.controller.leader.LeaderManager;
import cn.spider.framework.controller.sdk.data.FollowerInfo;
import cn.spider.framework.controller.sdk.data.QueryLeaderInfoResult;
import cn.spider.framework.controller.sdk.data.QuerySpiderServerResult;
import cn.spider.framework.controller.sdk.data.SpiderServerInfo;
import cn.spider.framework.controller.sdk.interfaces.LeaderHeartService;
import cn.spider.framework.controller.sockt.BrokerClientInfo;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.impl
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-23  11:36
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
public class LeaderHeartServiceImpl implements LeaderHeartService {

    private LeaderManager leaderManager;

    public LeaderHeartServiceImpl(LeaderManager leaderManager) {
        this.leaderManager = leaderManager;
    }

    /**
     * 检测leader是否存活
     *
     * @return
     */
    @Override
    public Future<Void> detection() {
        return Future.succeededFuture();
    }

    @Override
    public Future<JsonObject> queryLeaderInfo() {
        QueryLeaderInfoResult queryLeaderInfoResult = new QueryLeaderInfoResult();
        queryLeaderInfoResult.setBrokerIp(leaderManager.getBrokerIp());
        queryLeaderInfoResult.setBrokerName(leaderManager.getBrokerName());
        return Future.succeededFuture(JsonObject.mapFrom(queryLeaderInfoResult));
    }

    @Override
    public Future<JsonObject> querySpiderInfo() {
        List<BrokerClientInfo> clientInfos = leaderManager.queryFollowerInfo();
        List<SpiderServerInfo> serverInfoList = clientInfos.stream().map(item -> {
            SpiderServerInfo spiderServerInfo = new SpiderServerInfo();
            spiderServerInfo.setBrokerIp(item.getBrokerIp());
            spiderServerInfo.setBrokerName(item.getBrokerName());
            return spiderServerInfo;
        }).collect(Collectors.toList());
        SpiderServerInfo leaderSpiderServerInfo = new SpiderServerInfo();
        leaderSpiderServerInfo.setBrokerName(leaderManager.getBrokerName());
        leaderSpiderServerInfo.setBrokerIp(leaderManager.getBrokerIp());
        serverInfoList.add(leaderSpiderServerInfo);
        QuerySpiderServerResult querySpiderServerResult = new QuerySpiderServerResult(serverInfoList);
        return Future.succeededFuture(JsonObject.mapFrom(querySpiderServerResult));
    }

    @Override
    public Future<Void> escalationFollowerInfo(JsonObject param) {
        FollowerInfo followerInfo = param.mapTo(FollowerInfo.class);
        leaderManager.registerFollower(followerInfo);
        return Future.succeededFuture();
    }
}
