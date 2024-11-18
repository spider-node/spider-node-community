package cn.spider.framework.param.result.build.model.enums;

public enum WorkerType {
    //MICROSERVICE(微服务)/HOST_APPLICATION(宿主应用)
    MICROSERVICE("MICROSERVICE"),
    HOST_APPLICATION("HOST_APPLICATION");

    private String type;

    WorkerType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
