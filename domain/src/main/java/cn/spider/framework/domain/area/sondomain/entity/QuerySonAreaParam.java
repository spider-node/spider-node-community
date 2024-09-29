package cn.spider.framework.domain.area.sondomain.entity;

import java.util.List;

public class QuerySonAreaParam {
    /**
     * 主域名称
     */
    private String areaName;

    /**
     * 领域名称
     */
    private String sonAreaName;

    private List<Long> ids;
    /**
     * 页数
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getSonAreaName() {
        return sonAreaName;
    }

    public void setSonAreaName(String sonAreaName) {
        this.sonAreaName = sonAreaName;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
