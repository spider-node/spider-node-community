package cn.spider.framework.linker.client.timer;

import cn.spider.framework.linker.client.socket.SocketManager;
import com.google.common.collect.Maps;
import io.vertx.core.Vertx;

import java.util.Map;
import java.util.Objects;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.linker.client.timer
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-30  22:55
 * @Description: 业务timer
 * @Version: 1.0
 */
public class BusinessTimer {

    private Vertx vertx;

    private Map<String, Long> serverIpHeartMap;

    public BusinessTimer(Vertx vertx) {
        this.vertx = vertx;
        this.serverIpHeartMap = Maps.newHashMap();
    }

    /**
     * 延迟鱼server端建立链接
     *
     * @param serverIp
     */
    public void delayConnectServer(String serverIp, SocketManager socketManager) {
    }

    /**
     * 注册周期性延迟任务
     *
     * @param serverIp
     * @param socketManager
     */
    public void registerSocketHeart(String serverIp, SocketManager socketManager) {

        Long timerId = vertx.setPeriodic(1000 * 15, id -> {
            socketManager.heart(serverIp);
        });
        serverIpHeartMap.put(serverIp, timerId);
    }

    /**
     * 取消周期性任务
     *
     * @param serverIp
     */
    public void cancelHeart(String serverIp) {
        Long timerId = serverIpHeartMap.get(serverIp);
        if (Objects.isNull(timerId)) {
            return;
        }
        vertx.cancelTimer(timerId);
    }

    /**
     * 获取spider-service信息
     * @param socketManager
     */
    public void updateSpiderServer(SocketManager socketManager,Integer rpcPort) {
        vertx.setPeriodic(20 * 1000, id -> {
            socketManager.connect(rpcPort);
        });
    }

}
