package cn.spider.framework.domain.area.data;

import cn.spider.framework.domain.area.domain.entity.SpiderArea;

import java.util.List;

public class QueryDomainResult {
    private List<SpiderArea> spiderAreaList;

    private Long total;

    public QueryDomainResult(List<SpiderArea> spiderAreaList, Long total) {
        this.spiderAreaList = spiderAreaList;
        this.total = total;
    }

    public List<SpiderArea> getSpiderAreaList() {
        return spiderAreaList;
    }

    public void setSpiderAreaList(List<SpiderArea> spiderAreaList) {
        this.spiderAreaList = spiderAreaList;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
