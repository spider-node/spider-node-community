package cn.spider.framework.domain.area.task.data;

import cn.spider.framework.domain.area.task.entity.SpiderDomainFunctionTask;

import java.util.List;

public class QueryDomainFunctionTaskResult {
    private List<SpiderDomainFunctionTask> tasks;



    public List<SpiderDomainFunctionTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<SpiderDomainFunctionTask> tasks) {
        this.tasks = tasks;
    }
}
