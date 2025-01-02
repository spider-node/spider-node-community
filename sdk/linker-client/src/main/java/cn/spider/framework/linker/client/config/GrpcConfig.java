package cn.spider.framework.linker.client.config;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.linker.client.escalation.AreaInfoService;
import cn.spider.framework.linker.client.grpc.TransferServerHandler;
import cn.spider.framework.linker.client.socket.SocketManager;
import cn.spider.framework.linker.client.task.TaskManager;
import cn.spider.framework.linker.client.timer.BusinessTimer;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.client.WebClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;

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
        transferServerHandler.init(vertx,platformTransactionManager,transactionDefinition,taskManager,Integer.parseInt(rpcPort),false);
        return transferServerHandler;
    }

    @Bean
    public SocketManager buildSocketManager(Vertx vertx, @Value("${spider.worker.name}") String workerName,
                                            BusinessTimer businessTimer,
                                            WebClient webClient,
                                            @Value("${spider.worker.rpc-port:}") String rpcPort,
                                            @Value("${spider.server.ip:}") String spiderServerIp,
                                            @Value("${spider.server.port:}") String spiderServerPort,
                                            @Value("${spider.worker.type:}") String workerType,
                                            AreaInfoService areaInfoService){
        String spiderWorkerName = System.getenv(Constant.WORKER_NAME);
        if(StringUtils.isNotEmpty(spiderWorkerName)){
            workerName = spiderWorkerName;
        }
        String serverIp = System.getenv(Constant.SPIDER_SERVER_IP);
        if(StringUtils.isNotEmpty(serverIp)){
            spiderServerIp = serverIp;
        }
        String serverPort = System.getenv(Constant.SPIDER_SERVER_PORT);
        if(StringUtils.isNotEmpty(serverPort)){
            spiderServerPort = serverPort;
        }
        String workerType1 = System.getenv(Constant.WORKER_TYPE);
        if(StringUtils.isNotEmpty(workerType1)){
            workerType = workerType1;
        }
        return new SocketManager(vertx,workerName,businessTimer,webClient,spiderServerIp,Integer.parseInt(rpcPort),spiderServerPort,false,areaInfoService,workerType);
    }

    @Bean
    public BusinessTimer BuildBusinessTimer(Vertx vertx){
        return new BusinessTimer(vertx);
    }
}
