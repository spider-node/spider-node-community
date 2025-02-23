package cn.spider.framework.domain.sdk.data;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class QueryAnalysisResult {
    private JsonObject analysisResult;

    public QueryAnalysisResult(JsonObject analysisResult) {
        this.analysisResult = analysisResult;
    }

    public JsonObject getAnalysisResult() {
        return analysisResult;
    }

    public void setAnalysisResult(JsonObject analysisResult) {
        this.analysisResult = analysisResult;
    }
}
