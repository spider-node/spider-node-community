package cn.spider.framework.common.event.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.common.event.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-29  13:23
 * @Description: 停止功能的实体
 * @Version: 1.0
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StopFunctionData extends EventData {
    private String functionId;
}
