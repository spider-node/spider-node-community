package cn.spider.framework.common.event.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BizHostOfflineData extends EventData {
    /**
     * 功能名称
     */
    private String bizName;

    /**
     * 功能版本
     */
    private String bizVersion;

    /**
     * 宿主机ip
     */
    private String ip;
}
