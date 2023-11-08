package cn.spider.framework.common.event.data;

import cn.spider.framework.common.event.enums.FlowExampleStatus;
import cn.spider.framework.common.event.enums.TransactionStatus;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.common.event.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-19  01:57
 * @Description: TODO
 * @Version: 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EndFlowExampleEventData extends EventData{
    /**
     * 链路唯一的请求id
     */
    private String requestId;

    /**
     * 功能id
     */
    private String functionId;

    /**
     * 返回结果
     */
    private JsonObject result;

    /**
     * 流程状态-成功，失败
     */
    private FlowExampleStatus status;

    /**
     * 事务状态
     */
    private TransactionStatus transactionStatus;

    /**
     * 异常信息
     */
    private String exception;

}
