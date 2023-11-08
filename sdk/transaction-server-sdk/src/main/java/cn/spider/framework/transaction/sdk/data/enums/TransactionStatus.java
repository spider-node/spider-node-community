package cn.spider.framework.transaction.sdk.data.enums;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.transaction.server.enums
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-07  11:44
 * @Description: 事务状态
 * @Version: 1.0
 */
public enum TransactionStatus {
    INIT,
    COMMIT_SUSS,
    COMMIT_FAIL,
    ROLL_BACK_SUSS,
    ROLL_BACK_FAIL,
}
