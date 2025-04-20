package cn.spider.framework.transaction.server;

import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.EndTransactionData;
import cn.spider.framework.common.event.enums.TransactionType;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.common.utils.NumberUtil;
import cn.spider.framework.db.map.RocksDbMap;
import cn.spider.framework.db.util.RocksdbUtil;
import cn.spider.framework.linker.sdk.data.*;
import cn.spider.framework.linker.sdk.interfaces.LinkerService;
import cn.spider.framework.transaction.sdk.data.RegisterTransactionRequest;
import cn.spider.framework.transaction.sdk.data.enums.TransactionStatus;
import cn.spider.framework.transaction.server.example.TransactionElement;
import cn.spider.framework.transaction.server.example.TransactionExample;
import cn.spider.framework.transaction.server.example.enums.ExampleStatus;
import cn.spider.framework.transaction.server.example.enums.TaskStatus;
import cn.spider.framework.transaction.server.example.enums.TransactionRunType;
import cn.spider.framework.transaction.server.queue.TransactionExceptionQueue;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;


/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.transaction.server
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-07  11:38
 * @Description: 事务管理器
 * @Version: 1.0
 */
@Slf4j
public class TransactionManager {

    private RocksdbUtil rocksdbUtil;

    private EventManager eventManager;

    private Integer order;

    private final String GROUP_PREFIX = "_";

    private Map<String, TransactionExample> transactionExamples;

    private LinkerService linkerService;

    private TransactionExceptionQueue transactionExceptionQueue;
    // 主要用于存放回滚的队列
    private Map<String, Queue<TransactionElement>> transactionRollbackElementQueue;


    public TransactionManager(EventManager eventManager, RocksdbUtil rocksdbUtil, LinkerService linkerService, TransactionExceptionQueue transactionExceptionQueue) {
        this.eventManager = eventManager;
        this.order = 0;
        this.rocksdbUtil = rocksdbUtil;
        this.transactionExamples = new HashMap<>();
        this.linkerService = linkerService;
        this.transactionExceptionQueue = transactionExceptionQueue;
        this.transactionRollbackElementQueue = new HashMap<>();
        transactionExceptionQueue.init(this);
    }

    private void orderIncrement() {
        this.order++;
    }

    private String buildGroupId(String requestId, String groupId) {
        return requestId + GROUP_PREFIX + groupId;
    }

