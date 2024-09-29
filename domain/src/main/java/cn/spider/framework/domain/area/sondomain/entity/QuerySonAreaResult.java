package cn.spider.framework.domain.area.sondomain.entity;

import java.util.List;

public class QuerySonAreaResult {
    private List<QuerySonAreaData> querySonAreaDatas;

    private Long total;

    public QuerySonAreaResult(List<QuerySonAreaData> querySonAreaDatas, Long total) {
        this.querySonAreaDatas = querySonAreaDatas;
        this.total = total;
    }

    public QuerySonAreaResult() {

    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<QuerySonAreaData> getQuerySonAreaDatas() {
        return querySonAreaDatas;
    }

    public void setQuerySonAreaDatas(List<QuerySonAreaData> querySonAreaDatas) {
        this.querySonAreaDatas = querySonAreaDatas;
    }
}
