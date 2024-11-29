package cn.spider.framework.flow.timer;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.flow.funtion.InitLoaderClassService;
import cn.spider.framework.flow.resource.factory.StartEventFactory;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SystemTimer {

    private Vertx vertx;

    private StartEventFactory startEventFactory;

    private InitLoaderClassService initLoaderClassService;

    public SystemTimer(Vertx vertx, StartEventFactory startEventFactory, InitLoaderClassService initLoaderClassService) {
        this.vertx = vertx;
        this.startEventFactory = startEventFactory;
        this.initLoaderClassService = initLoaderClassService;
    }

    public void delayLoadResource() {
        vertx.setTimer(5* 1000, id -> {
            try {
                startEventFactory.initBpmn().onFailure(fail->{
                    log.info("init-bpmn-fail {}", ExceptionMessage.getStackTrace(fail));
                    delayLoadResource();
                });
                initLoaderClassService.init();
            } catch (Exception e) {
                delayLoadResource();
                log.info("init-fail {}", ExceptionMessage.getStackTrace(e));
            }
        });
    }

}
