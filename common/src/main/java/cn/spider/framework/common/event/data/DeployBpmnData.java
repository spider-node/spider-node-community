package cn.spider.framework.common.event.data;

import cn.spider.framework.common.data.enums.BpmnStatus;
import lombok.Builder;
import lombok.Data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.container.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-26  18:16
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class DeployBpmnData extends EventData {
    private String bpmnName;

    private BpmnStatus status;

    private String url;

    private String id;
}
