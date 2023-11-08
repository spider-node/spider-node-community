package cn.spider.framework.flow.engine.example;
import cn.spider.framework.db.rocksdb.RocksdbKeyManager;
import cn.spider.framework.flow.engine.StoryEngine;
import com.google.common.collect.Queues;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.engine.example
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-22  11:12
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
public class ExampleDestroyManager {
    private BlockingQueue<String> deleteExample;

    private Vertx vertx;

    private Executor spiderDeleterRocksdbPool;

    private FlowExampleManager flowExampleManager;

    private RocksdbKeyManager rocksdbKeyManager;


    public ExampleDestroyManager(Vertx vertx, Executor spiderDeleterRocksdbPool, StoryEngine storyEngine,RocksdbKeyManager rocksdbKeyManager) {
        this.vertx = vertx;
        this.spiderDeleterRocksdbPool = spiderDeleterRocksdbPool;
        this.flowExampleManager = storyEngine.getFlowExampleManager();
        this.deleteExample = new ArrayBlockingQueue<>(15000, true);
        this.rocksdbKeyManager = rocksdbKeyManager;
        registerTimer();
    }

    public void addExampleData(String exampleId){
        deleteExample.add(exampleId);
    }

    public void registerTimer() {
        this.vertx.setPeriodic(1000 * 10, id -> {
            spiderDeleterRocksdbPool.execute(()->{consumerByBatch();});
        });
    }


    public void consumerByBatch() {
        try {
            if (deleteExample.size() == 0) {
                return;
            }
            List<String> list = new ArrayList<>();
            Queues.drain(deleteExample, list, 500, 1000 * 9, TimeUnit.MILLISECONDS);
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            //log.info("开始移除example");
            flowExampleManager.endFlowExample(list);
           // log.info("开始移除example对应的key");
            for(String exampleId : list){
                rocksdbKeyManager.delete(exampleId);
            }
            //log.info("开始移除example对应的key结束");
        } catch (Exception e) {

        }
    }


}
