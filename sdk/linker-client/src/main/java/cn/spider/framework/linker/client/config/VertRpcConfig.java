package cn.spider.framework.linker.client.config;
import cn.spider.framework.linker.client.task.TaskManager;
import cn.spider.framework.linker.client.vertxrpc.VertxClusterStart;
import cn.spider.framework.linker.client.vertxrpc.impl.VertxTaskInterfaceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.linker.client.config
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-16  16:03
 * @Description: rpc-config
 * @Version: 1.0
 */
public class VertRpcConfig {
    @Bean
    public VertxTaskInterfaceImpl buildVertxTaskInterfaceImpl(TaskManager taskManager) {
        return new VertxTaskInterfaceImpl(taskManager);
    }

    @Bean
    public VertxClusterStart buildVertxRpcManager(VertxTaskInterfaceImpl vertxTaskInterface, @Value("${spider.worker.name}") String workerName){
        return new VertxClusterStart(vertxTaskInterface,workerName);
    }
}
