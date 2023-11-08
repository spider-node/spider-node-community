package cn.spider.framework.controller.sdk.data;

import java.util.List;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-24  13:02
 * @Description: TODO
 * @Version: 1.0
 */
public class QuerySpiderServerResult {

    private List<SpiderServerInfo> serverInfoList;

    public QuerySpiderServerResult(){}

    public QuerySpiderServerResult(List<SpiderServerInfo> serverInfoList) {
        this.serverInfoList = serverInfoList;
    }

    public List<SpiderServerInfo> getServerInfoList() {
        return serverInfoList;
    }
}
