package cn.spider.framework.flow.business.data;

import cn.spider.framework.flow.business.enums.FunctionStatus;
import lombok.Data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.container.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-09  14:43
 * @Description: 功能的状态变化
 * @Version: 1.0
 */
@Data
public class FunctionStateChangeRequest {
    private String functionId;

    private FunctionStatus status;
}
