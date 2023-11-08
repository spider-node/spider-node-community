package cn.spider.framework.flow;

import cn.spider.framework.flow.config.SpiderCoreConfig;
import cn.spider.framework.flow.init.SpiderCoreStart;
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

       /* SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");*/
        SpiderCoreStart spiderCoreStart = this.factory.getBean(SpiderCoreStart.class);
        spiderCoreStart.noCenterInit();
        // 去中心化模式
        /*if (localMap.get("cluster_mode").equals(Constant.N0_CENTER_MODE)) {
            spiderCoreStart.noCenterInit();
        }else {
            spiderCoreStart.startComponentByLeader();
            spiderCoreStart.necessaryComponent();
        }*/
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
