package cn.spider.framework.transaction.sdk.datasource.isolate;

public enum TransactionOperationStatus {
    COMMIT("提交"),
    ROLL_BACK("回滚")
    ;
    TransactionOperationStatus(String desc) {
        this.desc = desc;
    }

    private String desc;
}
