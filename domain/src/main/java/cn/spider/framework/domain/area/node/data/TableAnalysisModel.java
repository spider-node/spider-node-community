package cn.spider.framework.domain.area.node.data;

import cn.spider.framework.domain.sdk.data.TableAnalysisInfo;

import java.io.Serializable;
import java.util.List;

public class TableAnalysisModel implements Serializable {
    private List<TableAnalysisInfo> tableInfo;

    public List<TableAnalysisInfo> getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(List<TableAnalysisInfo> tableInfo) {
        this.tableInfo = tableInfo;
    }
}
