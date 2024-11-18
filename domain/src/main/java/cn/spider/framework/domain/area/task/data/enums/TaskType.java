package cn.spider.framework.domain.area.task.data.enums;

public enum TaskType {
    // NEWLY_ADDED/ITERATION
    NEWLY_ADDED("NEWLY_ADDED"),
    ITERATION("ITERATION");

    private String type;

    TaskType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
