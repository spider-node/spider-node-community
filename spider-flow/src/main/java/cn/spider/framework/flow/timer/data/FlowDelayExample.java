package cn.spider.framework.flow.timer.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.timer.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-07-05  14:28
 * @Description: TODO
 * @Version: 1.0
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowDelayExample implements Serializable {
    private String brokerName;

    private String exampleId;
}
