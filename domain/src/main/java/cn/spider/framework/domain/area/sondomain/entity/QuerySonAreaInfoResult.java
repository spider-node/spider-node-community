package cn.spider.framework.domain.area.sondomain.entity;

import java.util.List;

public class QuerySonAreaInfoResult {

    /**
     * 子域列表
     */
    private List<SpiderSonArea> spiderSonAreas;

    /**
     * 总数量
     */
    private Long total;

    public QuerySonAreaInfoResult(List<SpiderSonArea> spiderSonAreas, Long total) {
        this.spiderSonAreas = spiderSonAreas;
        this.total = total;
    }

    public List<SpiderSonArea> getSpiderSonAreas() {
        return spiderSonAreas;
    }

    public void setSpiderSonAreas(List<SpiderSonArea> spiderSonAreas) {
        this.spiderSonAreas = spiderSonAreas;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
