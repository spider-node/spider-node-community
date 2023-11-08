package cn.spider.framework.linker.server.socket;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.domain.sdk.interfaces.WorkerInterface;
import cn.spider.framework.linker.server.enums.ClientStatus;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import lombok.extern.slf4j.Slf4j;

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

    private WorkerInterface workerInterface;

    public WorkerRegisterManager(NetServer netServer, ClientRegisterCenter clientRegisterCenter,Vertx vertx,WorkerInterface workerInterface) {
        this.netServer = netServer;
        this.clientRegisterCenter = clientRegisterCenter;
        this.vertx = vertx;
        this.workerInterface = workerInterface;
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
                SocketAddress socketAddress = socket.remoteAddress();
                String ip = socketAddress.host();
                ClientInfo clientInfo = JSON.parseObject(buffer.toString(), ClientInfo.class);
                if(clientInfo.getHeart()){
                    log.info("心跳数据 {}",JSON.toJSONString(clientInfo));
                    return;
                }
                workerInterface.queryWorkerInfo(new JsonObject().put("workerName",clientInfo.getWorkerName())).onSuccess(suss->{
                    JsonObject workerInfo = suss;
                    // 获取到该服务的-rpc端口号
                    clientInfo.setClientStatus(ClientStatus.NORMAL);
                    clientInfo.setPort(workerInfo.getInteger("rpcPort"));
                    clientInfo.setRemoteAddress(ip);
                    log.info("接收到的数据为 {}", JSON.toJSONString(clientInfo));
                    // 按照协议响应给客户端
                    clientRegisterCenter.registerClient(clientInfo);
                    // 上报给leader-controller
                    socket.write(Buffer.buffer("spider-server"));
                    // 校验是建立链接还是 心跳。如果是建立链接发出的信息，就注册关闭
                    monitorSocketClose(socket,clientInfo);
                }).onFailure(fail->{
                    log.error("获取worker-port.fail {}", ExceptionMessage.getStackTrace(fail));
                });

            });
        });
    }

    private void monitorSocketClose(NetSocket socket,ClientInfo clientInfo){
        socket.closeHandler(close -> {
            // 移除ip对应的数据,防止下次被选中
            clientRegisterCenter.destroy(clientInfo.getIp(),clientInfo.getWorkerName());
        });
    }

    public void startNetServer(){
        String brokerIp = BrokerInfoUtil.queryBrokerIp(this.vertx);
        netServer.listen(9064, brokerIp, res -> {
            if (res.succeeded()) {
                log.info("服务器启动成功");
            }
        });
    }

}
