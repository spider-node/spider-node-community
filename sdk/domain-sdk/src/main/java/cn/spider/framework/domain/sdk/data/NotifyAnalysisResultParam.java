package cn.spider.framework.domain.sdk.data;

import com.alibaba.fastjson.JSONArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class NotifyAnalysisResultParam {
    private List<NotifyAnalysisResultModel> analysisResult;

    private String functionVersionId;



    public NotifyAnalysisResultParam() {
    }

    public List<NotifyAnalysisResultModel> getAnalysisResult() {
        return analysisResult;
    }

    public void setAnalysisResult(List<NotifyAnalysisResultModel> analysisResult) {
        this.analysisResult = analysisResult;
    }

    public String getFunctionVersionId() {
        return functionVersionId;
    }

    public void setFunctionVersionId(String functionVersionId) {
        this.functionVersionId = functionVersionId;
    }
}
