package cn.spider.framework.common.event.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
