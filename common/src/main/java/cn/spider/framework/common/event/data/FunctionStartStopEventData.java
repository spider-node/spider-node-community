package cn.spider.framework.common.event.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.common.event
 * @Author: dengdongsheng
 * @CreateTime: 2023-09-03  16:46
 * @Description: 功能启停的实体类
 * @Version: 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FunctionStartStopEventData extends EventData {
    private String functionId;

    private String status;
}
