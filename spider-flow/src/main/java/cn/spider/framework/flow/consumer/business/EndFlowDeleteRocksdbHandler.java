package cn.spider.framework.flow.consumer.business;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.EndFlowExampleEventData;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.db.rocksdb.RocksdbKeyManager;
import cn.spider.framework.db.util.RocksdbUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Queues;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rocksdb.RocksDBException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.consumer.business
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-06  18:14
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
public class EndFlowDeleteRocksdbHandler {

    private EventType eventType = EventType.END_FLOW_EXAMPLE;

    private BlockingQueue<String> deleteRocksdbQueue;

    private EventBus eventBus;

    private Vertx vertx;

    private RocksdbUtil rocksdbUtil;

    private Executor spiderDeleterRocksdbPool;

    private RocksdbKeyManager rocksdbKeyManager;

    public EndFlowDeleteRocksdbHandler(RocksdbUtil rocksdbUtil, Vertx vertx, EventBus eventBus, Executor spiderDeleterRocksdbPool,RocksdbKeyManager rocksdbKeyManager) {
        this.vertx = vertx;
        this.rocksdbUtil = rocksdbUtil;
        this.eventBus = eventBus;
        this.deleteRocksdbQueue = new ArrayBlockingQueue<>(1500, true);
        this.spiderDeleterRocksdbPool = spiderDeleterRocksdbPool;
        this.rocksdbKeyManager = rocksdbKeyManager;
        registerConsumer();
        registerTimer();
    }

    public void registerConsumer() {
        MessageConsumer<String> consumer = eventBus.consumer(eventType.queryAddr());
        consumer.handler(message -> {
            MultiMap multiMap = message.headers();
            String brokerName = multiMap.get(Constant.BROKER_NAME);
            // 校验该本节点是否为 brokerName的功能follower
            String localBrokerName = BrokerInfoUtil.queryBrokerName(vertx);
            if (!StringUtils.equals(brokerName, localBrokerName)) {
                return;
            }
            EndFlowExampleEventData data = JSON.parseObject(message.body(), EndFlowExampleEventData.class);
            deleteRocksdbQueue.add(data.getRequestId());
        });
    }

    public void registerTimer() {
        this.vertx.setPeriodic(1000 * 11, id -> {
            spiderDeleterRocksdbPool.execute(()->{consumerByBatch();});
        });
    }

    public void consumerByBatch() {
        try {
            if (deleteRocksdbQueue.size() == 0) {
                return;
            }
            List<String> list = new ArrayList<>();
            Queues.drain(deleteRocksdbQueue, list, 500, 1000 * 10, TimeUnit.MILLISECONDS);
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            log.info("删除rocksdb数据start");
            list.forEach(item->{
                List<String> keys = rocksdbKeyManager.query(item);
                if(CollectionUtils.isEmpty(keys)){
                    return;
                }
                keys.forEach(key->{
                    try {
                        rocksdbUtil.delete(item,key);
                        rocksdbKeyManager.delete(item);
                    } catch (RocksDBException e) {
                        log.error("删除失败的key是 {} 下级key是",item,key);
                    }
                });

            });
            log.info("删除rocksdb数据end");
        } catch (Exception e) {
            log.error("缓存队列批量消费异常：{}", ExceptionMessage.getStackTrace(e));
        }
    }
}
