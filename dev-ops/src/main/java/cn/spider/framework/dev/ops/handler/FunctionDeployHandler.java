package cn.spider.framework.dev.ops.handler;

import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.FunctionDeployData;
import cn.spider.framework.common.event.data.OperateFailData;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.dev.ops.K8sManager;
import com.alibaba.fastjson.JSON;
import io.vertx.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FunctionDeployHandler {
    private EventBus eventBus;

    private EventType eventType = EventType.DEPLOY;

    private K8sManager k8sManager;

    public FunctionDeployHandler(EventBus eventBus, K8sManager k8sManager) {
        this.eventBus = eventBus;
        this.k8sManager = k8sManager;
        registerConsumer();
    }

    public void registerConsumer() {
        eventBus.consumer(eventType.queryAddr(), message -> {
            FunctionDeployData data = JSON.parseObject(message.body().toString(), FunctionDeployData.class);
            try {
                k8sManager.createDeployment(data.getUrl());
            } catch (Exception e) {
                log.error("FunctionDeployHandler操作失败的异常信息为 {}", ExceptionMessage.getStackTrace(e));
                OperateFailData operateFailData = new OperateFailData(data.getFunctionVersionId(), ExceptionMessage.getStackTrace(e));
                // 发送下，事件，通知部署失败
                eventBus.send(EventType.K8S_OPERATE_FAIL.queryAddr(), JSON.toJSONString(operateFailData));
            }
        });
    }
}
