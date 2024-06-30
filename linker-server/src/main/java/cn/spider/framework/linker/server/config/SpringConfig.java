package cn.spider.framework.linker.server.config;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.domain.sdk.interfaces.FunctionInterface;
import cn.spider.framework.domain.sdk.interfaces.WorkerInterface;
import cn.spider.framework.linker.sdk.interfaces.LinkerService;
import cn.spider.framework.linker.server.LinkerMainVerticle;
import cn.spider.framework.linker.server.socket.WorkerRegisterManager;
import cn.spider.framework.linker.server.socket.ClientRegisterCenter;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

/**
 * @program: spider-node
 * @description: spring-配置
 * @author: dds
 * @create: 2023-03-02 10:55
 */
@Configuration
@ComponentScan(basePackages = {"cn.spider.framework.linker.server.*"})
public class SpringConfig {

    @Bean
    public Vertx getVertx() {
        return LinkerMainVerticle.vertxNew;
    }

    @Bean
    public WorkerExecutor createPool(Vertx vertx) {
        int poolSize = 20;
        long maxExecuteTime = 2;
        TimeUnit maxExecuteTimeUnit = TimeUnit.MINUTES;
        return vertx.createSharedWorkerExecutor("spider-worker-pool", poolSize, maxExecuteTime, maxExecuteTimeUnit);
    }

    @Bean
    public ClientRegisterCenter createClientRegisterCenter(Vertx vertx) {
        return new ClientRegisterCenter(vertx);
    }

    @Bean
    public WorkerRegisterManager createWorkerRegisterManager(NetServer server,ClientRegisterCenter clientRegisterCenter,Vertx vertx){
        return new WorkerRegisterManager(server,clientRegisterCenter,vertx);
    }

    @Bean
    public NetServer createNetServer(Vertx vertx){
        NetServer server = vertx.createNetServer();
        return server;
    }

    /**
     * 注入获取工作服务的节点信息
     * @param vertx
     * @return
     */
    @Bean
    public WorkerInterface buildWorkerInterface(Vertx vertx){
        return WorkerInterface.createProxy(vertx, WorkerInterface.ADDRESS);
    }

    @Bean
    public FunctionInterface buildFunctionInterface(Vertx vertx){
        return FunctionInterface.createProxy(vertx,FunctionInterface.ADDRESS);
    }

}
