package cn.spider.framework.domain.area.config;

import cn.spider.framework.common.event.EventConfig;
import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.container.sdk.interfaces.ContainerService;
import cn.spider.framework.db.config.MysqlConfig;
import cn.spider.framework.domain.area.AreaManger;
import cn.spider.framework.domain.area.AreaVerticle;
import cn.spider.framework.domain.area.function.FunctionManger;
import cn.spider.framework.domain.area.function.version.VersionManager;
import cn.spider.framework.domain.area.impl.AreaImpl;
import cn.spider.framework.domain.area.impl.FunctionImpl;
import cn.spider.framework.domain.area.impl.NodeInterfaceImpl;
import cn.spider.framework.domain.area.impl.VersionImpl;
import cn.spider.framework.domain.area.node.NodeManger;
import cn.spider.framework.domain.area.worker.WorkerImpl;
import cn.spider.framework.domain.sdk.interfaces.AreaInterface;
import cn.spider.framework.domain.sdk.interfaces.FunctionInterface;
import cn.spider.framework.domain.sdk.interfaces.NodeInterface;
import cn.spider.framework.domain.sdk.interfaces.VersionInterface;
import cn.spider.framework.log.sdk.interfaces.LogInterface;
import cn.spider.framework.param.result.build.interfaces.ParamRefreshInterface;
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
@Import({EventConfig.class, MysqlConfig.class})
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
    public VersionManager buildVersionManager(MySQLPool client,ContainerService containerService) {
        return new VersionManager(client,containerService);
    }

    @Bean
    public AreaManger buildAreaManger(MySQLPool client, ContainerService containerService, ParamRefreshInterface paramRefreshInterface) {
        return new AreaManger(client, containerService,paramRefreshInterface);
    }

    @Bean
    public ParamRefreshInterface buildParamRefreshInterface(Vertx vertx){
        return ParamRefreshInterface.createProxy(vertx,ParamRefreshInterface.ADDRESS);
    }

    @Bean
    public NodeManger buildNodeManger(MySQLPool client,AreaManger areaManger) {
        return new NodeManger(client,areaManger);
    }

    @Bean
    public WorkerImpl buildWorkerImpl(MySQLPool client) {
        return new WorkerImpl(client);
    }

    @Bean
    public ContainerService buildContainerService(Vertx vertx) {
        return ContainerService.createProxy(vertx, ContainerService.ADDRESS);
    }

    @Bean
    public AreaInterface buildAreaImpl(AreaManger areaManger) {
        return new AreaImpl(areaManger);
    }

    @Bean
    public FunctionInterface buildFunctionImpl(FunctionManger functionManger,LogInterface logInterface) {
        return new FunctionImpl(functionManger,logInterface);
    }

    @Bean
    public LogInterface buildLogInterface(Vertx vertx){
        return LogInterface.createProxy(vertx,LogInterface.ADDRESS);
    }

    @Bean
    public NodeInterface buildNodeInterface(NodeManger nodeManger){
        return new NodeInterfaceImpl(nodeManger);
    }

    @Bean
    public VersionInterface buildVersionImpl(VersionManager versionManager){
        return new VersionImpl(versionManager);
    }
}
