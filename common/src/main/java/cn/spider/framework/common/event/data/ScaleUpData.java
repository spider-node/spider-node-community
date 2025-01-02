package cn.spider.framework.common.event.data;

public class ScaleUpData extends EventData {
    private String deploymentName;

    private Integer replicas;

    private String functionVersionId;

    public ScaleUpData(String deploymentName, Integer replicas, String functionVersionId) {
        this.deploymentName = deploymentName;
        this.replicas = replicas;
        this.functionVersionId = functionVersionId;
    }

    public ScaleUpData() {
    }

    public String getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }

    public Integer getReplicas() {
        return replicas;
    }

    public void setReplicas(Integer replicas) {
        this.replicas = replicas;
    }

    public String getFunctionVersionId() {
        return functionVersionId;
    }

    public void setFunctionVersionId(String functionVersionId) {
        this.functionVersionId = functionVersionId;
    }
}
