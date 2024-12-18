package cn.spider.framework.domain.area.task.data;

import cn.spider.framework.domain.area.task.entity.SpiderDomainFunctionAiCoderStep;

import java.util.List;

public class QueryAiCoderStepResult {
    private Long takeTime;

    private List<SpiderDomainFunctionAiCoderStep> steps;

    public Long getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(Long takeTime) {
        this.takeTime = takeTime;
    }

    public List<SpiderDomainFunctionAiCoderStep> getSteps() {
        return steps;
    }

    public void setSteps(List<SpiderDomainFunctionAiCoderStep> steps) {
        this.steps = steps;
    }
}
