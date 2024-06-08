package cn.spider.framework.flow;

import cn.spider.framework.flow.config.SpiderCoreConfig;
import cn.spider.framework.flow.init.SpiderCoreStart;
import cn.spider.framework.flow.timer.SystemTimer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-15  12:59
 * @Description: spider-flow的vertile类
 * @Version: 1.0
 */
public class SpiderCoreVerticle extends AbstractVerticle {

    public static AbstractApplicationContext factory;

    public static Vertx clusterVertx;

    /**
     * 启动verticle
     *
     * @param startPromise
     * @throws Exception
     */
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        this.clusterVertx = vertx;
        this.factory = new AnnotationConfigApplicationContext(SpiderCoreConfig.class);
        SpiderCoreStart spiderCoreStart = this.factory.getBean(SpiderCoreStart.class);
        spiderCoreStart.noCenterInit();
        SystemTimer systemTimer = this.factory.getBean(SystemTimer.class);
        systemTimer.delayLoadResource();
        // 提供接口出来
        startPromise.complete();
    }

    /**
     * 关闭verticle
     *
     * @param stopPromise
     */
    @Override
    public void stop(Promise<Void> stopPromise) {
        SpiderCoreStart spiderCoreStart = this.factory.getBean(SpiderCoreStart.class);
        spiderCoreStart.unregister();
        factory.close();
        stopPromise.complete();
    }
}
