package cn.spider.framework.linker.server.socket;
import cn.spider.framework.domain.sdk.data.RefreshAreaParam;
import cn.spider.framework.linker.sdk.data.emuns.EscalationType;
import cn.spider.framework.linker.sdk.data.emuns.FunctionEscalationType;
import cn.spider.framework.linker.server.enums.ClientStatus;
import cn.spider.framework.linker.server.socket.data.WorkerType;
import cn.spider.framework.proto.grpc.VertxTransferServerGrpc;


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

    /**
     * 交互类型
     */
    private EscalationType escalationType;

    /**
     * 上报的领域信息
     */
    private RefreshAreaParam refreshAreaParam;

    private FunctionEscalationType functionEscalationType;

    /**
     * worker类型,目前支付微服务,宿主机应用
     */
    private WorkerType workerType;

    public WorkerType getWorkerType() {
        return workerType;
    }

    public void setWorkerType(WorkerType workerType) {
        this.workerType = workerType;
    }

    public FunctionEscalationType getFunctionEscalationType() {
        return functionEscalationType;
    }

    public void setFunctionEscalationType(FunctionEscalationType functionEscalationType) {
        this.functionEscalationType = functionEscalationType;
    }

    public RefreshAreaParam getRefreshAreaParam() {
        return refreshAreaParam;
    }

    public void setRefreshAreaParam(RefreshAreaParam refreshAreaParam) {
        this.refreshAreaParam = refreshAreaParam;
    }

    public EscalationType getEscalationType() {
        return escalationType;
    }

    public void setEscalationType(EscalationType escalationType) {
        this.escalationType = escalationType;
    }

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
