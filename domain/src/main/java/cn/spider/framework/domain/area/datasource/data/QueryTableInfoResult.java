package cn.spider.framework.domain.area.datasource.data;

import cn.spider.framework.domain.area.util.TableInfo;

import java.util.List;

public class QueryTableInfoResult {
    private List<TableInfo> tableInfos;

    public List<TableInfo> getTableInfos() {
        return tableInfos;
    }

    public void setTableInfos(List<TableInfo> tableInfos) {
        this.tableInfos = tableInfos;
    }

    public QueryTableInfoResult(List<TableInfo> tableInfos) {
        this.tableInfos = tableInfos;
    }

    public QueryTableInfoResult() {

    }


}
