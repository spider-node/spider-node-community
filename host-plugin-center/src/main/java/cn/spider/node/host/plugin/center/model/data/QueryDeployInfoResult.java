package cn.spider.node.host.plugin.center.model.data;

import cn.spider.node.host.plugin.center.model.entity.SpiderPluginDeployInfo;

import java.util.List;

public class QueryDeployInfoResult {
    private List<SpiderPluginDeployInfo> deployInfos;

    public QueryDeployInfoResult(List<SpiderPluginDeployInfo> deployInfos) {
        this.deployInfos = deployInfos;
    }

    public List<SpiderPluginDeployInfo> getDeployInfos() {
        return deployInfos;
    }

    public void setDeployInfos(List<SpiderPluginDeployInfo> deployInfos) {
        this.deployInfos = deployInfos;
    }
}
