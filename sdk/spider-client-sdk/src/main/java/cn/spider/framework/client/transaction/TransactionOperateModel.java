package cn.spider.framework.client.transaction;

/**
 * @program: spider-node
 * @description:
 * @author: dds
 * @create: 2023-03-06 14:13
 */
public class TransactionOperateModel {
    private String xid;
    private String resourceId;
    private String branchId;

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }
}
