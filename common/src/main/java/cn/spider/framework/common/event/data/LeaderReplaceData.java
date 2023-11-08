package cn.spider.framework.common.event.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.common.event.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-25  17:01
 * @Description: TODO
 * @Version: 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LeaderReplaceData extends EventData {
    /**
     * 新的leader
     */
    private String newLeaderTransaction;

    /**
     * 旧leader
     */
    private String oldLeaderTransaction;
}
