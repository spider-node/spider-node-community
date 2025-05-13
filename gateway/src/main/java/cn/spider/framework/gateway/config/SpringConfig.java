package cn.spider.framework.gateway.config;

import cn.spider.framework.common.event.EventConfig;
import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.container.sdk.interfaces.BusinessService;
import cn.spider.framework.container.sdk.interfaces.ContainerService;
import cn.spider.framework.container.sdk.interfaces.FlowService;
import cn.spider.framework.controller.sdk.interfaces.BrokerInfoService;
import cn.spider.framework.controller.sdk.interfaces.LeaderHeartService;
import cn.spider.framework.domain.sdk.interfaces.*;
import cn.spider.framework.gateway.GatewayVerticle;
import cn.spider.framework.gateway.api.file.FileHandler;
import cn.spider.framework.gateway.api.function.SpiderServerHandler;
import cn.spider.framework.linker.sdk.interfaces.LinkerService;
import cn.spider.framework.log.sdk.interfaces.LogInterface;
import cn.spider.node.host.plugin.center.sdk.interfaces.HostPluginInterface;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.gateway.config
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-19  14:57
 * @Description: spring组件的配置类
 * @Version: 1.0
 */
@Import(EventConfig.class)
@Configuration
@ComponentScan(basePackages = {"cn.spider.framework.gateway.api.*"})
public class SpringConfig {

    @Bean
    public Vertx buildVertx() {
        return GatewayVerticle.clusterVertx;
    }

    /**
     * 请求到leader
     *
     * @param vertx
     * @return
     */
    @Bean
    public ContainerService buildContainerService(Vertx vertx) {
        return ContainerService.createProxy(vertx, ContainerService.ADDRESS);
    }

    /**
     * 请求所有节点
     *
     * @param vertx
     * @return
     */
    @Bean
    public FlowService buildFlowService(Vertx vertx) {
        return FlowService.createProxy(vertx, FlowService.ADDRESS);
    }

    /**
     * 只发送到leader
     *
     * @param vertx
     * @return
     */
    @Bean
    public BusinessService buildBusinessService(Vertx vertx) {
        return BusinessService.createProxy(vertx, BusinessService.ADDRESS);
    }

    /**
     * log服务接口
     *
     * @param vertx
     * @return
     */
    @Bean
    public LogInterface buildLogInterface(Vertx vertx) {
        return LogInterface.createProxy(vertx, LogInterface.ADDRESS);
    }

    @Bean
    public LeaderHeartService buildLeaderHeartService(Vertx vertx) {
        String addr = LeaderHeartService.ADDRESS;
        return LeaderHeartService.createProxy(vertx, addr);
    }

    @Bean
    public BrokerInfoService buildBrokerInfoService(Vertx vertx) {
        return BrokerInfoService.createProxy(vertx, BrokerInfoService.ADDRESS);
    }

    @Bean
    public DataFlowInterface buildDataFlowInterface(Vertx vertx) {
        return DataFlowInterface.createProxy(vertx, DataFlowInterface.ADDRESS);
    }

    @Bean
    public SpiderServerHandler buildSpiderServerHandler(ContainerService containerService,
                                                        FlowService flowService,
                                                        BusinessService businessService,
                                                        LogInterface logInterface,
                                                        BrokerInfoService brokerInfoService,
                                                        AreaInterface areaInterface,
                                                        FunctionInterface functionInterface,
                                                        NodeInterface nodeInterface,
                                                        VersionInterface versionInterface,
                                                        Vertx vertx,
                                                        EventManager eventManager,
                                                        DataFlowInterface dataFlowInterface,
                                                        AiTaskInterface aiTaskInterface, HostPluginInterface hostPluginInterface,
                                                        LinkerService linkerService,FrameworkInterface frameworkInterface,
                                                        HttpFunctionInterface httpFunctionInterface,
                                                        DomainObjectInterface domainObjectInterface) {
        return new SpiderServerHandler(containerService,
                flowService,
                businessService,
                logInterface,
                brokerInfoService,
                areaInterface,
                functionInterface,
                nodeInterface,
                versionInterface, vertx, eventManager, dataFlowInterface, aiTaskInterface,hostPluginInterface,linkerService,frameworkInterface,httpFunctionInterface,domainObjectInterface);
    }

    @Bean
    public DomainObjectInterface buildDomainObjectInterface(Vertx vertx) {
        return DomainObjectInterface.createProxy(vertx, DomainObjectInterface.ADDRESS);
    }

    @Bean
    public HttpFunctionInterface buildHttpFunctionInterface(Vertx vertx) {
        return HttpFunctionInterface.createProxy(vertx, HttpFunctionInterface.ADDRESS);
    }

    @Bean
    public FrameworkInterface buildFrameworkInterface(Vertx vertx) {
        return FrameworkInterface.createProxy(vertx, FrameworkInterface.ADDRESS);
    }

    @Bean
    public HostPluginInterface buildHostPluginInterface(Vertx vertx) {
        return HostPluginInterface.createProxy(vertx, HostPluginInterface.ADDRESS);
    }

    @Bean
    public FileHandler buildFileHandler(Vertx vertx) {
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");
        String bpmnPatch = localMap.get("bpmn_path");
        String sdkPatch = localMap.get("sdk_path");
        return new FileHandler(bpmnPatch, sdkPatch, vertx);
    }

    @Bean
    public AreaInterface buildAreaInterface(Vertx vertx) {
        return AreaInterface.createProxy(vertx, AreaInterface.ADDRESS);
    }

    @Bean
    public FunctionInterface buildFunctionInterface(Vertx vertx) {
        return FunctionInterface.createProxy(vertx, FunctionInterface.ADDRESS);
    }

    @Bean
    public NodeInterface buildNodeInterface(Vertx vertx) {
        return NodeInterface.createProxy(vertx, NodeInterface.ADDRESS);
    }

    @Bean
    public VersionInterface buildVersionInterface(Vertx vertx) {
        return VersionInterface.createProxy(vertx, VersionInterface.ADDRESS);
    }

    @Bean
    public AiTaskInterface buildAiTaskInterface(Vertx vertx) {
        return AiTaskInterface.createProxy(vertx, AiTaskInterface.ADDRESS);
    }

    /**
     * 跟worker通信 接口
     *
     * @param vertx
     * @return
     */
    @Bean
    public LinkerService buildLinkerService(Vertx vertx) {
        return LinkerService.createProxy(vertx, LinkerService.ADDRESS);
    }

}
