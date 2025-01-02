package cn.spider.framework.common.event.data;

public class DeleteDeployData  extends EventData  {
    private String deploymentName;

    private String functionVersionId;

    public DeleteDeployData(String deploymentName, String functionVersionId) {
        this.deploymentName = deploymentName;
        this.functionVersionId = functionVersionId;
    }

    public DeleteDeployData() {
    }

    public String getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }

    public String getFunctionVersionId() {
        return functionVersionId;
    }

    public void setFunctionVersionId(String functionVersionId) {
        this.functionVersionId = functionVersionId;
    }
}
