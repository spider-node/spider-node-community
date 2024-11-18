package cn.spider.framework.domain.area.node.data;

public class QueryDomainFunctionParam {
    /**
     * 主域名称
     */
    private String areaName;

    /**
     * 领域名称
     */
    private String sonAreaName;

    /**
     * 功能名称
     */
    private String name;


    private Integer page;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