    /**
     * 注册事务
     *
     * @param request 注册事务的请求
     * @return RegisterTransactionResponse
     */
    public void registerTransactionV2(RegisterTransactionRequest request) {
        TransactionElement element = buildTransactionExample(request);
        // 判断 实例当中是否包含该requestId
        if (!transactionExamples.containsKey(request.getRequestId())) {
            TransactionExample transactionExample = TransactionExample.builder()
                    .requestId(request.getRequestId())
                    .transactionElementGroup(new HashMap<>())
                    .exampleStatus(ExampleStatus.INIT)
                    .build();
            transactionExamples.put(request.getRequestId(), transactionExample);
        }
        // 判断实例中是否存在task
        TransactionExample transactionExample = transactionExamples.get(request.getRequestId());
        Map<String, Set<String>> transactionElementGroup = transactionExample.getTransactionElementGroup();
        if (!transactionElementGroup.containsKey(element.getTransactionGroupId())) {
            transactionElementGroup.put(element.getTransactionGroupId(), new HashSet<>());
        }
        Set<String> taskIds = transactionElementGroup.get(element.getTransactionGroupId());
        taskIds.add(element.getTaskId());
        // step2: 获取rocksdbMap
        RocksDbMap rocksDbMap = new RocksDbMap(buildGroupId(element.getRequestId(), element.getTransactionGroupId()), rocksdbUtil);
        try {
            // 事务信息存入 rocksDbMap
            rocksDbMap.put(element.getTaskId(), element);
            log.info("注册事务节点:{}", JSON.toJSONString(element));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建事务节点实例
     *
     * @param request 注册事务的请求体
     * @return TransactionElement
     */
    private TransactionElement buildTransactionExample(RegisterTransactionRequest request) {
        orderIncrement();
        return TransactionElement.builder()
                .transactionGroupId(request.getGroupId())
                .transactionStatus(TransactionStatus.INIT)
                .requestId(request.getRequestId())
                .taskId(request.getTaskId())
                .branchId(NumberUtil.stringToLong(request.getRequestId(), request.getGroupId()) + "")
                .order(this.order.intValue())
                .workerName(request.getWorkerName())
                .datasourceId(request.getResourceId())
                .taskStatus(TaskStatus.INIT)
                .build();
    }


    public void updateTransactionTaskStatus(String groupId, String taskId, TaskStatus taskStatus, String requestId, String workerName) {
        RocksDbMap rocksDbMap = new RocksDbMap(buildGroupId(requestId, groupId), rocksdbUtil);
        TransactionElement element = rocksDbMap.get(taskId, TransactionElement.class);
        element.setWorkerName(workerName);
        element.setTaskStatus(taskStatus);
        log.info("更新事务节点状态:{}", JSON.toJSONString(element));
        rocksDbMap.put(taskId, element);
    }

    /**
     * 更新实例的执行状态
     *
     * @param requestId
     * @param exampleStatus
     */
    public void updateExampleStatus(String requestId, ExampleStatus exampleStatus) {
        if (!transactionExamples.containsKey(requestId)) {
            log.info("更新事务状态失败,事务实例不存在,requestId:{}", requestId);
            return;
        }
        TransactionExample transactionExample = transactionExamples.get(requestId);
        transactionExample.setExampleStatus(exampleStatus);
        log.info("更新事务状态:{}", JSON.toJSONString(transactionExample));
        decisionTransactByRequestId(requestId);
    }

    public void decisionTransactByRequestId(String requestId) {
        TransactionExample transactionExample = transactionExamples.get(requestId);
        Map<String, Set<String>> transactionElementGroup = transactionExample.getTransactionElementGroup();
        if (transactionElementGroup.isEmpty()) {
            return;
        }
        Map<String, List<TransactionElement>> transactionElementGroupMap = new HashMap<>(transactionElementGroup.size());
        transactionElementGroup.forEach((groupId, taskIds) -> {
            RocksDbMap rocksDbMap = new RocksDbMap(buildGroupId(transactionExample.getRequestId(), groupId), rocksdbUtil);
            List<TransactionElement> elements = new ArrayList<>(taskIds.size());
            for (String taskId : taskIds) {
                TransactionElement element = rocksDbMap.get(taskId, TransactionElement.class);
                elements.add(element);
            }
            transactionElementGroupMap.put(groupId, elements);
        });
        // 执行
        List<TransactionElement> rollbackElements = new ArrayList<>();
        List<TransactionElement> commitElements = new ArrayList<>();

        transactionElementGroupMap.forEach((groupId, elements) -> {
            for (TransactionElement element : elements) {
                if (element.getTaskStatus().equals(TaskStatus.FAIL)) {
                    rollbackElements.addAll(elements);
                    return;
                }
            }
            commitElements.addAll(elements);
        });
        log.info("事务执行结果:{},提交节点:{},回滚节点:{}", transactionExample.getExampleStatus(), commitElements, rollbackElements);
        // 执行事务操作
        commitBefore(commitElements);
        rollbackBefore(rollbackElements, requestId);
        transactionExamples.remove(requestId);
    }


    /**
     * 提交前置做的内容
     *
     * @param commitElements 提交节点
     */
    public void commitBefore(List<TransactionElement> commitElements) {
        for (TransactionElement element : commitElements) {
            commit(element);
        }
    }

    /**
     * 回滚前置需要做的内容
     *
     * @param rollbackElements 回滚的节点
     * @param requestId        链路的请求id
     */
    public void rollbackBefore(List<TransactionElement> rollbackElements, String requestId) {
        if (CollectionUtils.isNotEmpty(rollbackElements)) {
            // rollbackElements中使用 order 降序的方式排序
            rollbackElements.sort(Comparator.comparingInt(TransactionElement::getOrder).reversed());
            // 遍历rollbackElements,将元素加入到队列中,然后从队列中取出元素进行回滚 使用LinkedList主要为了性能。
            Queue<TransactionElement> queue = this.transactionRollbackElementQueue.containsKey(requestId) ?
                    this.transactionRollbackElementQueue.get(requestId) :
                    new LinkedList<>(rollbackElements);
            // 添加到队列中
            this.transactionRollbackElementQueue.put(requestId, queue);
        }
        // 进行回滚
        rollback(requestId);
    }

    /**
     * 基于request进行做事务回滚
     *
     * @param requestId requestId
     */
    private void rollback(String requestId) {
        if (!this.transactionRollbackElementQueue.containsKey(requestId)) {
            return;
        }
        Queue<TransactionElement> rollbackQueue = this.transactionRollbackElementQueue.get(requestId);
        TransactionElement element = rollbackQueue.poll();
        if (Objects.isNull(element)) {
            // 当数据为空的时候说明队列中没有数据了，可以移除了
            this.transactionRollbackElementQueue.remove(requestId);
            return;
        }
        rollBack(element);
    }


    /**
     * 回滚
     *
     * @param example 需要回滚的节点实例
     */
    public void rollBack(TransactionElement example) {
        example.setTransactionRunType(TransactionRunType.ROLLBACK);
        LinkerServerRequest linkerServerRequest = buildRequestEntity(example, TransactionalType.ROLLBACK);
        JsonObject request = JsonObject.mapFrom(linkerServerRequest);
        log.info("回滚请求为-data{}", request.toString());
        Future<JsonObject> rollBackResult = linkerService.transaction(request);
        EndTransactionData endTransactionData = EndTransactionData.builder()
                .transactionGroupId(example.getTransactionGroupId())
                .requestId(example.getRequestId())
                .flowElementId(example.getTaskId())
                .branchId(String.valueOf(example.getBranchId()))
                .transactionOperate(TransactionType.ROLLBACK)
                .build();
        rollBackResult.onSuccess(suss -> {
            // 可以通知进行下一个阶段的回滚了
            rollback(example.getRequestId());
            JsonObject result = suss;
            LinkerServerResponse responseNew = result.getJsonObject("data").mapTo(LinkerServerResponse.class);
            if (responseNew.getResultCode().equals(ResultCode.SUSS)) {
                log.info("事务返回的结果成功");
                example.setTransactionStatus(TransactionStatus.ROLL_BACK_SUSS);
                endTransactionData.setTransactionStatus(cn.spider.framework.common.event.enums.TransactionStatus.SUSS);
                eventManager.sendMessage(EventType.END_TRANSACTION, endTransactionData);
            } else {
                example.setTransactionStatus(TransactionStatus.ROLL_BACK_FAIL);
                example.recordFailNum();
                // 进行注册3秒一次进行retry->每隔十秒一次
                if (example.getFailNum() <= 3) {
                    log.error("事务执行错误");
                    // rollBack(example, this.linkerService);
                    transactionExceptionQueue.insertQueue(example);
                    return;
                }
                example.setTransactionStatus(TransactionStatus.ROLL_BACK_FAIL);
                eventManager.sendMessage(EventType.END_TRANSACTION, endTransactionData);
            }
        }).onFailure(fail -> {
            log.error("事务回滚执行错误 {}",ExceptionMessage.getStackTrace(fail));
            rollback(example.getRequestId());
            example.setTransactionStatus(TransactionStatus.ROLL_BACK_FAIL);
            example.recordFailNum();
            // 进行回滚重试
            if (example.getFailNum() <= 3) {
                //rollBack(example, linkerService);
                transactionExceptionQueue.insertQueue(example);
                return;
            }
            // 发送事务操作失败
            endTransactionData.setTransactionStatus(cn.spider.framework.common.event.enums.TransactionStatus.FAIL);
            eventManager.sendMessage(EventType.END_TRANSACTION, endTransactionData);
        });
    }

    public void commit(TransactionElement example) {
        example.setTransactionRunType(TransactionRunType.COMMIT);
        LinkerServerRequest linkerServerRequest = buildRequestEntity(example, TransactionalType.SUBMIT);
        JsonObject request = JsonObject.mapFrom(linkerServerRequest);
        Future<JsonObject> commitResult = linkerService.transaction(request);
        log.info("提交请求为-data{}", request.toString());
        EndTransactionData endTransactionData = EndTransactionData.builder()
                .transactionGroupId(example.getTransactionGroupId())
                .requestId(example.getRequestId())
                .flowElementId(example.getTaskId())
                .branchId(String.valueOf(example.getBranchId()))
                .transactionOperate(TransactionType.COMMIT)
                .build();

        commitResult.onSuccess(suss -> {
            JsonObject result = suss;
            log.info("事务返回的结果为 事务返回信息为 {}", result.toString());
            LinkerServerResponse responseNew = result.getJsonObject("data").mapTo(LinkerServerResponse.class);
            if (responseNew.getResultCode().equals(ResultCode.SUSS)) {
                log.info("事务执行成功");
                example.setTransactionStatus(TransactionStatus.COMMIT_SUSS);
                endTransactionData.setTransactionStatus(cn.spider.framework.common.event.enums.TransactionStatus.SUSS);
                eventManager.sendMessage(EventType.END_TRANSACTION, endTransactionData);
            } else {
                example.setTransactionStatus(TransactionStatus.COMMIT_FAIL);
                example.recordFailNum();
                // 进行注册3秒一次进行retry->每隔十秒一次
                if (example.getFailNum() <= 3) {
                    log.error("执行错误");
                    transactionExceptionQueue.insertQueue(example);
                    return;
                }
                example.setTransactionStatus(TransactionStatus.ROLL_BACK_FAIL);
                eventManager.sendMessage(EventType.END_TRANSACTION, endTransactionData);
            }
        }).onFailure(fail -> {
            log.error("transaction-BranchId {} xid {} 提交失败 {}", example.getBranchId(), example.getTaskId(), ExceptionMessage.getStackTrace(fail));
            example.setTransactionStatus(TransactionStatus.COMMIT_FAIL);
            example.recordFailNum();
            // 进行注册3秒一次进行retry->每隔十秒一次
            if (example.getFailNum() <= 3) {
                //commit(example, linkerService);
                transactionExceptionQueue.insertQueue(example);
                log.error("执行错误");
                return;
            }
            example.setTransactionStatus(TransactionStatus.ROLL_BACK_FAIL);
            eventManager.sendMessage(EventType.END_TRANSACTION, endTransactionData);
        });
    }

    private LinkerServerRequest buildRequestEntity(TransactionElement example, TransactionalType transactionalType) {
        // 参数中，移除末尾的 Promise<Object> promise
        LinkerServerRequest linkerServerRequest = new LinkerServerRequest();
        TransactionalRequest transactionalRequest = new TransactionalRequest();
        transactionalRequest.setTransactionId(example.getTaskId());
        transactionalRequest.setBranchId(example.getBranchId());
        transactionalRequest.setResourceId(example.getDatasourceId());
        transactionalRequest.setWorkerName(example.getWorkerName());
        transactionalRequest.setTransactionalType(transactionalType);
        linkerServerRequest.setExecutionType(ExecutionType.TRANSACTION);
        linkerServerRequest.setTransactionalRequest(transactionalRequest);
        return linkerServerRequest;
    }


}
