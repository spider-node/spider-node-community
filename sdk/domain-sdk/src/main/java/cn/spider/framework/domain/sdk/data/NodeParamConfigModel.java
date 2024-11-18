package cn.spider.framework.domain.sdk.data;

import java.io.Serializable;
import java.util.List;

public class NodeParamConfigModel implements Serializable {
    private List<NodeParamConfig> nodeParamInfoList;

    public List<NodeParamConfig> getNodeParamInfoList() {
        return nodeParamInfoList;
    }

    public void setNodeParamInfoList(List<NodeParamConfig> nodeParamInfoList) {
        this.nodeParamInfoList = nodeParamInfoList;
    }
}
