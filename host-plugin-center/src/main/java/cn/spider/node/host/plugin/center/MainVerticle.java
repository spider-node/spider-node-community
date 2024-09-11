package cn.spider.node.host.plugin.center;
import cn.spider.node.host.plugin.center.config.SpringConfig;
import cn.spider.node.host.plugin.center.sdk.interfaces.HostPluginInterface;
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

public class MainVerticle extends AbstractVerticle {

  private static AbstractApplicationContext factory;

  public static Vertx clusterVertx;

  private List<MessageConsumer<JsonObject>> consumers;

  private ServiceBinder binder;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    this.consumers = new ArrayList<>();
    this.clusterVertx = vertx;
    this.factory = new AnnotationConfigApplicationContext(SpringConfig.class);
    HostPluginInterface hostPluginInterface = this.factory.getBean(HostPluginInterface.class);
    this.binder = new ServiceBinder(vertx);
    MessageConsumer<JsonObject> consumer = binder.setAddress(HostPluginInterface.ADDRESS)
            .register(HostPluginInterface.class, hostPluginInterface);
    this.consumers.add(consumer);
    startPromise.complete();
  }

  /**
   * 关闭verticle
   *
   * @param stopPromise
   */
  @Override
  public void stop(Promise<Void> stopPromise) {
    factory.close();
    for (MessageConsumer<JsonObject> consumer : consumers) {
      this.binder.unregister(consumer);
    }
    stopPromise.complete();
  }
}
