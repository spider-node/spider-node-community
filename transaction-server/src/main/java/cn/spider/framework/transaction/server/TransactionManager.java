package cn.spider.framework.transaction.server;
import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.RegisterTransactionData;
import cn.spider.framework.common.utils.IdWorker;
import cn.spider.framework.common.utils.SnowFlake;
import cn.spider.framework.common.utils.SnowIdDto;
import cn.spider.framework.db.map.RocksDbMap;
import cn.spider.framework.db.util.RocksdbUtil;
import cn.spider.framework.linker.sdk.data.*;
import cn.spider.framework.linker.sdk.interfaces.LinkerService;
import cn.spider.framework.transaction.sdk.data.RegisterTransactionResponse;
import cn.spider.framework.transaction.sdk.data.enums.TransactionStatus;
import cn.spider.framework.transaction.server.example.TransactionExample;
import cn.spider.framework.transaction.server.example.TransactionGroupExample;
import io.vertx.core.Promise;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.json.JsonObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import java.util.List;


/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.transaction.server
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-07  11:38
 * @Description: 事务管理器
 * @Version: 1.0
 */
public class TransactionManager {

    private WorkerExecutor workerExecutor;

    private LinkerService linkerService;
    //prefix
    private final String GROUP_PREFIX = "GROUP_PREFIX";

    private RocksdbUtil rocksdbUtil;

    private EventManager eventManager;

    private RedissonClient redissonClient;



    public TransactionManager(WorkerExecutor workerExecutor, LinkerService linkerService,
                              RocksdbUtil rocksdbUtil,
                              EventManager eventManager,RedissonClient redissonClient) {
        this.workerExecutor = workerExecutor;
        this.linkerService = linkerService;
        this.rocksdbUtil = rocksdbUtil;
        this.eventManager = eventManager;
        this.redissonClient = redissonClient;
    }

    public void registerSyncTransaction(String requestId, String groupId, String taskId, String workerName) {
        TransactionExample example = buildTransactionExample(requestId, groupId, workerName, taskId);
        groupId = example.getTransactionGroupId();
        RocksDbMap rocksDbMap = new RocksDbMap(groupId, rocksdbUtil);
        String transactionKey = requestId + taskId;
        try {
            rocksDbMap.put(transactionKey, example);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 注册事务
     *
     * @param requestId
     * @param groupId
     * @param taskId
     * @param workerName
     * @return
     */
    public RegisterTransactionResponse registerTransaction(String requestId, String groupId, String taskId, String workerName) {
        // step1: 构建事务实例
        TransactionExample example = buildTransactionExample(requestId, groupId, workerName, taskId);
        groupId = example.getTransactionGroupId();
        // step2: 获取rocksdbMap
        RocksDbMap rocksDbMap = new RocksDbMap(groupId, rocksdbUtil);
        String transactionKey = requestId + taskId;
        try {
            // 事务信息存入 rocksDbMap
            rocksDbMap.put(transactionKey, example);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 构建消息体
        RegisterTransactionResponse response = new RegisterTransactionResponse();
        response.setGroupId(example.getTransactionGroupId());
        response.setBranchId(example.getBranchId());
        RegisterTransactionData registerTransactionData = RegisterTransactionData.builder().build();
        BeanUtils.copyProperties(example, registerTransactionData);
        // 发送事件+
        eventManager.sendMessage(EventType.REGISTER_TRANSACTION, registerTransactionData);
        return response;
    }

    public void transactionOperate(String groupId, Promise<JsonObject> promise, TransactionalType transactionalType) {

        RocksDbMap rocksDbMap = new RocksDbMap(groupId, this.rocksdbUtil);

        List<TransactionExample> examples = rocksDbMap.getAll(TransactionExample.class);

        if (CollectionUtils.isEmpty(examples)) {
            promise.complete();
        }

        TransactionGroupExample transactionGroupExample = new TransactionGroupExample(groupId, examples, promise);
        // 选择执行方式
        switch (transactionalType) {
            case ROLLBACK:
                transactionGroupExample.runRollBack(this.linkerService);
                break;
            case SUBMIT:
                transactionGroupExample.runCommit(this.linkerService);
                break;
        }
    }


    private String buildGroupId() {
        SnowIdDto snowIdDto = IdWorker.calculateDataIdAndWorkId2(this.redissonClient, GROUP_PREFIX);
        SnowFlake snowFlake = new SnowFlake(snowIdDto.getWorkerId(), snowIdDto.getDataCenterId(), snowIdDto.getTimestamp());
        return snowFlake.nextId() + "";
    }

    private TransactionExample buildTransactionExample(String requestId, String groupId, String workerName, String taskId) {
        String brushId = buildGroupId();
        return TransactionExample.builder()
                .transactionGroupId(StringUtils.isEmpty(groupId) ? buildGroupId() : groupId)
                .transactionStatus(TransactionStatus.INIT)
                .requestId(requestId)
                .taskId(taskId)
                .branchId(brushId)
                .workerName(workerName)
                .build();

    }


}
