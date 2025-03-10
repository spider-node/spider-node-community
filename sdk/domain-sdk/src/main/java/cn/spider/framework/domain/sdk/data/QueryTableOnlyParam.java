package cn.spider.framework.domain.sdk.data;

import java.util.Set;

public class QueryTableOnlyParam {
    private Set<String> tables;

    public QueryTableOnlyParam(Set<String> tables) {
        this.tables = tables;
    }

    public QueryTableOnlyParam() {
    }

    public Set<String> getTables() {
        return tables;
    }

    public void setTables(Set<String> tables) {
        this.tables = tables;
    }
}
