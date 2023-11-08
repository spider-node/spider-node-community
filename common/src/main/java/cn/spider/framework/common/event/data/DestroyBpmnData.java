package cn.spider.framework.common.event.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.common.event.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-08  13:12
 * @Description: TODO
 * @Version: 1.0
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DestroyBpmnData extends EventData {
    private String bpmnName;
}
