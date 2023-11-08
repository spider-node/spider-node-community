package cn.spider.framework.common.event.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.common.event.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-09-03  18:12
 * @Description: 功能版本的start
 * @Version: 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FunctionVersionStartStopEventData {
    private String versionId;

    private String status;

    private String functionId;
}
