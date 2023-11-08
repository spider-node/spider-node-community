package cn.spider.framework.transaction.sdk.datasource;

/**
 * @program: flow-cloud
 * @description:
 * @author: dds
 * @create: 2022-07-30 18:41
 */
public class Phase2Context {
    /**
     * AT Phase 2 context
     *
     * @param xid        the xid
     * @param branchId   the branch id
     * @param resourceId the resource id
     */
    public Phase2Context(String xid, long branchId, String resourceId) {
        this.xid = xid;
        this.branchId = branchId;
        this.resourceId = resourceId;
    }

    /**
     * The Xid.
     */
    String xid;
    /**
     * The Branch id.
     */
    long branchId;
    /**
     * The Resource id.
     */
    String resourceId;

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public long getBranchId() {
        return branchId;
    }

    public void setBranchId(long branchId) {
        this.branchId = branchId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
