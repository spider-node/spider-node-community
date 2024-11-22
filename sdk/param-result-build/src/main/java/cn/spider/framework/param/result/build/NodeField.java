package cn.spider.framework.param.result.build;

import java.util.List;

public class NodeField {
    private String fieldName;
    private String targetName;

    private String fieldType;

    private List<NodeObjectStructure> nodeParamStructure;

    public NodeField(String fieldName, String targetName, String fieldType) {
        this.fieldName = fieldName;
        this.targetName = targetName;
        this.fieldType = fieldType;
    }

    public NodeField() {
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public List<NodeObjectStructure> getNodeParamStructure() {
        return nodeParamStructure;
    }

    public void setNodeParamStructure(List<NodeObjectStructure> nodeParamStructure) {
        this.nodeParamStructure = nodeParamStructure;
    }
}
