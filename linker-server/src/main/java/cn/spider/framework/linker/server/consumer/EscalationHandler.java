package cn.spider.framework.linker.server.consumer;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.EscalationData;
import cn.spider.framework.domain.sdk.data.RefreshAreaParam;
import cn.spider.framework.linker.sdk.data.emuns.FunctionEscalationType;
import cn.spider.framework.linker.server.socket.WorkerRegisterManager;
import com.alibaba.fastjson.JSON;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EscalationHandler {

    private EventType eventType = EventType.ESCALATION_AREA_INFO;

    private EventBus eventBus;

    private WorkerRegisterManager workerRegisterManager;

    public EscalationHandler(EventBus eventBus, WorkerRegisterManager workerRegisterManager) {
        this.eventBus = eventBus;
        this.workerRegisterManager = workerRegisterManager;
        registerHandler();
    }

    private void registerHandler(){
        MessageConsumer<String> consumer = eventBus.consumer(eventType.queryAddr());
        consumer.handler(message -> {
            log.info("上报领域信息了 {}",message.body());
            EscalationData data = JSON.parseObject(message.body(), EscalationData.class);
            RefreshAreaParam refreshAreaParam = JSON.parseObject(JSON.toJSONString(data.getRefreshAreaParam()),RefreshAreaParam.class);
            workerRegisterManager.escalationAreaInfo(refreshAreaParam,data.getIp(), FunctionEscalationType.valueOf(data.getFunctionEscalationType()));
        });
    }
}
