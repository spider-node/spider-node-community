package cn.spider.framework.linker.server.socket;

import cn.spider.framework.common.utils.TaskKeyUtil;
import cn.spider.framework.linker.server.baseinfo.BaseManager;
import cn.spider.framework.linker.server.socket.data.HostApplication;
import cn.spider.framework.proto.grpc.VertxTransferServerGrpc;
import com.alibaba.fastjson.JSON;
import io.grpc.ManagedChannel;
import io.vertx.core.Vertx;
import io.vertx.grpc.VertxChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class HostWorkerRegisterManager {

    private Vertx vertx;

    private Map<String, HostApplication> hostApplicationMap;

    private BaseManager baseManager;

    public HostWorkerRegisterManager(Vertx vertx, BaseManager baseManager) {
        this.vertx = vertx;
        this.hostApplicationMap = new HashMap<>();
        this.baseManager = baseManager;
    }

    /**
     * 宿主应用上线
     *
     * @param clientInfo 宿主应用的client信息
     */
    public void register(ClientInfo clientInfo) {
        Integer port = Objects.isNull(clientInfo.getPort()) ? 9974 : clientInfo.getPort();
        ManagedChannel channel = VertxChannelBuilder
                .forAddress(vertx, clientInfo.getIp(), port)
                .usePlaintext()
                .build();
        // 构造grpc代理
        VertxTransferServerGrpc.TransferServerVertxStub serverVertxStub = VertxTransferServerGrpc.newVertxStub(channel);
        clientInfo.setServerVertxStub(serverVertxStub);
        HostApplication hostApplication = new HostApplication();
        hostApplication.setIp(clientInfo.getIp());
        hostApplication.setClientInfo(clientInfo);
        hostApplication.setFunctionInfo(new HashMap<>());
        hostApplicationMap.put(clientInfo.getIp(), hostApplication);
        log.info("当前宿主应用的内容为 {}", JSON.toJSONString(hostApplicationMap));
    }


    // 根据String taskComponent, String taskService, String version 查询出 HostApplication
    private List<HostApplication> queryHostApplication(String taskComponent, String taskService, String version) throws Exception {
        Set<String> ipSet = this.baseManager.queryIpByFunctionKey(taskComponent, taskService, version);
        if (CollectionUtils.isEmpty(ipSet)) {
            throw new Exception("没有查询到对应的宿主应用");
        }
        return ipSet.stream().map(item -> hostApplicationMap.get(item)).collect(Collectors.toList());
    }


    /**
     * 随机获取宿主应用信息 去做执行
     *
     * @param taskComponent 任务组件
     * @param taskService   任务方法
     * @param version       版本
     * @return HostApplication 返回宿主应用信息
     * @throws Exception 获取宿主应用信息异常
     */
    public HostApplication queryClientInfo(String taskComponent, String taskService, String version) throws Exception {
        List<HostApplication> hostApplications = queryHostApplication(taskComponent, taskService, version);
        if (CollectionUtils.isEmpty(hostApplications)) {
            return null;
        }
        if (hostApplications.size() == 1) {
            return hostApplications.get(0);
        }
        int min = 0;
        int max = hostApplications.size() - 1;
        Random random = new Random();
        int randomNumber = random.nextInt(max - min + 1) + min;
        return hostApplications.get(randomNumber);
    }

    /**
     * 随机获取一个client
     */
    public HostApplication queryClientRandom() {
        // 随机获取一个client
        int min = 0;
        int max = hostApplicationMap.size() - 1;
        Random random = new Random();
        int randomNumber = random.nextInt(max - min + 1) + min;
        return (HostApplication) hostApplicationMap.values().toArray()[randomNumber];
    }

    /**
     * 宿主应用下线
     */
    public void offline(String ip) {
        HostApplication hostApplication = hostApplicationMap.get(ip);
        if (Objects.isNull(hostApplication)) {
            return;
        }
        hostApplicationMap.remove(ip);
        log.info("当前宿主应用的内容为 {}", JSON.toJSONString(hostApplicationMap));
    }

}
