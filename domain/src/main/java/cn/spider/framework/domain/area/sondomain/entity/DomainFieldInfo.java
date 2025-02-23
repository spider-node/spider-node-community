package cn.spider.framework.domain.area.sondomain.entity;

public class DomainFieldInfo {
    /**
     * 字段名称
     */
    private String field;

    /**
     * 字段类型
     */
    private String type;

    /**
     * 字段描述
     */
    private String fieldDesc;

    /**
     * 表结构中的字段名称
     */
    private String tableField;

    private String areaFiled;

    private Boolean uniqueIndex;

    public String getAreaFiled() {
        return areaFiled;
    }

    public void setAreaFiled(String areaFiled) {
        this.areaFiled = areaFiled;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFieldDesc() {
        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }

    public String getTableField() {
        return tableField;
    }

    public void setTableField(String tableField) {
        this.tableField = tableField;
    }

    public Boolean getUniqueIndex() {
        return uniqueIndex;
    }

    public void setUniqueIndex(Boolean uniqueIndex) {
        this.uniqueIndex = uniqueIndex;
    }
}
