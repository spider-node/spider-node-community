package cn.spider.framework.domain.area.sondomain.entity;

import java.util.List;

public class QuerySonAreaData {

    /**
     * 子域的名称
     */
    private String sonAreaName;

    /**
     * 子域中字段的信息
     */
    private List<DomainFieldInfo> domainFieldInfo;

    /**
     * 子域id
     */
    private Integer id;

    public QuerySonAreaData(String sonAreaName, List<DomainFieldInfo> domainFieldInfo, Integer id) {
        this.sonAreaName = sonAreaName;
        this.domainFieldInfo = domainFieldInfo;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSonAreaName() {
        return sonAreaName;
    }

    public void setSonAreaName(String sonAreaName) {
        this.sonAreaName = sonAreaName;
    }

    public List<DomainFieldInfo> getDomainFieldInfo() {
        return domainFieldInfo;
    }

    public void setDomainFieldInfo(List<DomainFieldInfo> domainFieldInfo) {
        this.domainFieldInfo = domainFieldInfo;
    }
}
