package cn.spider.framework.common.event.data;

import cn.spider.framework.common.event.enums.TransactionStatus;
import cn.spider.framework.common.event.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.common.event.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-19  12:28
 * @Description: 事务结束节点
 * @Version: 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EndTransactionData extends EventData {

    /**
     * 请求链路id
     */
    private String requestId;

    /**
     * 事务组id
     */
    private String transactionGroupId;

    /**
     * 节点id
     */
    private String flowElementId;

    /**
     * 节点的事务id
     */
    private String branchId;

    /**
     * 事务状态
     */
    private TransactionStatus transactionStatus;

    /**
     * 节点名称
     */
    private String flowElementName;


    /**
     * 事务操作
     */
    private TransactionType transactionOperate;
}
