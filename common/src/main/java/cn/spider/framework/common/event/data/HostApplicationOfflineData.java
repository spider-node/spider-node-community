package cn.spider.framework.common.event.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 宿主应用下线时间的数据体
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class HostApplicationOfflineData extends EventData {
    private String ip;

    private String brokerName;
}
