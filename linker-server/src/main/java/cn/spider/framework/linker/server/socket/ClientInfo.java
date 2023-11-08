package cn.spider.framework.linker.server.socket;

import cn.spider.framework.linker.server.enums.ClientStatus;
import cn.spider.framework.proto.grpc.VertxTransferServerGrpc;
import io.vertx.core.http.HttpServer;

/**
 * @program: spider-node
 * @description: 客户端的-详情信息
 * @author: dds
 * @create: 2023-02-24 17:53
 */
public class ClientInfo {
    /**
     * ip
     */
    private String ip;
    /**
     * 域名
     */
    private String domain;

    /**
     * client状态
     */
    private ClientStatus clientStatus;

    /**
     * 工作者名称
     */
    private String workerName;

    private String hostName;

    private String remoteAddress;

    private Boolean isHeart;

    private Integer port;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean getHeart() {
        return isHeart;
    }

    public void setHeart(Boolean heart) {
        isHeart = heart;
    }

    private VertxTransferServerGrpc.TransferServerVertxStub serverVertxStub;

    public ClientInfo(String ip, String domain, ClientStatus clientStatus, String workerName,Boolean isHeart) {
        this.ip = ip;
        this.domain = domain;
        this.clientStatus = clientStatus;
        this.workerName = workerName;
        this.isHeart = isHeart;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public VertxTransferServerGrpc.TransferServerVertxStub getServerVertxStub() {
        return serverVertxStub;
    }

    public void setServerVertxStub(VertxTransferServerGrpc.TransferServerVertxStub serverVertxStub) {
        this.serverVertxStub = serverVertxStub;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public ClientStatus getClientStatus() {
        return clientStatus;
    }

    public void setClientStatus(ClientStatus clientStatus) {
        this.clientStatus = clientStatus;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }
}
