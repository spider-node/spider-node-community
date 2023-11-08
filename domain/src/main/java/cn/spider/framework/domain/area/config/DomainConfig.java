package cn.spider.framework.domain.area.config;

import cn.spider.framework.common.event.EventConfig;
import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.role.BrokerRole;
import cn.spider.framework.container.sdk.interfaces.ContainerService;
import cn.spider.framework.db.config.MysqlConfig;
import cn.spider.framework.db.config.RedissonConfig;
import cn.spider.framework.domain.area.AreaManger;
import cn.spider.framework.domain.area.AreaVerticle;
import cn.spider.framework.domain.area.function.FunctionManger;
import cn.spider.framework.domain.area.function.version.VersionManager;
import cn.spider.framework.domain.area.impl.AreaImpl;
import cn.spider.framework.domain.area.impl.FunctionImpl;
import cn.spider.framework.domain.area.impl.NodeInterfaceImpl;
import cn.spider.framework.domain.area.node.NodeManger;
import cn.spider.framework.domain.area.worker.WorkerImpl;
import cn.spider.framework.domain.sdk.interfaces.AreaInterface;
import cn.spider.framework.domain.sdk.interfaces.FunctionInterface;
import cn.spider.framework.domain.sdk.interfaces.NodeInterface;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.config
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-15  21:59
 * @Description: 组件配置类
 * @Version: 1.0
 */
@Configuration
@Import({EventConfig.class, MysqlConfig.class, RedissonConfig.class})
@ComponentScan(basePackages = {"cn.spider.framework.domain.area.*"})
public class DomainConfig {

    @Bean
    public Vertx getVertx() {
        return AreaVerticle.clusterVertx;
    }

    @Bean
    public FunctionManger buildFunctionManger(MySQLPool client, EventManager eventManager, VersionManager versionManager) {
        return new FunctionManger(client, eventManager, versionManager);
    }

    @Bean
    public VersionManager buildVersionManager(MySQLPool client) {
        return new VersionManager(client);
    }

    @Bean
    public AreaManger buildAreaManger(MySQLPool client, ContainerService containerService) {
        return new AreaManger(client, containerService);
    }

    @Bean
    public NodeManger buildNodeManger(MySQLPool client) {
        return new NodeManger(client);
    }

    @Bean
    public WorkerImpl buildWorkerImpl(MySQLPool client) {
        return new WorkerImpl(client);
    }

    @Bean
    public ContainerService buildContainerService(Vertx vertx) {
        return ContainerService.createProxy(vertx, BrokerRole.LEADER.name() + ContainerService.ADDRESS);
    }

    @Bean
    public AreaInterface buildAreaImpl(AreaManger areaManger) {
        return new AreaImpl(areaManger);
    }

    @Bean
    public FunctionInterface buildFunctionImpl(FunctionManger functionManger) {
        return new FunctionImpl(functionManger);
    }

    @Bean
    public NodeInterface buildNodeInterface(NodeManger nodeManger){
        return new NodeInterfaceImpl(nodeManger);
    }
}
