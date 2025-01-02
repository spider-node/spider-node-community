package cn.spider.framework.dev.ops;

import cn.spider.framework.dev.ops.config.K8sConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class MainVerticle extends AbstractVerticle {

  public static AbstractApplicationContext factory;

  public static Vertx clusterVertx;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    this.clusterVertx = vertx;
    this.factory = new AnnotationConfigApplicationContext(K8sConfig.class);
    startPromise.complete();
  }

  @Override
  public void stop(Promise<Void> stopPromise) {
    factory.close();
    stopPromise.complete();
  }


}
