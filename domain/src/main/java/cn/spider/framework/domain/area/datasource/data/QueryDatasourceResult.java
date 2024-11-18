package cn.spider.framework.domain.area.datasource.data;

import cn.spider.framework.domain.area.datasource.entity.AreaDatasourceInfo;

import java.util.List;

public class QueryDatasourceResult {
    private List<AreaDatasourceInfo> datasourceInfos;

    private Long total;

    public QueryDatasourceResult(List<AreaDatasourceInfo> datasourceInfos,Long total) {
        this.datasourceInfos = datasourceInfos;
        this.total = total;
    }

    public List<AreaDatasourceInfo> getDatasourceInfos() {
        return datasourceInfos;
    }

    public void setDatasourceInfos(List<AreaDatasourceInfo> datasourceInfos) {
        this.datasourceInfos = datasourceInfos;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
