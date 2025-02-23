package cn.spider.framework.common.event.data;

public class ScaleUpData extends EventData {
    private String yaml;

    private Integer replicas;

    private String functionVersionId;

    public ScaleUpData(String yaml, Integer replicas, String functionVersionId) {
        this.yaml = yaml;
        this.replicas = replicas;
        this.functionVersionId = functionVersionId;
    }

    public ScaleUpData() {
    }


    public String getYaml() {
        return yaml;
    }

    public void setYaml(String yaml) {
        this.yaml = yaml;
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
