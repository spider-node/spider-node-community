package cn.spider.framework.domain.area.function.data;

public class FunctionParamConfigModel {
    private String domainField;

    private String fieldDesc;

    private String fieldName;

    private String fieldType;

    private boolean required;

    private String realFieldType;

    public String getDomainField() {
        return domainField;
    }

    public void setDomainField(String domainField) {
        this.domainField = domainField;
    }

    public String getFieldDesc() {
        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getRealFieldType() {
        return realFieldType;
    }

    public void setRealFieldType(String realFieldType) {
        this.realFieldType = realFieldType;
    }
}
