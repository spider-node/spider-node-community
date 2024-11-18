package cn.spider.framework.domain.area.function.data;

import cn.spider.framework.domain.area.function.entity.SpiderBusinessFunction;

import java.util.List;

public class QueryBusinessFunctionResult {
    private List<SpiderBusinessFunction> businessFunctions;

    private Long total;

    public QueryBusinessFunctionResult(List<SpiderBusinessFunction> businessFunctions, Long total) {
        this.businessFunctions = businessFunctions;
        this.total = total;
    }

    public List<SpiderBusinessFunction> getBusinessFunctions() {
        return businessFunctions;
    }

    public void setBusinessFunctions(List<SpiderBusinessFunction> businessFunctions) {
        this.businessFunctions = businessFunctions;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
