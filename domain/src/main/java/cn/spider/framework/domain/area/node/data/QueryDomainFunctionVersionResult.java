package cn.spider.framework.domain.area.node.data;
import cn.spider.framework.domain.area.node.entity.SpiderAreaFunctionVersion;
import java.util.List;

public class QueryDomainFunctionVersionResult {
    private List<SpiderAreaFunctionVersion> versionList;

    private Long total;

    public QueryDomainFunctionVersionResult(List<SpiderAreaFunctionVersion> versionList, Long total) {
        this.versionList = versionList;
        this.total = total;
    }

    public List<SpiderAreaFunctionVersion> getVersionList() {
        return versionList;
    }

    public void setVersionList(List<SpiderAreaFunctionVersion> versionList) {
        this.versionList = versionList;
    }
}
