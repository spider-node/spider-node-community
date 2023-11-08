package cn.spider.framework.flow.timer;

import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.container.sdk.interfaces.FlowService;
import cn.spider.framework.flow.SpiderCoreVerticle;
import cn.spider.framework.flow.delayQueue.DelayQueueManager;
import cn.spider.framework.flow.delayQueue.enums.RedisDelayQueueEnum;
import cn.spider.framework.flow.engine.StoryEngine;
import cn.spider.framework.flow.engine.example.data.FlowExample;
import cn.spider.framework.flow.timer.data.FlowDelayExample;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.timer
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-27  18:27
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
@Component
public class SpiderTimer {

    @Resource
    private Vertx vertx;

    @Resource
    private StoryEngine storyEngine;

    private FlowService flowService;

    /**
     * 重算总次数
     */
    private Map<String, JsonObject> retryCountMap;

    @Resource
    private WorkerExecutor businessExecute;

    @Resource
    private DelayQueueManager delayQueueManager;

    private String brokerName;

    @PostConstruct
    public void init() {
        this.retryCountMap = new HashMap<>();
        this.brokerName = BrokerInfoUtil.queryBrokerName(vertx);
    }

    public void registerRetry(String requestId, JsonObject param) {
        if (retryCountMap.size() > 800) {
            log.info("重试数据超过500 请求requestId", requestId);
            return;
        }
        retryCountMap.put(requestId, param);
        vertx.setTimer(1000 * 5, id -> {
            businessExecute.executeBlocking(promise -> {
                JsonObject data = retryCountMap.get(requestId);
                if (Objects.isNull(flowService)) {
                    flowService = SpiderCoreVerticle.factory.getBean(FlowService.class);
                }
                flowService.startFlow(data).onFailure(fail -> {
                    log.info("retry-fail {}", requestId);
                });
                retryCountMap.remove(requestId);
            });
        });
    }

    /**
     * 轮询节点
     * @param example
     * @param timer
     */
    public void registerDelay(FlowExample example, Integer timer) {
        FlowDelayExample flowDelayExample = FlowDelayExample.builder().exampleId(example.getExampleId()).brokerName(brokerName).build();
        delayQueueManager.addDelayQueue(flowDelayExample,timer * 1000, TimeUnit.MILLISECONDS, RedisDelayQueueEnum.FLOW_DELAY.getCode());
    }

    public void registerFinalDelay(FlowExample example, Integer timer) {
        FlowDelayExample flowDelayExample = FlowDelayExample.builder().exampleId(example.getExampleId()).brokerName(brokerName).build();
        delayQueueManager.addDelayQueue(flowDelayExample,timer * 1000, TimeUnit.MILLISECONDS, RedisDelayQueueEnum.FLOW_DELAY.getCode());
    }

    public void registerApprove(String flowExampleId, Integer time) {
        vertx.setPeriodic(time, id -> {
            if (storyEngine.getFlowExampleManager().checkFlowExampleVerify(flowExampleId)) {
                vertx.cancelTimer(id);
            }
        });
    }

    public void registerExampleMonitor(String flowExampleId) {
        FlowDelayExample flowDelayExample = FlowDelayExample.builder().exampleId(flowExampleId).brokerName(brokerName).build();
        delayQueueManager.addDelayQueue(flowDelayExample,120 * 1000, TimeUnit.MILLISECONDS, RedisDelayQueueEnum.FLOW_REMOVE_DELAY.getCode());
    }
}
