package cn.spider.framework.domain.area.function.version.data;

import cn.spider.framework.domain.area.function.entity.SpiderBusinessFunctionVersion;

import java.util.List;

public class QueryFunctionVersionResult {
    private List<SpiderBusinessFunctionVersion> versions;

    private Long total;

    public QueryFunctionVersionResult(List<SpiderBusinessFunctionVersion> versions, Long total) {
        this.versions = versions;
        this.total = total;
    }

    public List<SpiderBusinessFunctionVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<SpiderBusinessFunctionVersion> versions) {
        this.versions = versions;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
