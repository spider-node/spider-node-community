package cn.spider.framework.linker.client.socket;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.linker.client.data.QuerySpiderServerResult;
import cn.spider.framework.linker.client.escalation.AreaInfoService;
import cn.spider.framework.linker.client.timer.BusinessTimer;
import cn.spider.framework.linker.client.util.IpUtil;
import cn.spider.framework.linker.sdk.data.emuns.EscalationType;
import cn.spider.framework.linker.sdk.data.emuns.FunctionEscalationType;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.net.InetAddress;
import java.util.Map;

/**
 * @program: spider-node
 * @description: 链接管理
 * @author: dds
 * @create: 2023-03-02 21:22
 */
@Slf4j
public class SocketManager {

    // 服务器启动-执行该逻辑
    private NetClient client;
    // 通信套接字

    private String workerName;

    private String workerIp;

    private BusinessTimer businessTimer;

    private Map<String, NetSocket> serverMap;

    private WebClient webClient;

    private String spiderServerIp;

    private int spiderServerPort;

    private AreaInfoService areaInfoService;


    public SocketManager(Vertx vertx,
                         String workerName,
                         BusinessTimer businessTimer,
                         WebClient webClient,
                         String spiderServerIp,
                         Integer rpcPort,
                         String spiderServerPort, Boolean isLocal,
                         AreaInfoService areaInfoService) {
        NetClientOptions options = new NetClientOptions()
                .setLogActivity(true)
                .setConnectTimeout(10000);
        this.client = vertx.createNetClient(options);
        this.workerName = workerName;
        this.businessTimer = businessTimer;
        try {
            if (isLocal) {
                this.workerIp = IpUtil.buildLocalHost();
            } else {
                this.workerIp = InetAddress.getLocalHost().getHostAddress();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.serverMap = Maps.newHashMap();
        this.webClient = webClient;
        this.spiderServerIp = spiderServerIp;
        this.spiderServerPort = Integer.parseInt(spiderServerPort);
        this.areaInfoService = areaInfoService;
        // 跟spider-server进行建立链接
        connect(rpcPort);
        businessTimer.updateSpiderServer(this, rpcPort);
    }

    // 请求spider-controller获取spider-server地址
    public void connect(Integer rpcPort) {
        this.webClient.post(this.spiderServerPort, this.spiderServerIp, "/query/spider/server/info")
                .sendJsonObject(new JsonObject())
                .onSuccess(res -> {
                    JsonObject body = res.bodyAsJsonObject();
                    log.info("获取到的spider-server-info {}", body.toString());
                    QuerySpiderServerResult result = body.getJsonObject("data").mapTo(QuerySpiderServerResult.class);
                    result.getServerInfoList().forEach(item -> {
                        connectSpiderServer(item.getBrokerIp(), rpcPort);
                    });
                })
                .onFailure(fail -> {
                    log.warn("请求-spiderServer失败 ip{} port {} 错误信息 {}", this.spiderServerIp, this.spiderServerPort, ExceptionMessage.getStackTrace(fail));
                });

    }

    public void monitorSocket(NetSocket socket, String serverIp) {
        // 监听客户端的退出连接
        socket.closeHandler(close -> {
            String serverIpNew = serverIp;
            // 移除进行重连
            this.serverMap.remove(serverIpNew);
            // 取消定时任务
            this.businessTimer.cancelHeart(serverIpNew);
        });
    }

    public void connectSpiderServer(String serverIp, Integer rpcPort) {
        if (this.serverMap.containsKey(serverIp)) {
            return;
        }
        this.client.connect(9064, serverIp, res -> {
            if (res.succeeded()) {
                NetSocket socket = res.result();
                this.serverMap.put(serverIp, socket);
                // 回写服务信息
                JsonObject clientInfo = new JsonObject();
                clientInfo.put("ip", workerIp);
                clientInfo.put("workerName", this.workerName);
                clientInfo.put("port", rpcPort);
                clientInfo.put("escalationType", EscalationType.REGISTER);
                socket.write(Buffer.buffer(clientInfo.toString()));
                monitorSocket(res.result(), serverIp);
                this.businessTimer.registerSocketHeart(serverIp, this);
                this.businessTimer.senAreaInfo(this, serverIp);
                // 注册 heart
            } else {
                log.error("跟spider-server通信进行链接失败 serverIp {} 错误信息为 {}", serverIp, ExceptionMessage.getStackTrace(res.cause()));
            }
        });
    }

    public void heart(String serverIp) {
        NetSocket socket = this.serverMap.get(serverIp);
        JsonObject clientInfo = new JsonObject();
        clientInfo.put("ip", workerIp);
        clientInfo.put("workerName", this.workerName);
        clientInfo.put("escalationType", EscalationType.HEART);
        socket.write(Buffer.buffer(clientInfo.toString()));
    }

    public void escalationAreaFunctionInfo(String serverIp) {
        NetSocket socket = this.serverMap.get(serverIp);
        JsonObject clientInfo = new JsonObject();
        clientInfo.put("ip", workerIp);
        clientInfo.put("workerName", this.workerName);
        clientInfo.put("escalationType", EscalationType.ESCALATION_AREA_INFO);
        socket.write(Buffer.buffer(clientInfo.toString()));
        // 注册延迟，5s后执行
        areaInfoService.escalationAreaInfo();
    }

    /**
     * 宿主机上报领域功能信息
     * areaInfo 包含 领域功能的参数信息，向spider每个服务端发起注册
     */
    public void escalationAreaFunctionInfo(JsonObject areaInfo, FunctionEscalationType functionEscalationType) {
        if (this.serverMap.isEmpty()) {
            log.info("spider_socket 不存在");
            return;
        }
        JsonObject clientInfo = new JsonObject();
        clientInfo.put("ip", workerIp);
        clientInfo.put("functionEscalationType", functionEscalationType);
        clientInfo.put("refreshAreaParam", areaInfo);
        // 调用spider-node进行上报
        this.webClient.post(this.spiderServerPort, this.spiderServerIp, "/escalation/area_info")
                .sendJsonObject(clientInfo)
                .onSuccess(res -> {
                    JsonObject body = res.bodyAsJsonObject();
                    log.info("获取到的spider-server-info {}", body.toString());
                })
                .onFailure(fail -> {
                    log.warn("请求-spiderServer失败 ip{} port {} 错误信息 {}", this.spiderServerIp, this.spiderServerPort, ExceptionMessage.getStackTrace(fail));
                });
    }
}
