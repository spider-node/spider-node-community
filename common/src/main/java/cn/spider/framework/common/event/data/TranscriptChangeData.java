package cn.spider.framework.common.event.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.common.event.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-25  14:05
 * @Description: 副本变化的事件
 * @Version: 1.0
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptChangeData extends EventData {
    private String brokerName;

    private Set<String> transcript;
}
