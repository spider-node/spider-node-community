package cn.spider.framework.flow.funtion;

import cn.spider.framework.container.sdk.interfaces.LeaderService;
import cn.spider.framework.flow.SpiderCoreVerticle;
import cn.spider.framework.flow.init.SpiderCoreStart;
import io.vertx.core.Future;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.funtion
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-21  18:13
 * @Description: 通知spider-core从follower晋升为leader
 * @Version: 1.0
 */
@Component
public class LeaderServiceImpl implements LeaderService {
    @Override
    public Future<Void> upgrade() {
        SpiderCoreStart spiderCoreStart = SpiderCoreVerticle.factory.getBean(SpiderCoreStart.class);
        spiderCoreStart.upgrade();
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> relegation() {
        SpiderCoreStart spiderCoreStart = SpiderCoreVerticle.factory.getBean(SpiderCoreStart.class);
        spiderCoreStart.reduceFollower();
        return Future.succeededFuture();
    }
}
