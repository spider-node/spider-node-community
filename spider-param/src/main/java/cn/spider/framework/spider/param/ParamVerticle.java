package cn.spider.framework.spider.param;

import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.param.sdk.interfaces.ParamInterface;
import cn.spider.framework.spider.param.config.ParamConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import java.util.ArrayList;
import java.util.List;

public class ParamVerticle extends AbstractVerticle {

    public static AbstractApplicationContext factory;

    public static Vertx clusterVertx;

    private ServiceBinder binder;

    private List<MessageConsumer<JsonObject>> containerConsumers;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        this.clusterVertx = vertx;
        this.factory = new AnnotationConfigApplicationContext(ParamConfig.class);
        containerConsumers = new ArrayList<>();
        this.binder = new ServiceBinder(clusterVertx);

        ParamInterface paramInterface = this.factory.getBean(ParamInterface.class);
        MessageConsumer<JsonObject> containerConsumer = this.binder
                .setAddress(BrokerInfoUtil.queryBrokerName(vertx) + ParamInterface.ADDRESS)
                .register(ParamInterface.class, paramInterface);
        containerConsumers.add(containerConsumer);
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
        this.factory.close();
        stopPromise.complete();
    }
}
