package cn.spider.framework.linker.server.consumer;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.linker.sdk.data.emuns.FunctionEscalationType;
import cn.spider.framework.linker.server.socket.WorkerRegisterManager;
import cn.spider.framework.param.result.build.enventData.EscalationData;
import cn.spider.framework.param.result.build.model.ReportParamInfo;
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
            ReportParamInfo refreshAreaParam = JSON.parseObject(JSON.toJSONString(data.getRefreshAreaParam()),ReportParamInfo.class);
            try {
                workerRegisterManager.escalationAreaInfo(refreshAreaParam,data.getIp(), FunctionEscalationType.valueOf(data.getFunctionEscalationType()));
            } catch (IllegalArgumentException e) {
                log.info("escalationAreaInfo error {} data {}", ExceptionMessage.getStackTrace(e),message.body());
            }
        });
    }
}
