package cn.spider.framework.domain.area;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.domain.area.config.DomainConfig;
import cn.spider.framework.domain.sdk.interfaces.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-18  22:54
 * @Description: 领域的启动类
 * @Version: 1.0
 */
@Slf4j
public class AreaVerticle extends AbstractVerticle {

    private static AbstractApplicationContext factory;

    public static Vertx clusterVertx;

    public String brokerName;

    private ServiceBinder binder;

    private List<MessageConsumer<JsonObject>> containerConsumers;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        this.clusterVertx = vertx;
        this.factory = new AnnotationConfigApplicationContext(DomainConfig.class);
        this.containerConsumers = new ArrayList<>();
        this.brokerName = BrokerInfoUtil.queryBrokerName(vertx);
        // 进行接口发布
        this.binder = new ServiceBinder(vertx);
        WorkerInterface workerInterface = this.factory.getBean(WorkerInterface.class);
        MessageConsumer<JsonObject> workerConsumer = this.binder.setAddress(WorkerInterface.ADDRESS)
                .register(WorkerInterface.class, workerInterface);

        AreaInterface areaInterface = this.factory.getBean(AreaInterface.class);

        MessageConsumer<JsonObject> areaConsumer = this.binder.setAddress(AreaInterface.ADDRESS)
                .register(AreaInterface.class, areaInterface);

        this.containerConsumers.add(areaConsumer);

        // FunctionInterface
        FunctionInterface functionInterface = this.factory.getBean(FunctionInterface.class);


        MessageConsumer<JsonObject> functionConsumer = this.binder.setAddress(FunctionInterface.ADDRESS)
                .register(FunctionInterface.class, functionInterface);

        this.containerConsumers.add(functionConsumer);

        NodeInterface nodeInterface = this.factory.getBean(NodeInterface.class);

        MessageConsumer<JsonObject> nodeConsumer = this.binder.setAddress(NodeInterface.ADDRESS)
                .register(NodeInterface.class, nodeInterface);


        VersionInterface versionInterface = this.factory.getBean(VersionInterface.class);

        MessageConsumer<JsonObject> versionConsumer = this.binder.setAddress(VersionInterface.ADDRESS)
                .register(VersionInterface.class, versionInterface);

        this.containerConsumers.add(versionConsumer);

        this.containerConsumers.add(nodeConsumer);

        this.containerConsumers.add(workerConsumer);
        log.info("domain-start-suss");
        startPromise.complete();
    }

    @Override
    public void stop(Promise<Void> stopPromise){
        factory.close();
        for (MessageConsumer<JsonObject> consumer : containerConsumers) {
            this.binder.unregister(consumer);
        }
        stopPromise.complete();
    }

}
