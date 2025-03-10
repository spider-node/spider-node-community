package cn.spider.framework.domain.sdk.data;

import java.util.Map;

public class QueryTableOnlyResult {
    private Map<String,String> tableOnlyMap;

    public QueryTableOnlyResult(Map<String, String> tableOnlyMap) {
        this.tableOnlyMap = tableOnlyMap;
    }

    public QueryTableOnlyResult() {
    }

    public Map<String, String> getTableOnlyMap() {
        return tableOnlyMap;
    }

    public void setTableOnlyMap(Map<String, String> tableOnlyMap) {
        this.tableOnlyMap = tableOnlyMap;
    }
}
