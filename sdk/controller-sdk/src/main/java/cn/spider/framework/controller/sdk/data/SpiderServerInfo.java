package cn.spider.framework.controller.sdk.data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-24  13:02
 * @Description: TODO
 * @Version: 1.0
 */
public class SpiderServerInfo {
    private String brokerName;
    private String brokerIp;

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public String getBrokerIp() {
        return brokerIp;
    }

    public void setBrokerIp(String brokerIp) {
        this.brokerIp = brokerIp;
    }
}
