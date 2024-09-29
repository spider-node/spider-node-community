package cn.spider.framework.domain.area.flowdata.data;

public class QueryFlowDataParam {

    // 全不配搜索
    private String flowDataName;

    // 子域名称
    private String sonAreaName;

    private Integer page;

    private Integer size;

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

    public String getFlowDataName() {
        return flowDataName;
    }

    public void setFlowDataName(String flowDataName) {
        this.flowDataName = flowDataName;
    }

    public String getSonAreaName() {
        return sonAreaName;
    }

    public void setSonAreaName(String sonAreaName) {
        this.sonAreaName = sonAreaName;
    }
}
