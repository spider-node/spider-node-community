package cn.spider.framework.param.result.build;

public class NodeObjectStructure {
    private String paramType;

    private String fieldName;


    public NodeObjectStructure(String paramType, String fieldName) {
        this.paramType = paramType;
        this.fieldName = fieldName;
    }

    public NodeObjectStructure() {
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
