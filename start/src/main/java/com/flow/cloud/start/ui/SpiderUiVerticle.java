package com.flow.cloud.start.ui;

import cn.spider.framework.common.utils.BrokerInfoUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * @program: flow-cloud
 * @description:
 * @author: dds
 * @create: 2022-06-18 17:05
 */
public class SpiderUiVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        Router router= Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route("/*").handler(StaticHandler.create("webroot/dist"));
        String uiPort = BrokerInfoUtil.queryUiPort(vertx);
        vertx.createHttpServer().requestHandler(router).listen(Integer.parseInt(uiPort));
        startPromise.complete();
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        this.stop();
        stopPromise.complete();
    }
}
