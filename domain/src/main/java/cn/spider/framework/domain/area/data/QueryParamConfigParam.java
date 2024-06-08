package cn.spider.framework.domain.area.data;

import java.util.Set;

public class QueryParamConfigParam {

    private Set<String> taskComponents;

    private Set<String> taskServices;

    public Set<String> getTaskComponents() {
        return taskComponents;
    }

    public void setTaskComponents(Set<String> taskComponents) {
        this.taskComponents = taskComponents;
    }

    public Set<String> getTaskServices() {
        return taskServices;
    }

    public void setTaskServices(Set<String> taskServices) {
        this.taskServices = taskServices;
    }
}
