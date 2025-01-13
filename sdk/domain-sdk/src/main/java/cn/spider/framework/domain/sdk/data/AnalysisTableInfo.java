package cn.spider.framework.domain.sdk.data;

import io.vertx.core.json.JsonArray;

import java.util.List;

public class AnalysisTableInfo {
    private String domainFunctionVersionId;

    private List<TableAnalysisInfo> tableInfo;

    public String getDomainFunctionVersionId() {
        return domainFunctionVersionId;
    }

    public void setDomainFunctionVersionId(String domainFunctionVersionId) {
        this.domainFunctionVersionId = domainFunctionVersionId;
    }

    public List<TableAnalysisInfo> getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(List<TableAnalysisInfo> tableInfo) {
        this.tableInfo = tableInfo;
    }
}
