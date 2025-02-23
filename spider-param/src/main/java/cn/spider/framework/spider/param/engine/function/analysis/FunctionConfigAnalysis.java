package cn.spider.framework.spider.param.engine.function.analysis;

import java.util.List;
import java.util.Map;

public class FunctionConfigAnalysis {
    private List<Map<String,String>> params;

    private String scriptsFunctionName;

    public List<Map<String, String>> getParams() {
        return params;
    }

    public void setParams(List<Map<String, String>> params) {
        this.params = params;
    }

    public String getScriptsFunctionName() {
        return scriptsFunctionName;
    }

    public void setScriptsFunctionName(String scriptsFunctionName) {
        this.scriptsFunctionName = scriptsFunctionName;
    }
}
