package cn.spider.framework.common.event.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.common.event.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-20  01:15
 * @Description: TODO
 * @Version: 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NotifyLeaderCommissionData extends EventData {
    private String brokerName;

    private String brokerIp;
}