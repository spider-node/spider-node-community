package cn.spider.framework.domain.area.task.data;

import cn.spider.framework.domain.area.task.entity.SpiderTaskTestInfo;

import java.util.List;

public class CaseInfoResult {
    private List<SpiderTaskTestInfo> spiderTaskTestInfos;

    public CaseInfoResult(List<SpiderTaskTestInfo> spiderTaskTestInfos) {
        this.spiderTaskTestInfos = spiderTaskTestInfos;
    }

    public List<SpiderTaskTestInfo> getSpiderTaskTestInfos() {
        return spiderTaskTestInfos;
    }

    public void setSpiderTaskTestInfos(List<SpiderTaskTestInfo> spiderTaskTestInfos) {
        this.spiderTaskTestInfos = spiderTaskTestInfos;
    }
}
