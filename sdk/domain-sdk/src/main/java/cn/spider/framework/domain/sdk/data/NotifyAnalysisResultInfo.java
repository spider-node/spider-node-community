package cn.spider.framework.domain.sdk.data;

import java.util.List;

public class NotifyAnalysisResultInfo {
    private List<NotifyAnalysisResultModel> analysis;

    public NotifyAnalysisResultInfo(List<NotifyAnalysisResultModel> analysis) {
        this.analysis = analysis;
    }

    public List<NotifyAnalysisResultModel> getAnalysis() {
        return analysis;
    }

    public void setAnalysis(List<NotifyAnalysisResultModel> analysis) {
        this.analysis = analysis;
    }
}
