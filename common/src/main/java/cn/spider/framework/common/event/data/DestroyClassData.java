package cn.spider.framework.common.event.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.common.event.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-08  14:27
 * @Description: TODO
 * @Version: 1.0
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DestroyClassData extends EventData  {
    private String jarName;
}
