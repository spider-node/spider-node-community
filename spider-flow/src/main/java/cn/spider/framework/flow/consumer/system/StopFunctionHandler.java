package cn.spider.framework.flow.consumer.system;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.StopFunctionData;
import cn.spider.framework.flow.business.BusinessManager;
import com.alibaba.fastjson.JSON;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.consumer.system
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-29  13:49
 * @Description: 停止对应的功能
 * @Version: 1.0
 */
public class StopFunctionHandler {
    private EventBus eventBus;

    private BusinessManager businessManager;

    private EventType eventType = EventType.STOP_FUNCTION;

    public StopFunctionHandler(EventBus eventBus, BusinessManager businessManager) {
        this.eventBus = eventBus;
        this.businessManager = businessManager;
        registerConsumer();
    }

    public void registerConsumer() {
        MessageConsumer<String> consumer = eventBus.consumer(eventType.queryAddr());
        consumer.handler(message -> {
            StopFunctionData destroyClassData = JSON.parseObject(message.body(),StopFunctionData.class);
            businessManager.deleteFunction(destroyClassData.getFunctionId());
        });
    }
}
