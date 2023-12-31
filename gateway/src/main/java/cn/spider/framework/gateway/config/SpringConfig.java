package cn.spider.framework.gateway.config;

import cn.spider.framework.common.role.BrokerRole;
import cn.spider.framework.container.sdk.interfaces.BusinessService;
import cn.spider.framework.container.sdk.interfaces.ContainerService;
import cn.spider.framework.container.sdk.interfaces.FlowService;
import cn.spider.framework.controller.sdk.interfaces.BrokerInfoService;
import cn.spider.framework.controller.sdk.interfaces.LeaderHeartService;
import cn.spider.framework.db.config.*;
import cn.spider.framework.domain.sdk.interfaces.AreaInterface;
import cn.spider.framework.domain.sdk.interfaces.FunctionInterface;
import cn.spider.framework.domain.sdk.interfaces.NodeInterface;
import cn.spider.framework.domain.sdk.interfaces.VersionInterface;
import cn.spider.framework.gateway.GatewayVerticle;
import cn.spider.framework.gateway.api.file.FileHandler;
import cn.spider.framework.gateway.api.function.SpiderServerHandler;
import cn.spider.framework.gateway.oss.OssConfigClient;
import cn.spider.framework.log.sdk.interfaces.LogInterface;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
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
@Configuration
@ComponentScan(basePackages = {"cn.spider.framework.gateway.api.*"})
@Import({RedissonConfig.class})
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
        return ContainerService.createProxy(vertx, BrokerRole.LEADER.name() + ContainerService.ADDRESS);
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
        return BusinessService.createProxy(vertx, BrokerRole.LEADER.name() + BusinessService.ADDRESS);
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
    public RRateLimiter buildRRateLimiter(RedissonClient redissonClient, Vertx vertx) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter("spider-rrateLimiter");
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");
        // 配置
        rateLimiter.trySetRate(RateType.OVERALL, Integer.parseInt(localMap.get("limitation-number")),
                Integer.parseInt(localMap.get("limitation-interval")), RateIntervalUnit.SECONDS);
        return rateLimiter;
    }

    @Bean
    public BrokerInfoService buildBrokerInfoService(Vertx vertx) {
        return BrokerInfoService.createProxy(vertx, BrokerInfoService.ADDRESS);
    }

    @Bean
    public SpiderServerHandler buildSpiderServerHandler(ContainerService containerService,
                                                        FlowService flowService,
                                                        BusinessService businessService,
                                                        LogInterface logInterface,
                                                        LeaderHeartService leaderHeartService,
                                                        BrokerInfoService brokerInfoService,
                                                        AreaInterface areaInterface,
                                                        RRateLimiter rateLimiter,
                                                        FunctionInterface functionInterface,
                                                        NodeInterface nodeInterface,
                                                        VersionInterface versionInterface) {
        return new SpiderServerHandler(containerService,
                flowService,
                businessService,
                logInterface,
                leaderHeartService,
                brokerInfoService,
                rateLimiter,
                areaInterface,
                functionInterface,
                nodeInterface,
                versionInterface);
    }

    @Bean
    public OssConfigClient buildOssConfigClient(Vertx vertx) {
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");
        String endpoint = localMap.get("oss_endpoint");

        String keyId = localMap.get("oss_keyId");

        String keySecret = localMap.get("oss_keySecret");

        String bucketName = localMap.get("oss_bucketName");
        return new OssConfigClient(endpoint,keyId,keySecret,bucketName);
    }

    @Bean
    public FileHandler buildFileHandler(OssConfigClient ossConfigClient,Vertx vertx){
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");
        String bpmnPatch = localMap.get("bpmn_path");
        String sdkPatch = localMap.get("sdk_path");
        return new FileHandler(ossConfigClient,bpmnPatch,sdkPatch,vertx);
    }

    @Bean
    public AreaInterface buildAreaInterface(Vertx vertx){
        return AreaInterface.createProxy(vertx,AreaInterface.ADDRESS);
    }

    @Bean
    public FunctionInterface buildFunctionInterface(Vertx vertx){
        return FunctionInterface.createProxy(vertx,FunctionInterface.ADDRESS);
    }

    @Bean
    public NodeInterface buildNodeInterface(Vertx vertx){
        return NodeInterface.createProxy(vertx,NodeInterface.ADDRESS);
    }

    @Bean
    public VersionInterface buildVersionInterface(Vertx vertx){
        return VersionInterface.createProxy(vertx,VersionInterface.ADDRESS);
    }

}
