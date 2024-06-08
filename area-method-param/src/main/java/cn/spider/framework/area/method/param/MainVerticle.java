package cn.spider.framework.area.method.param;

import cn.spider.framework.area.method.param.config.SpiderConfig;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.domain.sdk.interfaces.AreaInterface;
import cn.spider.framework.domain.sdk.interfaces.WorkerInterface;
import cn.spider.framework.param.result.build.interfaces.ParamRefreshInterface;
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

  public String brokerName;

  private ServiceBinder binder;

  private List<MessageConsumer<JsonObject>> containerConsumers;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    this.clusterVertx = vertx;
    this.factory = new AnnotationConfigApplicationContext(SpiderConfig.class);
    this.containerConsumers = new ArrayList<>();
    this.brokerName = BrokerInfoUtil.queryBrokerName(vertx);
    // 进行接口发布
    this.binder = new ServiceBinder(vertx);

    ParamRefreshInterface paramRefreshInterface = this.factory.getBean(ParamRefreshInterface.class);
    MessageConsumer<JsonObject> paramRefreshConsumer = this.binder.setAddress(ParamRefreshInterface.ADDRESS)
            .register(ParamRefreshInterface.class, paramRefreshInterface);
    this.containerConsumers.add(paramRefreshConsumer);
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
