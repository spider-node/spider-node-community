package cn.spider.framework.flow.init;

import cn.spider.framework.common.role.BrokerRole;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.container.sdk.interfaces.BusinessService;
import cn.spider.framework.container.sdk.interfaces.ContainerService;
import cn.spider.framework.container.sdk.interfaces.FlowService;
import cn.spider.framework.container.sdk.interfaces.LeaderService;
import cn.spider.framework.controller.sdk.data.QueryRoleResult;
import cn.spider.framework.controller.sdk.interfaces.RoleService;
import com.google.common.collect.Lists;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.init
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-21  14:54
 * @Description: 初始化core
 * @Version: 1.0
 */
@Slf4j
@Component
public class SpiderCoreStart {

    private ApplicationContext applicationContext;

    private ServiceBinder binder;

    private List<MessageConsumer<JsonObject>> containerConsumers;

    private Vertx vertx;

    private RoleService roleService;

    private MessageConsumer<JsonObject> businessConsumer;

    public SpiderCoreStart(Vertx vertx, ApplicationContext applicationContext, RoleService roleService) {
        this.vertx = vertx;
        this.binder = new ServiceBinder(vertx);
        this.applicationContext = applicationContext;
        this.containerConsumers = Lists.newArrayList();
        this.roleService = roleService;
    }

    public void upgrade() {
        startComponentByLeader();
    }

    public void startComponentByLeader() {
        // leader才会注册以下服务
        roleService.queryRole().onSuccess(suss -> {
            QueryRoleResult result = suss.mapTo(QueryRoleResult.class);
            if (result.getRole().equals(BrokerRole.LEADER)) {

            }
        }).onFailure(fail -> {
            log.info("queryRole-fail");
        });

    }

    public void necessaryComponent() {
        FlowService flowService = applicationContext.getBean(FlowService.class);
        MessageConsumer<JsonObject> flowConsumer = this.binder
                .setAddress(FlowService.ADDRESS)
                .register(FlowService.class, flowService);
        containerConsumers.add(flowConsumer);

        LeaderService leaderService = applicationContext.getBean(LeaderService.class);
        String leaderAddr = BrokerInfoUtil.queryBrokerName(vertx) + LeaderService.ADDRESS;
        MessageConsumer<JsonObject> leaderConsumer = this.binder
                .setAddress(leaderAddr)
                .register(LeaderService.class, leaderService);
        containerConsumers.add(leaderConsumer);
    }

    /**
     * 非中心化的启动
     */
    public void noCenterInit() {
        necessaryComponent();
        initContainer();
    }

    private void initContainer() {
        ContainerService containerService = applicationContext.getBean(ContainerService.class);

        String containerAddr = BrokerRole.LEADER.name() + ContainerService.ADDRESS;
        MessageConsumer<JsonObject> containerConsumer = this.binder
                .setAddress(containerAddr)
                .register(ContainerService.class, containerService);
        containerConsumers.add(containerConsumer);

        BusinessService businessService = applicationContext.getBean(BusinessService.class);

        String businessAddr = BrokerRole.LEADER.name() + BusinessService.ADDRESS;

        this.businessConsumer = this.binder
                .setAddress(businessAddr)
                .register(BusinessService.class, businessService);
    }

    public void reduceFollower() {
        if (Objects.isNull(this.businessConsumer)) {
            return;
        }
        // 卸载
        try {
            this.businessConsumer.unregister();
        } catch (Exception e) {
            log.info("卸载失败");
        }
    }

    /**
     * 注销消费者
     */
    public void unregister() {
        for (MessageConsumer<JsonObject> consumer : containerConsumers) {
            this.binder.unregister(consumer);
        }
    }
}
