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
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.serviceproxy.ServiceBinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

@Slf4j
public class ControllerVerticle extends AbstractVerticle {

    public static AbstractApplicationContext factory;

    public static Vertx clusterVertx;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        this.clusterVertx = vertx;
        this.factory = new AnnotationConfigApplicationContext(ControllerConfig.class);

        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");
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
        binder.setAddress(brokerInfoAddr)
                .register(BrokerInfoService.class, brokerInfoService);
        //log.info("启动的模式为 {}",localMap.get("cluster_mode"));
        startPromise.complete();

        // 去中心化模式
        /*if (localMap.get("cluster_mode").equals(Constant.N0_CENTER_MODE)) {

        } else {
            ElectionLeader electionLeader = this.factory.getBean(ElectionLeader.class);
            RoleService roleService = this.factory.getBean(RoleService.class);


            String roleAddr = BrokerInfoUtil.queryBrokerName(vertx) + LeaderHeartService.ADDRESS;

            // 发布具体的信息
            binder.setAddress(roleAddr)
                    .register(RoleService.class, roleService);
            // 进行选举
            Future<Void> future = electionLeader.election();
            future.onSuccess(suss -> {
                BrokerRoleManager brokerRoleManager = this.factory.getBean(BrokerRoleManager.class);
                log.info("选举的角色为 {}", brokerRoleManager.queryBrokerRole());
                startPromise.complete();
            }).onFailure(fail -> {
                startPromise.fail(ExceptionMessage.getStackTrace(fail));
            });
        }*/
    }

    /**
     * 关闭verticle
     *
     * @param stopPromise
     */
    @Override
    public void stop(Promise<Void> stopPromise) {
        stopPromise.complete();
        factory.close();
    }
}
