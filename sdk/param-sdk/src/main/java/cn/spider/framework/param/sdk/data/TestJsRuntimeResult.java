package cn.spider.framework.param.sdk.data;

import java.util.List;

public class TestJsRuntimeResult {
    private List<TestJsRuntimeResultModel> testJsRuntimeResultModelList;

    public TestJsRuntimeResult() {
    }

    public TestJsRuntimeResult(List<TestJsRuntimeResultModel> testJsRuntimeResultModelList) {
        this.testJsRuntimeResultModelList = testJsRuntimeResultModelList;
    }

    public List<TestJsRuntimeResultModel> getTestJsRuntimeResultModelList() {
        return testJsRuntimeResultModelList;
    }

    public void setTestJsRuntimeResultModelList(List<TestJsRuntimeResultModel> testJsRuntimeResultModelList) {
        this.testJsRuntimeResultModelList = testJsRuntimeResultModelList;
    }
}
