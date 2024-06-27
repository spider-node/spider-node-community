package cn.spider.framework.common.event.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NotifyLeaderCommissionData extends EventData {
    private String brokerName;

    private String brokerIp;
}