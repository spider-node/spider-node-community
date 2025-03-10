package cn.spider.framework.domain.area.data;

import java.util.List;
import java.util.Map;

public class AiNodeInfo {
    private Map<String, List<Map<String, String>>> tableFiledMap;

    private String paramClass;

    private String id;

    private String name;

    private String functionVersionId;

    public AiNodeInfo(Map<String, List<Map<String, String>>> tableFiledMap, String paramClass, String id, String name, String functionVersionId) {
        this.tableFiledMap = tableFiledMap;
        this.paramClass = paramClass;
        this.id = id;
        this.name = name;
        this.functionVersionId = functionVersionId;
    }

    public Map<String, List<Map<String, String>>> getTableFiledMap() {
        return tableFiledMap;
    }

    public void setTableFiledMap(Map<String, List<Map<String, String>>> tableFiledMap) {
        this.tableFiledMap = tableFiledMap;
    }

    public String getParamClass() {
        return paramClass;
    }

    public void setParamClass(String paramClass) {
        this.paramClass = paramClass;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFunctionVersionId() {
        return functionVersionId;
    }

    public void setFunctionVersionId(String functionVersionId) {
        this.functionVersionId = functionVersionId;
    }
}
