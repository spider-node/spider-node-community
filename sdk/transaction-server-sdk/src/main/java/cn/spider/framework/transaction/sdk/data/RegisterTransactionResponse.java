package cn.spider.framework.transaction.sdk.data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.transaction.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-07  17:24
 * @Description: 注册返回类
 * @Version: 1.0
 */
public class RegisterTransactionResponse {
    /**
     * 相当于xid
     */
    private String groupId;

    /**
     * 相当于数据
     */
    private String branchId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }
}
