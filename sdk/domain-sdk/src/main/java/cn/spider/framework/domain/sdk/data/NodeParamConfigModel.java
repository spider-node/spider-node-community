package cn.spider.framework.domain.sdk.data;

import java.io.Serializable;
import java.util.List;

public class NodeParamConfigModel implements Serializable {
    private List<NodeParamConfig> nodeParamConfigs;

    public List<NodeParamConfig> getNodeParamConfigs() {
        return nodeParamConfigs;
    }

    public void setNodeParamConfigs(List<NodeParamConfig> nodeParamConfigs) {
        this.nodeParamConfigs = nodeParamConfigs;
    }
}
