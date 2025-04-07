package cn.spider.framework.transaction.server.queue;

import cn.spider.framework.transaction.server.TransactionManager;
import cn.spider.framework.transaction.server.example.TransactionElement;
import cn.spider.framework.transaction.server.example.enums.TransactionRunType;
import com.google.common.collect.Queues;
import io.vertx.core.Vertx;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 事务异常队列
 */
public class TransactionExceptionQueue {
    private BlockingQueue<TransactionElement> transactionElementQueue;

    private Vertx vertx;

    private Executor spiderTransactionPool;

    private TransactionManager transactionManager;

    public TransactionExceptionQueue(Vertx vertx, Executor spiderTransactionPool) {
        this.transactionElementQueue = new ArrayBlockingQueue<>(1000, true);
        this.vertx = vertx;
        this.spiderTransactionPool = spiderTransactionPool;
    }

    public void init(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.vertx.setPeriodic(1000, event -> {
            consumerByBatch();
        });
    }


    /**
     * 加入队列
     *
     * @param param
     */
    public void insertQueue(TransactionElement param) {
        transactionElementQueue.offer(param);
    }

    public void consumerByBatch() {
        if (transactionElementQueue.size() == 0) {
            return;
        }
        spiderTransactionPool.execute(() -> {
            List<TransactionElement> list = new ArrayList<>();
            try {
                // 1秒执行一次
                Queues.drain(transactionElementQueue, list, 200, 1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            // 获取出list中为transactionRunType为ROLLBACK的数据
            List<TransactionElement> rollBackList = list.stream().filter(transactionElement -> transactionElement.getTransactionRunType() == TransactionRunType.ROLLBACK).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(rollBackList)) {
                // list中使用 requestId进行map分组
                Map<String, List<TransactionElement>> transactionElementMap = rollBackList.stream().collect(Collectors.groupingBy(TransactionElement::getRequestId));
                transactionElementMap.forEach((requestId, transactionElements) -> {
                    // transactionElements 基于groupId进行map分组
                    transactionManager.rollbackBefore(transactionElements, requestId);
                });
            }
            List<TransactionElement> commitList = list.stream().filter(transactionElement -> transactionElement.getTransactionRunType() == TransactionRunType.COMMIT).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(commitList)) {
                return;
            }
            // 执行提交重试
            transactionManager.commitBefore(commitList);

        });
    }
}
