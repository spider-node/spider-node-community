package cn.spider.framework.spider.param.result;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TableFiledAnalysis {

    /**
     * 每个table在该次解析中，会返回的值
     */
    private Map<String, Set<String>> tables;

    /**
     * 解析结果
     */
    private List<AnalysisInfo> analysis;

    public Map<String, Set<String>> getTables() {
        return tables;
    }

    public void setTables(Map<String, Set<String>> tables) {
        this.tables = tables;
    }

    public List<AnalysisInfo> getAnalysis() {
        return analysis;
    }

    public void setAnalysis(List<AnalysisInfo> analysis) {
        this.analysis = analysis;
    }
}
