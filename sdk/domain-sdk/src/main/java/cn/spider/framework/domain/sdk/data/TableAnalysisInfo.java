package cn.spider.framework.domain.sdk.data;

import java.io.Serializable;
import java.util.List;

public class TableAnalysisInfo implements Serializable {
    private String table;

    private List<FiledInfo> data_analysis;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<FiledInfo> getData_analysis() {
        return data_analysis;
    }

    public void setData_analysis(List<FiledInfo> data_analysis) {
        this.data_analysis = data_analysis;
    }
}

