package cn.spider.framework.controller.impl;
import cn.spider.framework.common.role.BrokerRole;
import cn.spider.framework.controller.BrokerRoleManager;
import cn.spider.framework.controller.sdk.data.QueryRoleResult;
import cn.spider.framework.controller.sdk.interfaces.RoleService;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.impl
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-23  17:23
 * @Description: TODO
 * @Version: 1.0
 */
public class RoleServiceImpl implements RoleService {

    private BrokerRoleManager brokerRoleManager;

    public RoleServiceImpl(BrokerRoleManager brokerRoleManager) {
        this.brokerRoleManager = brokerRoleManager;
    }

    @Override
    public Future<JsonObject> queryRole() {
        BrokerRole role = brokerRoleManager.queryBrokerRole();
        QueryRoleResult queryRoleResult = new QueryRoleResult();
        queryRoleResult.setRole(role);
        return Future.succeededFuture(JsonObject.mapFrom(queryRoleResult));
    }
}
