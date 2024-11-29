package cn.spider.framework.linker.server.socket;

import cn.spider.framework.common.utils.TaskKeyUtil;
import cn.spider.framework.linker.server.socket.data.HostApplication;
import cn.spider.framework.proto.grpc.VertxTransferServerGrpc;
import com.alibaba.fastjson.JSON;
import io.grpc.ManagedChannel;
import io.vertx.core.Vertx;
import io.vertx.grpc.VertxChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

@Slf4j
public class HostWorkerRegisterManager {

    private Vertx vertx;

    private Map<String, HostApplication> hostApplicationMap;

    public HostWorkerRegisterManager(Vertx vertx) {
        this.vertx = vertx;
        this.hostApplicationMap = new HashMap<>();
    }

    // 定时同步 宿主应用，与插件的关系


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

    /**
     * 宿主应用下线
     *
     * @param ip 宿主应用的ip
     */
    public void cancellationHost(String ip) {
        hostApplicationMap.remove(ip);
    }

    /**
     * 注销功能
     *
     * @param ip            宿主应用ip
     * @param taskComponent 任务组件
     * @param taskService   任务service
     * @param version       版本
     */
    public void cancelFunction(String ip, String taskComponent, String taskService, String version) {
        String functionKey = TaskKeyUtil.buildTaskKey(taskComponent, taskService, version);
        HostApplication hostApplication = hostApplicationMap.get(ip);
        hostApplication.getFunctionInfo().remove(functionKey);
    }

    /**
     * 注册功能
     *
     * @param ip            宿主应用的ip
     * @param taskComponent 任务组件
     * @param taskService   任务service
     * @param version       版本
     */
    public void registerFunction(String ip, String taskComponent, String taskService, String version) {
        String functionKey = TaskKeyUtil.buildTaskKey(taskComponent, taskService, version);
        HostApplication hostApplication = hostApplicationMap.get(ip);
        hostApplication.getFunctionInfo().put(functionKey, null);
    }

    // 根据String taskComponent, String taskService, String version 查询出 HostApplication
    private List<HostApplication> queryHostApplication(String taskComponent, String taskService, String version) {
        String functionKey = TaskKeyUtil.buildTaskKey(taskComponent, taskService, version);
        List<HostApplication> hostApplications = new ArrayList<>();
        hostApplicationMap.forEach((key, value) -> {
            if (value.getFunctionInfo().containsKey(functionKey)) {
                hostApplications.add(value);
            }
        });
        return hostApplications;
    }




    public HostApplication queryClientInfo(String taskComponent, String taskService, String version) {
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

}
