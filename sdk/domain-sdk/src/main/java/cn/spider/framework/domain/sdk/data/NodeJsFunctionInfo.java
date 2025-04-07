package cn.spider.framework.domain.sdk.data;

import java.util.List;

public class NodeJsFunctionInfo {
    private String functionVersionId;

    private List<NodeParamConfig> nodeParamConfigList;

    public String getFunctionVersionId() {
        return functionVersionId;
    }

    public void setFunctionVersionId(String functionVersionId) {
        this.functionVersionId = functionVersionId;
    }

    public List<NodeParamConfig> getNodeParamConfigList() {
        return nodeParamConfigList;
    }

    public void setNodeParamConfigList(List<NodeParamConfig> nodeParamConfigList) {
        this.nodeParamConfigList = nodeParamConfigList;
    }
}
