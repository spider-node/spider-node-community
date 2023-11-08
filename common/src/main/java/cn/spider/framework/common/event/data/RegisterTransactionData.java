package cn.spider.framework.common.event.data;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.transaction.TransactionStatus;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.common.event.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-25  18:40
 * @Description: TODO
 * @Version: 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RegisterTransactionData extends EventData {
    /**
     * 请求id
     */
    private String requestId;

    /**
     * 事务状态
     */
    private TransactionStatus transactionStatus;

    /**
     * 事务组id --相当于xid
     */
    private String transactionGroupId;

    /**
     * 单个事务id
     */
    private String branchId;

    /**
     * 服务
     */
    private String workerName;

    private String taskId;
}
