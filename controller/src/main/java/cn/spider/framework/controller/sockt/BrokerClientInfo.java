package cn.spider.framework.controller.sockt;

import io.vertx.core.net.NetSocket;
import lombok.Builder;
import lombok.Data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.sockt
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-19  18:59
 * @Description:
 * @Version: 1.0
 */
@Builder
@Data
public class BrokerClientInfo {

    /**
     * broker配置的名称（该名称唯一）
     */
    private String brokerName;

    /**
     * broker配置的ip
     */
    private String brokerIp;

    /**
     * socket
     */
    private NetSocket socket;

    /**
     * 虚拟ip
     */
    private String virtuallyIp;
}
