package cn.spider.framework.controller.impl;

import cn.spider.framework.controller.ControllerVerticle;
import cn.spider.framework.controller.follower.FollowerManager;
import cn.spider.framework.controller.sdk.interfaces.FollowerHeartService;
import io.vertx.core.Future;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.impl
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-23  11:30
 * @Description: TODO
 * @Version: 1.0
 */
public class FollowerHeartServiceImpl implements FollowerHeartService {

    /**
     * 直接返回成功用于 检测是否存货
     *
     * @return
     */
    @Override
    public Future<Void> detection() {
        return Future.succeededFuture();
    }

    /**
     * 重新跟leader建立链接
     * @return
     */
    @Override
    public Future<Void> reconnectLeader() {
        FollowerManager followerManager = ControllerVerticle.factory.getBean(FollowerManager.class);
        followerManager.leaderConnect();
        return Future.succeededFuture();
    }
}
