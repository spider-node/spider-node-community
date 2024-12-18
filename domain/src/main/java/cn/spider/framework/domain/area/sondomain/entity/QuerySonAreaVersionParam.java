package cn.spider.framework.domain.area.sondomain.entity;

import java.util.Set;

public class QuerySonAreaVersionParam {
    private Integer sonAreaId;

    private Set<Integer> sonAreaIds;

    private Set<Integer> ids;

    public Integer getSonAreaId() {
        return sonAreaId;
    }

    public void setSonAreaId(Integer sonAreaId) {
        this.sonAreaId = sonAreaId;
    }

    public Set<Integer> getSonAreaIds() {
        return sonAreaIds;
    }

    public void setSonAreaIds(Set<Integer> sonAreaIds) {
        this.sonAreaIds = sonAreaIds;
    }

    public Set<Integer> getIds() {
        return ids;
    }

    public void setIds(Set<Integer> ids) {
        this.ids = ids;
    }
}
