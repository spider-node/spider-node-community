package cn.spider.framework.domain.area.data;

public class AiNodeInfo {
    private String returnsClass;

    private String parametersClass;

    private String otherClass;

    private String nodeId;

    private String nodeName;

    private String functionVersionId;

    public AiNodeInfo(String paramClass, String id, String name, String functionVersionId,String returnsClass,String otherClass) {
        this.returnsClass = returnsClass;
        this.parametersClass = paramClass;
        this.nodeId = id;
        this.nodeName = name;
        this.functionVersionId = functionVersionId;
        this.otherClass = otherClass;
    }

    public String getReturnsClass() {
        return returnsClass;
    }

    public void setReturnsClass(String returnsClass) {
        this.returnsClass = returnsClass;
    }

    public String getParametersClass() {
        return parametersClass;
    }

    public void setParametersClass(String parametersClass) {
        this.parametersClass = parametersClass;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getFunctionVersionId() {
        return functionVersionId;
    }

    public void setFunctionVersionId(String functionVersionId) {
        this.functionVersionId = functionVersionId;
    }

    public String getOtherClass() {
        return otherClass;
    }

    public void setOtherClass(String otherClass) {
        this.otherClass = otherClass;
    }
}
