package cn.spider.framework.domain.sdk.data;

public class NodeModelParam {
    private String paramType;

    private String fieldName;

    private String targetName;

    public NodeModelParam(String paramType, String fieldName, String targetName) {
        this.paramType = paramType;
        this.fieldName = fieldName;
        this.targetName = targetName;
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

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
}
