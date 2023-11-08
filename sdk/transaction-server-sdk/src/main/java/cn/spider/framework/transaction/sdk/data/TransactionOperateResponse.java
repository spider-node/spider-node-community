package cn.spider.framework.transaction.sdk.data;
import java.util.List;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.transaction.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-11  01:21
 * @Description: 事务操作的返回类
 * @Version: 1.0
 */
public class TransactionOperateResponse {
    private String groupId;

    private List<TransactionOperateStatus> operateStatusList;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<TransactionOperateStatus> getOperateStatusList() {
        return operateStatusList;
    }

    public void setOperateStatusList(List<TransactionOperateStatus> operateStatusList) {
        this.operateStatusList = operateStatusList;
    }
}
