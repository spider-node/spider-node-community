package cn.spider.framework.dev.ops.handler;

import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.OperateFailData;
import cn.spider.framework.common.event.data.ScaleUpData;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.dev.ops.K8sManager;
import com.alibaba.fastjson.JSON;
import io.vertx.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScaleUpHandler {
    private EventBus eventBus;

    private EventType eventType = EventType.SCALE_UP;

    private K8sManager k8sManager;

    public ScaleUpHandler(EventBus eventBus, K8sManager k8sManager) {
        this.eventBus = eventBus;
        this.k8sManager = k8sManager;
        registerConsumer();
    }

    public void registerConsumer() {
        eventBus.consumer(eventType.queryAddr(), message -> {
            ScaleUpData scaleUpData = JSON.parseObject(message.body().toString(), ScaleUpData.class);
            try {
                k8sManager.scaleDeployment("spider-vertx",scaleUpData.getYaml(), scaleUpData.getReplicas());
            } catch (Exception e) {
                log.error("ScaleUpHandler操作失败的异常信息为 {}", ExceptionMessage.getStackTrace(e));
                OperateFailData operateFailData = new OperateFailData(scaleUpData.getFunctionVersionId(), ExceptionMessage.getStackTrace(e));
                eventBus.send(EventType.K8S_OPERATE_FAIL.queryAddr(), JSON.toJSONString(operateFailData));
            }
        });
    }


}
