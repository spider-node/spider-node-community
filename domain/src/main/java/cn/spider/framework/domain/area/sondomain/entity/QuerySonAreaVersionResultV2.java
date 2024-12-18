package cn.spider.framework.domain.area.sondomain.entity;

import java.util.List;

public class QuerySonAreaVersionResultV2 {
    private List<AreaDomainBaseInfoModel> sonAreaBaseInfos;

    public QuerySonAreaVersionResultV2(List<AreaDomainBaseInfoModel> infoModelList) {
        this.sonAreaBaseInfos = infoModelList;
    }

    public List<AreaDomainBaseInfoModel> getSonAreaBaseInfos() {
        return sonAreaBaseInfos;
    }

    public void setSonAreaBaseInfos(List<AreaDomainBaseInfoModel> sonAreaBaseInfos) {
        this.sonAreaBaseInfos = sonAreaBaseInfos;
    }
}
