package cn.spider.framework.domain.area.sondomain.entity;

import java.util.List;

public class QuerySonBaseResult {
    private List<SpiderSonArea> spiderSonAreas;

    public List<SpiderSonArea> getSpiderSonAreas() {
        return spiderSonAreas;
    }

    public void setSpiderSonAreas(List<SpiderSonArea> spiderSonAreas) {
        this.spiderSonAreas = spiderSonAreas;
    }

    public QuerySonBaseResult(List<SpiderSonArea> spiderSonAreas) {
        this.spiderSonAreas = spiderSonAreas;
    }
}
