package cn.spider.framework.controller.broker.data;

import cn.spider.framework.controller.sdk.interfaces.BrokerHeartService;
import cn.spider.framework.controller.sdk.interfaces.FollowerHeartService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.broker.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-18  18:02
 * @Description: TODO
 * @Version: 1.0
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrokerInfo {
    private String brokerName;

    private String brokerIp;

    private BrokerHeartService brokerHeartService;
}
