package cn.spider.framework.common.event.data;

import cn.spider.framework.common.event.enums.ElementStatus;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.common.event.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-19  02:15
 * @Description: 流程节点执行结束
 * @Version: 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EndElementExampleData extends EventData {

    /**
     * 流程节点id
     */
    private String flowElementId;

    /**
     * 请求的链路id
     */
    private String requestId;

    /**
     * 请求返回的参数
     */
    private Object returnParam;

    /**
     * 返回参数的类型
     */
    private String returnClassType;

    /**
     * 节点执行参数
     */
    private String requestParam;

    /**
     * 异常信息
     */
    private String exception;

    /**
     * 功能执行的状态
     */
    private ElementStatus status;

}
