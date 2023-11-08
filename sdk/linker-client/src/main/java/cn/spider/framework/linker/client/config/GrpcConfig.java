package cn.spider.framework.linker.client.config;

import cn.spider.framework.linker.client.grpc.TransferServerHandler;
import cn.spider.framework.linker.client.socket.SocketManager;
import cn.spider.framework.linker.client.task.TaskManager;
import cn.spider.framework.linker.client.timer.BusinessTimer;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.linker.client.config
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-27  15:13
 * @Description: TODO
 * @Version: 1.0
 */
public class GrpcConfig {

    @Bean
    public Vertx buildVertx(){
        VertxOptions options = new VertxOptions();
        options.setWorkerPoolSize(10);
        return Vertx.vertx(options);
    }

    @Bean
    public TransferServerHandler buildTransferServerHandler(Vertx vertx,
                                                            @Value("${spider.worker.rpc-port}") String rpcPort,
                                                            PlatformTransactionManager platformTransactionManager,
                                                            TransactionDefinition transactionDefinition,
                                                            TaskManager taskManager){
        TransferServerHandler transferServerHandler = new TransferServerHandler();
        transferServerHandler.init(vertx,platformTransactionManager,transactionDefinition,taskManager,Integer.parseInt(rpcPort));
        return transferServerHandler;
    }

    @Bean
    public SocketManager buildSocketManager(Vertx vertx, @Value("${spider.worker.name}") String workerName,
                                            BusinessTimer businessTimer,
                                            WebClient webClient,
                                            @Value("${spider.server.ip}") String spiderServerIp,
                                            @Value("${spider.server.port}") String spiderServerPort){
        return new SocketManager(vertx,workerName,businessTimer,webClient,spiderServerIp,spiderServerPort,false);
    }

    @Bean
    public BusinessTimer BuildBusinessTimer(Vertx vertx){
        return new BusinessTimer(vertx);
    }
}
