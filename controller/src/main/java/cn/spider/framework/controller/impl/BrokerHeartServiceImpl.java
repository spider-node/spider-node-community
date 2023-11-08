package cn.spider.framework.controller.impl;
import cn.spider.framework.controller.sdk.interfaces.BrokerHeartService;
import io.vertx.core.Future;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.impl
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-18  18:21
 * @Description: TODO
 * @Version: 1.0
 */
public class BrokerHeartServiceImpl implements BrokerHeartService {
    /**
     * 用于心跳机制
     * @return
     */
    @Override
    public Future<Void> detection() {
        return Future.succeededFuture();
    }
}
