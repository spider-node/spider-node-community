package cn.spider.framework.domain.area.datasource.data;

import cn.spider.framework.domain.area.datasource.entity.AreaDatasourceInfo;

import java.util.List;

public class QueryDatasourceResult {
    private List<AreaDatasourceInfo> datasourceInfos;

    public QueryDatasourceResult(List<AreaDatasourceInfo> datasourceInfos) {
        this.datasourceInfos = datasourceInfos;
    }

    public List<AreaDatasourceInfo> getDatasourceInfos() {
        return datasourceInfos;
    }

    public void setDatasourceInfos(List<AreaDatasourceInfo> datasourceInfos) {
        this.datasourceInfos = datasourceInfos;
    }
}
