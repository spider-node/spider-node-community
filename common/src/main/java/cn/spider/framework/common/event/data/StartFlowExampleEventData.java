package cn.spider.framework.common.event.data;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.common.event.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-19  01:44
 * @Description: TODO
 * @Version: 1.0
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StartFlowExampleEventData extends EventData {

    /**
     * 流程的其实号
     */
    private String startId;

    /**
     * 链路唯一的请求id
     */
    private String requestId;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 功能id
     */
    private String functionId;

    /**
     * 请求参数
     */
    private Object requestParam;

    /**
     * 请求参数的class路径
     */
    private String requestClassType;

}
