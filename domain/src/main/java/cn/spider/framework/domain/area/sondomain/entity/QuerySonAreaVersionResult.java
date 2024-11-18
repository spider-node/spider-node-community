package cn.spider.framework.domain.area.sondomain.entity;

import java.util.List;

public class QuerySonAreaVersionResult {
    private List<AreaDomainBaseInfo> sonAreaBaseInfos;

    public QuerySonAreaVersionResult(List<AreaDomainBaseInfo> sonAreaBaseInfos) {
        this.sonAreaBaseInfos = sonAreaBaseInfos;
    }

    public List<AreaDomainBaseInfo> getSonAreaBaseInfos() {
        return sonAreaBaseInfos;
    }

    public void setSonAreaBaseInfos(List<AreaDomainBaseInfo> sonAreaBaseInfos) {
        this.sonAreaBaseInfos = sonAreaBaseInfos;
    }
}
