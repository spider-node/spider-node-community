package cn.spider.framework.controller.leader;

import io.vertx.core.net.NetSocket;
import lombok.Builder;
import lombok.Data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.leader
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-20  13:27
 * @Description: TODO
 * @Version: 1.0
 */
@Builder
@Data
public class Leader {
    private String brokerName;

    private String brokerIp;

    private NetSocket socket;
}
