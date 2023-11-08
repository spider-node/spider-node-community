package cn.spider.framework.linker.client.vertxrpc;
import cn.spider.framework.linker.client.vertxrpc.impl.VertxTaskInterfaceImpl;
import cn.spider.framework.linker.sdk.interfaces.VertxRpcTaskInterface;
import com.hazelcast.config.Config;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.linker.client.vertxrpc
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-16  17:19
 * @Description: client-加入vertx集群
 * @Version: 1.0
 */
@Slf4j
public class VertxClusterStart {
    private VertxTaskInterfaceImpl vertxTaskInterface;

    private List<MessageConsumer<JsonObject>> containerConsumers;

    private String workerName;

    private ServiceBinder binder;

    private Vertx vertx;

    public VertxClusterStart(VertxTaskInterfaceImpl vertxTaskInterface,String workerName) {
        this.vertxTaskInterface = vertxTaskInterface;
        this.containerConsumers = new ArrayList<>();
        this.workerName = workerName;
        init();
    }

    public void init() {
        Config hazelcastConfig = Config.load();
        // 设置集群类型
        ClusterManager clusterManager = new HazelcastClusterManager(hazelcastConfig);
        VertxOptions options = new VertxOptions().setClusterManager(clusterManager);
        options.setWorkerPoolSize(10);
        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                this.vertx = res.result();
                String taskAddr = workerName + VertxRpcTaskInterface.ADDRESS;
                this.binder = new ServiceBinder(vertx);
                MessageConsumer<JsonObject> taskConsumer = this.binder
                        .setAddress(taskAddr)
                        .register(VertxRpcTaskInterface.class, vertxTaskInterface);
                containerConsumers.add(taskConsumer);
            }else {
                log.error("加入集群失败 {}","vertx启动失败");
            }
        });
    }

    /**
     * 销毁
     */
    public void destroy(){
        for(MessageConsumer<JsonObject> consumer : containerConsumers){
            consumer.unregister();
        }
        containerConsumers.clear();
    }
}
