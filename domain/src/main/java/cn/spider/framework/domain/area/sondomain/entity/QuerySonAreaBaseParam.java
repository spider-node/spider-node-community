package cn.spider.framework.domain.area.sondomain.entity;

public class QuerySonAreaBaseParam {
    private String sonAreaName;

    private Integer sonAreaId;

    private String areaId;

    public String getSonAreaName() {
        return sonAreaName;
    }

    public void setSonAreaName(String sonAreaName) {
        this.sonAreaName = sonAreaName;
    }

    public Integer getSonAreaId() {
        return sonAreaId;
    }

    public void setSonAreaId(Integer sonAreaId) {
        this.sonAreaId = sonAreaId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }
}
