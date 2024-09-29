package cn.spider.framework.domain.area.sondomain.entity;

import java.util.List;

public class QuerySonAreaBaseResult {
    private List<QuerySonAreaBaseModel> sonAreaBaseInfos;

    public QuerySonAreaBaseResult(List<QuerySonAreaBaseModel> sonAreaBaseInfos) {
        this.sonAreaBaseInfos = sonAreaBaseInfos;
    }

    public List<QuerySonAreaBaseModel> getSonAreaBaseInfos() {
        return sonAreaBaseInfos;
    }

    public void setSonAreaBaseInfos(List<QuerySonAreaBaseModel> sonAreaBaseInfos) {
        this.sonAreaBaseInfos = sonAreaBaseInfos;
    }
}
