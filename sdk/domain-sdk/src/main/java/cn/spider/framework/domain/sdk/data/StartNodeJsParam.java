package cn.spider.framework.domain.sdk.data;

import java.util.Set;

public class StartNodeJsParam {
    private String functionVersionId;

    private String nodeId;

    private Set<String> paramJsDemand;

    public String getFunctionVersionId() {
        return functionVersionId;
    }

    public void setFunctionVersionId(String functionVersionId) {
        this.functionVersionId = functionVersionId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Set<String> getParamJsDemand() {
        return paramJsDemand;
    }

    public void setParamJsDemand(Set<String> paramJsDemand) {
        this.paramJsDemand = paramJsDemand;
    }
}
