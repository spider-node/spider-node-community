package cn.spider.framework.dev.ops.handler;

import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.DeleteDeployData;
import cn.spider.framework.common.event.data.OperateFailData;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.dev.ops.K8sManager;
import com.alibaba.fastjson.JSON;
import io.vertx.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeleteDeployHandler {
    private EventBus eventBus;

    private EventType eventType = EventType.SCALE_DOWN;

    private K8sManager k8sManager;

    public DeleteDeployHandler(EventBus eventBus, K8sManager k8sManager) {
        this.eventBus = eventBus;
        this.k8sManager = k8sManager;
        registerConsumer();
    }

    public void registerConsumer() {
        eventBus.consumer(eventType.queryAddr(), message -> {
            DeleteDeployData data = JSON.parseObject(message.body().toString(), DeleteDeployData.class);
            try {
                k8sManager.deleteDeployment(data.getDeploymentName());
            } catch (Exception e) {
                log.error("DeleteDeployHandler操作失败的异常信息为 {}", ExceptionMessage.getStackTrace(e));
                OperateFailData operateFailData = new OperateFailData(data.getFunctionVersionId(), ExceptionMessage.getStackTrace(e));
                eventBus.send(EventType.K8S_OPERATE_FAIL.queryAddr(), JSON.toJSONString(operateFailData));
            }
        });
    }
}
