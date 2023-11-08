package cn.spider.framework.transaction.server.example;

import cn.spider.framework.transaction.sdk.data.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.transaction.server.example
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-07  11:41
 * @Description: 事务实例
 * @Version: 1.0
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionExample {
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

    private Integer failNum;

    public void recordFailNum() {
        if (Objects.isNull(this.failNum)) {
            this.failNum = 0;
        }
        this.failNum = this.failNum + 1;
    }

}
