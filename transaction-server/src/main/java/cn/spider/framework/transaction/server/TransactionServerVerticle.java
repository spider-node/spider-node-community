package cn.spider.framework.transaction.server;

import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.transaction.sdk.interfaces.TransactionInterface;
import cn.spider.framework.transaction.server.config.TransactionConfig;
import cn.spider.framework.transaction.server.impl.TransactionInterfaceImpl;
import cn.spider.framework.transaction.server.transcript.TranscriptManager;
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
@Slf4j
public class TransactionServerVerticle extends AbstractVerticle {

    public static AbstractApplicationContext factory;

    public static Vertx clusterVertx;

    private List<MessageConsumer<JsonObject>> containerConsumers;

    private ServiceBinder binder;

    private String brokerName;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        this.containerConsumers = new ArrayList<>();
        this.clusterVertx = vertx;
        this.factory = new AnnotationConfigApplicationContext(TransactionConfig.class);
        this.binder = new ServiceBinder(vertx);
        this.brokerName = BrokerInfoUtil.queryBrokerName(vertx);

        TransactionManager transactionManager = factory.getBean(TransactionManager.class);
        TranscriptManager transcriptManager = factory.getBean(TranscriptManager.class);
        TransactionInterfaceImpl transactionInterface = new TransactionInterfaceImpl(transactionManager,transcriptManager);
        // 发布 对我提供您接口
        String transactionAddr = this.brokerName+TransactionInterface.ADDRESS;
        MessageConsumer<JsonObject> transactionConsumer = this.binder
                .setAddress(transactionAddr)
                .register(TransactionInterface.class, transactionInterface);
        containerConsumers.add(transactionConsumer);
        startPromise.complete();
    }

    /**
     * 卸载会执行的内容
     * @param stopPromise
     */
    @Override
    public void stop(Promise<Void> stopPromise) {
        // 关闭ioc
        factory.close();
        // 卸载 接口的消费者
        for (MessageConsumer<JsonObject> consumer : containerConsumers) {
            this.binder.unregister(consumer);
        }
        stopPromise.complete();
    }
}
