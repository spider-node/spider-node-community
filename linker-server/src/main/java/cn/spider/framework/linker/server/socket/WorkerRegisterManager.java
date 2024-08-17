package cn.spider.framework.linker.server.socket;

import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.domain.sdk.data.RefreshAreaModel;
import cn.spider.framework.domain.sdk.data.RefreshAreaParam;
import cn.spider.framework.domain.sdk.interfaces.NodeInterface;
import cn.spider.framework.linker.sdk.data.emuns.FunctionEscalationType;
import cn.spider.framework.linker.server.enums.ClientStatus;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.shareddata.SharedData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;


/**
 * @program: spider-node
 * @description: 下游服务注册管理
 * @author: dds
 * @create: 2023-03-01 22:14
 */
@Slf4j
public class WorkerRegisterManager {
    /**
     * socket-server
     */
    private NetServer netServer;

    /**
     * client管理者
     */
    private ClientRegisterCenter clientRegisterCenter;

    private Vertx vertx;

    private SharedData sharedData;

    public WorkerRegisterManager(NetServer netServer, ClientRegisterCenter clientRegisterCenter, Vertx vertx) {
        this.netServer = netServer;
        this.clientRegisterCenter = clientRegisterCenter;
        this.vertx = vertx;
        this.sharedData = vertx.sharedData();
        init();
    }

    private void init() {
        createConnect();
        startNetServer();
    }

    /**
     * 开启 接受创建链接，关闭链接，需要做的事情
     */
    public void createConnect() {
        netServer.connectHandler(socket -> {
            socket.handler(buffer -> {
                // 在这里应该解析报文，封装为协议对象，并找到响应的处理类，得到处理结果，并响应
                log.info("上传数据为 {}", buffer.toString());
                ClientInfo clientInfo = JSON.parseObject(buffer.toString(), ClientInfo.class);
                switch (clientInfo.getEscalationType()) {
                    case HEART:
                        log.info("心跳数据 {}", JSON.toJSONString(clientInfo));
                        break;
                    case REGISTER:
                        register(clientInfo, socket);
                        break;
                }
            });
        });
    }

    /**
     * 监听 宿主机 是否断开
     *
     * @param socket     跟宿主机的通道
     * @param clientInfo 宿主机客户端信息
     */
    private void monitorSocketClose(NetSocket socket, ClientInfo clientInfo) {
        socket.closeHandler(close -> {
            // 移除ip对应的数据,防止下次被选中
            clientRegisterCenter.removeClient(clientInfo.getIp(), clientInfo.getWorkerName());
        });
    }

    /**
     * 监听端口
     */
    public void startNetServer() {
        String brokerIp = BrokerInfoUtil.queryBrokerIp(this.vertx);
        netServer.listen(9064, brokerIp, res -> {
            if (res.succeeded()) {
                log.info("服务器启动成功");
            }
        });
    }

    /**
     * 注册 宿主机信息
     *
     * @param clientInfo
     * @param socket
     */
    private void register(ClientInfo clientInfo, NetSocket socket) {
        SocketAddress socketAddress = socket.remoteAddress();
        String ip = socketAddress.host();
        // 获取到该服务的-rpc端口号
        clientInfo.setClientStatus(ClientStatus.NORMAL);
        clientInfo.setRemoteAddress(ip);
        log.info("接收到的数据为 {}", JSON.toJSONString(clientInfo));
        // 按照协议响应给客户端
        clientRegisterCenter.registerClient(clientInfo);
        // 上报给leader-controller
        socket.write(Buffer.buffer("spider-server"));
        // 校验是建立链接还是 心跳。如果是建立链接发出的信息，就注册关闭
        monitorSocketClose(socket, clientInfo);
    }

    /**
     * @param refreshAreaParam 领域信息
     * @param ip               宿主机的ip
     */
    public void escalationAreaInfo(RefreshAreaParam refreshAreaParam, String ip, FunctionEscalationType functionEscalationType) {
        if (Objects.isNull(refreshAreaParam) || CollectionUtils.isEmpty(refreshAreaParam.getAreaModelList())) {
            return;
        }
        switch (functionEscalationType) {
            case DEPLOY:
                List<RefreshAreaModel> areaModels = refreshAreaParam.getAreaModelList();
                for (RefreshAreaModel areaModel : areaModels) {
                    clientRegisterCenter.functionRegister(areaModel.getTaskComponent(), areaModel.getTaskService(), ip);
                }

                break;
            case UNLOCK:
                for (RefreshAreaModel refreshAreaModel : refreshAreaParam.getAreaModelList()) {
                    clientRegisterCenter.destroy(refreshAreaModel.getTaskComponent(), refreshAreaModel.getTaskService(), ip);
                }
                break;
        }

    }
}
