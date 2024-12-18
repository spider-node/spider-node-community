package cn.spider.framework.domain.sdk.data;

import javax.naming.ldap.PagedResultsControl;

public class FunctionParamInfo {

    /**
     * 领域与字段名
     */
    private String targetName;

    /**
     * 实际字段名称
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private String fieldType;

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
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
}
