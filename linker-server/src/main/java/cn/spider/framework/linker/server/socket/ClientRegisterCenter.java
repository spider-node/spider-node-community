package cn.spider.framework.linker.server.socket;

import cn.spider.framework.linker.server.loadbalancer.RoundRobinLoadBalancer;
import cn.spider.framework.proto.grpc.VertxTransferServerGrpc;
import io.grpc.ManagedChannel;
import io.vertx.core.Vertx;
import io.vertx.grpc.VertxChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: spider-node
 * @description: 使用于服务, 不适应于宿主应用提供的能力
 * @author: dds
 * @create: 2023-02-24 17:32
 */
@Slf4j
public class ClientRegisterCenter {
    // client的map便于调用
    private Map<String, RoundRobinLoadBalancer<ClientInfo>> roundRobinLoadBalancerMap;

    // 并于通过ip找到client
    private Vertx vertx;

    public ClientRegisterCenter(Vertx vertx) {
        this.vertx = vertx;
        this.roundRobinLoadBalancerMap = new HashMap<>();
    }

    /**
     * 注册-client
     *
     * @param clientInfo
     */
    public void registerClient(ClientInfo clientInfo) {
        serviceRegistry(clientInfo);
    }

    public void destroy(String ip, String workerName) {
        if (!this.roundRobinLoadBalancerMap.containsKey(workerName)) {
            return;
        }
        RoundRobinLoadBalancer robinLoadBalancer = this.roundRobinLoadBalancerMap.get(workerName);
        List<ClientInfo> clientInfos = robinLoadBalancer.getAll();
        List<ClientInfo> clientInfoList = clientInfos.stream().filter(item -> !item.getIp().equals(ip)).collect(Collectors.toList());
        robinLoadBalancer.updateAll(clientInfoList);
    }

    /**
     * 移除 -client
     *
     * @param ip         worker的ip
     * @param workerName worker的名称 可能是宿主应用
     */
    public void removeClient(String ip, String workerName) {
        destroy(ip, workerName);
    }

    public ClientInfo queryClientInfo(String workerName) {
        RoundRobinLoadBalancer<ClientInfo> robinLoadBalancer = this.roundRobinLoadBalancerMap.get(workerName);
        ClientInfo clientInfo = robinLoadBalancer.next();
        if (Objects.isNull(clientInfo)) {
            clientInfo = robinLoadBalancer.next();
        }
        return clientInfo;
    }

    /**
     * 服务注册
     */
    public void serviceRegistry(ClientInfo clientInfo) {
        if (!this.roundRobinLoadBalancerMap.containsKey(clientInfo.getWorkerName())) {
            RoundRobinLoadBalancer<ClientInfo> robinLoadBalancer = buildRoundRobinLoadBalancer();
            this.roundRobinLoadBalancerMap.put(clientInfo.getWorkerName(), robinLoadBalancer);
        }
        // 查询对于的-服务端口-默认为 9974
        // 初始化vertx-grpc客户端
        Integer port = Objects.isNull(clientInfo.getPort()) ? 9974 : clientInfo.getPort();
        ManagedChannel channel = VertxChannelBuilder
                .forAddress(vertx, clientInfo.getIp(), port)
                .usePlaintext()
                .build();
        // 构造grpc代理
        VertxTransferServerGrpc.TransferServerVertxStub serverVertxStub = VertxTransferServerGrpc.newVertxStub(channel);
        // 设置代理,方便后续调用
        clientInfo.setServerVertxStub(serverVertxStub);
        RoundRobinLoadBalancer clientInfos = this.roundRobinLoadBalancerMap.get(clientInfo.getWorkerName());

        clientInfos.add(clientInfo);
        log.info("注册上来的服务为 {} 端口号为{} ip {}", clientInfo.getWorkerName(), clientInfo.getPort(), clientInfo.getRemoteAddress());
    }

    /**
     * 宿主营养注册
     */
    private void hostApplication(ClientInfo clientInfo) {
        // 查询对于的-服务端口-默认为 9974
        // 初始化vertx-grpc客户端
        Integer port = Objects.isNull(clientInfo.getPort()) ? 9974 : clientInfo.getPort();
        ManagedChannel channel = VertxChannelBuilder
                .forAddress(vertx, clientInfo.getIp(), port)
                .usePlaintext()
                .build();
        // 构造grpc代理
        VertxTransferServerGrpc.TransferServerVertxStub serverVertxStub = VertxTransferServerGrpc.newVertxStub(channel);
        // 设置代理,方便后续调用
        clientInfo.setServerVertxStub(serverVertxStub);
    }

    private RoundRobinLoadBalancer<ClientInfo> buildRoundRobinLoadBalancer() {
        return RoundRobinLoadBalancer
                .newBuilder()
                .withInitialIndex(0)
                .withTasks(Arrays.asList())
                .build();
    }

}
