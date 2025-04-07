package cn.spider.framework.param.sdk.data;

public class TestJsRuntimeResultModel {
    /**
     * 函数名称
     */
    private String jsFunctionName;

    /**
     * 节点id
     */
    private String nodeId;

    /**
     * 执行状态
     */
    private Boolean runStatus;

    public TestJsRuntimeResultModel(String jsFunctionName, String nodeId, Boolean runStatus) {
        this.jsFunctionName = jsFunctionName;
        this.nodeId = nodeId;
        this.runStatus = runStatus;
    }
    public TestJsRuntimeResultModel() {
    }

    public String getJsFunctionName() {
        return jsFunctionName;
    }

    public void setJsFunctionName(String jsFunctionName) {
        this.jsFunctionName = jsFunctionName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Boolean getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(Boolean runStatus) {
        this.runStatus = runStatus;
    }
}
