package cn.spider.framework.transaction.server.example;

import cn.spider.framework.transaction.sdk.data.enums.TransactionStatus;
import cn.spider.framework.transaction.server.example.enums.TaskStatus;
import cn.spider.framework.transaction.server.example.enums.TransactionRunType;
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
public class TransactionElement {

    /**
     * 顺序 回滚需要根据顺序回滚
     */
    private int order;
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
    private Long branchId;

    /**
     * 服务
     */
    private String workerName;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 错误数量
     */
    private Integer failNum;

    /**
     * 资源id- 用于控制找到对应的数据源
     */
    private String datasourceId;

    /**
     * 任务的执行状态
     */
    private TaskStatus taskStatus;

    private TransactionRunType transactionRunType;

    public void recordFailNum() {
        if (Objects.isNull(this.failNum)) {
            this.failNum = 0;
        }
        this.failNum = this.failNum + 1;
    }

}
