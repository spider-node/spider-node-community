package cn.spider.framework.domain.area.flowdata.data;

import cn.spider.framework.domain.area.flowdata.entity.SpiderDataFlow;

import java.util.List;

public class QueryFlowDataResult {
    private List<SpiderDataFlow> dataFlowList;

    private Long total;

    public QueryFlowDataResult(List<SpiderDataFlow> dataFlowList, Long total) {
        this.dataFlowList = dataFlowList;
        this.total = total;
    }

    public List<SpiderDataFlow> getDataFlowList() {
        return dataFlowList;
    }

    public void setDataFlowList(List<SpiderDataFlow> dataFlowList) {
        this.dataFlowList = dataFlowList;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
