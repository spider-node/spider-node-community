package cn.spider.framework.controller;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.role.BrokerRole;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.controller.config.ControllerConfig;
import cn.spider.framework.controller.election.ElectionLeader;
import cn.spider.framework.controller.sdk.interfaces.BrokerHeartService;
import cn.spider.framework.controller.sdk.interfaces.BrokerInfoService;
import cn.spider.framework.controller.sdk.interfaces.LeaderHeartService;
import cn.spider.framework.controller.sdk.interfaces.RoleService;
import cn.spider.framework.controller.timer.ControllerTimer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.serviceproxy.ServiceBinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ControllerVerticle extends AbstractVerticle {

    public static AbstractApplicationContext factory;

    public static Vertx clusterVertx;

    private List<MessageConsumer<JsonObject>> containerConsumers;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        this.clusterVertx = vertx;
        this.factory = new AnnotationConfigApplicationContext(ControllerConfig.class);
        this.containerConsumers = new ArrayList<>();
        ServiceBinder binder = new ServiceBinder(vertx);

        ControllerTimer controllerTimer = this.factory.getBean(ControllerTimer.class);
        controllerTimer.sendBrokerInfo();
        controllerTimer.monitorBroker();
        // 发布心跳接口
        BrokerHeartService heartService = this.factory.getBean(BrokerHeartService.class);
        String brokerHeartAddr = BrokerInfoUtil.queryBrokerName(vertx) + BrokerHeartService.ADDRESS;
        binder.setAddress(brokerHeartAddr)
                .register(BrokerHeartService.class, heartService);
        // 发布获取brokerInfo接口
        BrokerInfoService brokerInfoService = this.factory.getBean(BrokerInfoService.class);
        String brokerInfoAddr = BrokerInfoService.ADDRESS;
        MessageConsumer<JsonObject> consumer = binder.setAddress(brokerInfoAddr)
                .register(BrokerInfoService.class, brokerInfoService);
        this.containerConsumers.add(consumer);
        //log.info("启动的模式为 {}",localMap.get("cluster_mode"));
        startPromise.complete();
    }

    /**
     * 关闭verticle
     *
     * @param stopPromise
     */
    @Override
    public void stop(Promise<Void> stopPromise) {
        for (MessageConsumer<JsonObject> consumer : containerConsumers) {
            consumer.unregister();
        }
        factory.close();
        stopPromise.complete();
    }
}
