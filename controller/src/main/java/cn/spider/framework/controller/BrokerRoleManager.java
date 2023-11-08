package cn.spider.framework.controller;

import cn.spider.framework.common.role.BrokerRole;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-23  17:14
 * @Description: TODO
 * @Version: 1.0
 */

public class BrokerRoleManager {
    private BrokerRole brokerRole;

    public void setUp(BrokerRole brokerRole){
        this.brokerRole = brokerRole;
    }

    public BrokerRole queryBrokerRole(){
        return this.brokerRole;
    }
}
