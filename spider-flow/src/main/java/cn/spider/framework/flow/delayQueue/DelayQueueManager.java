package cn.spider.framework.flow.delayQueue;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.flow.SpiderCoreVerticle;
import cn.spider.framework.flow.delayQueue.enums.RedisDelayQueueEnum;
import cn.spider.framework.flow.timer.data.FlowDelayExample;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.common.delayQueue
 * @Author: dengdongsheng
 * @CreateTime: 2023-07-05  14:15
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
public class DelayQueueManager {

    private Vertx vertx;

    private RedisDelayQueueUtil redisDelayQueueUtil;

    private WorkerExecutor businessExecute;


    public DelayQueueManager(Vertx vertx, RedisDelayQueueUtil redisDelayQueueUtil, WorkerExecutor businessExecute) {
        this.vertx = vertx;
        this.redisDelayQueueUtil = redisDelayQueueUtil;
        this.businessExecute = businessExecute;
        run();

    }

    public <T> boolean addDelayQueue(T value, long delay, TimeUnit timeUnit, String queueCode) {
        return redisDelayQueueUtil.addDelayQueue(value, delay, timeUnit, queueCode);
    }

    public void run() {
        vertx.setPeriodic(2000, id -> {
            businessExecute.executeBlocking(promise -> {
                for (RedisDelayQueueEnum delayQueueEnum : RedisDelayQueueEnum.values()) {
                    consumption(delayQueueEnum);
                }
            });
        });
    }

    public void consumption(RedisDelayQueueEnum delayQueueEnum){
        try {
            DelayHandler delayHandler = SpiderCoreVerticle.factory.getBean(delayQueueEnum.getBeanId(), DelayHandler.class);
            List<Object> example = redisDelayQueueUtil.getDelayQueueList(delayQueueEnum.getCode());
            if (CollectionUtils.isEmpty(example)) {
                return;
            }
            example.forEach(item -> {
                FlowDelayExample flowDelayExample = FlowDelayExample.builder().build();
                BeanUtils.copyProperties(item, flowDelayExample);
                delayHandler.execute(flowDelayExample);
            });
        } catch (InterruptedException e) {
            log.info("获取消费数据 {}",ExceptionMessage.getStackTrace(e));
        }

    }
}
