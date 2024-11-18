package cn.spider.framework.domain.area.node.data;

import cn.spider.framework.domain.area.node.entity.SpiderAreaFunction;

import java.util.List;

public class QueryDomainFunctionResult {
    private List<SpiderAreaFunction> spiderAreaFunctionList;

    private Long total;

    public QueryDomainFunctionResult(List<SpiderAreaFunction> spiderAreaFunctionList, Long total) {
        this.spiderAreaFunctionList = spiderAreaFunctionList;
        this.total = total;
    }

    public List<SpiderAreaFunction> getSpiderAreaFunctionList() {
        return spiderAreaFunctionList;
    }

    public void setSpiderAreaFunctionList(List<SpiderAreaFunction> spiderAreaFunctionList) {
        this.spiderAreaFunctionList = spiderAreaFunctionList;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
