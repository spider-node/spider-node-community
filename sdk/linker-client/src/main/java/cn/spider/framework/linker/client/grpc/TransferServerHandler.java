package cn.spider.framework.linker.client.grpc;

import cn.spider.framework.linker.client.task.TaskManager;
import cn.spider.framework.linker.client.util.IpUtil;
import cn.spider.framework.linker.sdk.data.LinkerServerRequest;
import cn.spider.framework.proto.grpc.TransferRequest;
import cn.spider.framework.proto.grpc.TransferResponse;
import cn.spider.framework.proto.grpc.VertxTransferServerGrpc;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import java.net.InetAddress;

/**
 * @program: spider-node
 * @description: 接受spider-服务端请求并且执行
 * @author: dds
 * @create: 2023-03-02 21:25
 */

public class TransferServerHandler {

    private Vertx vertx;

    private String localhost;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TransferServerHandler.class);

    private PlatformTransactionManager platformTransactionManager;

    private TransactionDefinition transactionDefinition;

    private TaskManager taskManager;

    private Integer rpcPort;


    public void init(Vertx vertx, PlatformTransactionManager platformTransactionManager, TransactionDefinition transactionDefinition, TaskManager taskManager, Integer rpcPort, Boolean isLocal) {
        this.vertx = vertx;
        this.rpcPort = rpcPort;
        this.platformTransactionManager = platformTransactionManager;
        this.transactionDefinition = transactionDefinition;
        try {
            if (isLocal) {
                this.localhost = IpUtil.buildLocalHost();
            } else {
                this.localhost = InetAddress.getLocalHost().getHostAddress();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.taskManager = taskManager;
        run();
    }

    // 跟客户端功能交互
    public void run() {
        log.info("rpc-端口号 {}", this.rpcPort);
        VertxServer server = VertxServerBuilder
                .forAddress(vertx, this.localhost, this.rpcPort)
                // 添加服务的实现
                .addService(new VertxTransferServerGrpc.TransferServerVertxImplBase() {
                    @Override
                    public Future<TransferResponse> instruct(TransferRequest transferRequest) {
                        // 构造Promise对象
                        Promise<TransferResponse> transferResponsePromise = Promise.promise();
                        // 获取请求中的body
                        String body = transferRequest.getBody();
                        LinkerServerRequest request = JSON.parseObject(body, LinkerServerRequest.class);
                        taskManager.runGrpc(request, transferResponsePromise);
                        return transferResponsePromise.future();
                    }
                })
                .build();
        // start the server
        server.start(ar -> {
            if (ar.failed()) {
                log.error("执行失败");
            } else {
                // 发布成功
                log.info("发布成功");
            }
        });
    }
}
