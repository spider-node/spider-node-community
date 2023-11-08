package cn.spider.framework.controller.sdk.data;

import cn.spider.framework.common.role.BrokerRole;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-23  18:23
 * @Description: 查询角色信息
 * @Version: 1.0
 */
public class QueryRoleResult {
    private BrokerRole role;

    public BrokerRole getRole() {
        return role;
    }

    public void setRole(BrokerRole role) {
        this.role = role;
    }
}
