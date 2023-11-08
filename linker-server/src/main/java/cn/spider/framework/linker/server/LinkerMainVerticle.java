package cn.spider.framework.linker.server;

import cn.spider.framework.common.role.BrokerRole;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.linker.sdk.interfaces.LinkerService;
import cn.spider.framework.linker.server.config.SpringConfig;
import cn.spider.framework.linker.server.external.LinkerServiceImpl;
import cn.spider.framework.linker.server.socket.ClientRegisterCenter;
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

public class LinkerMainVerticle extends AbstractVerticle {

    private AbstractApplicationContext factory;

    public static Vertx vertxNew;

    private List<MessageConsumer<JsonObject>> containerConsumers;

    private ServiceBinder binder;

    private String brokerName;


    /**
     * 启动verticle
     *
     * @param startPromise
     * @throws Exception
     */
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        this.containerConsumers = new ArrayList<>();
        this.vertxNew = vertx;
        this.brokerName = BrokerInfoUtil.queryBrokerName(vertx);
        this.factory = new AnnotationConfigApplicationContext(SpringConfig.class);
        ClientRegisterCenter clientRegisterCenter = factory.getBean(ClientRegisterCenter.class);
        LinkerService linkerService = new LinkerServiceImpl(clientRegisterCenter,vertx);
        // 发布接口
        this.binder = new ServiceBinder(vertx);
        MessageConsumer<JsonObject> linkerConsumer = binder.setAddress(this.brokerName + LinkerService.ADDRESS)
                .register(LinkerService.class, linkerService);
        this.containerConsumers.add(linkerConsumer);
        startPromise.complete();
    }

    /**
     * 关闭verticle
     *
     * @param stopPromise
     */
    /**
     * 关闭verticle
     *
     * @param stopPromise
     */
    @Override
    public void stop(Promise<Void> stopPromise) {
        factory.close();
        for (MessageConsumer<JsonObject> consumer : containerConsumers) {
            this.binder.unregister(consumer);
        }
        stopPromise.complete();
    }
}
