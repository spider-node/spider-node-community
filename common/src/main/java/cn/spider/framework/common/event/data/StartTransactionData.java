package cn.spider.framework.common.event.data;

import cn.spider.framework.common.event.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartTransactionData extends EventData {
    /**
     * 请求链路id
     */
    private String requestId;

    /**
     * 节点名称
     */
    private String flowElementName;

    /**
     * 节点id
     */
    private String flowElementId;

    /**
     * 事务组id
     */
    private String transactionGroupId;

    /**
     * 节点的事务id
     */
    private String branchId;

    /**
     * 事务操作
     */
    private TransactionType transactionOperate;
}
