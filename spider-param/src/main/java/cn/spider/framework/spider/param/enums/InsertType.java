package cn.spider.framework.spider.param.enums;

public enum InsertType {
    SINGLE("single"),
    BATCH("multiple");

    private String type;

    InsertType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
