package cn.spider.framework.domain.sdk.data;

import java.util.List;
import java.util.Map;

public class NotifyAnalysisResultModel {
    private String sourceFiled;

    private List<Map<String,String>> fields;

    private String type;

    private String table;

    public String getSourceFiled() {
        return sourceFiled;
    }

    public void setSourceFiled(String sourceFiled) {
        this.sourceFiled = sourceFiled;
    }


    public List<Map<String, String>> getFields() {
        return fields;
    }

    public void setFields(List<Map<String, String>> fields) {
        this.fields = fields;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
