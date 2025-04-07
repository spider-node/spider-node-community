package cn.spider.framework.param.sdk.data;

import java.util.List;

public class TestJsRuntimeParam {
    private List<TestJsRuntimeModel> testJsRuntimeModelList;

    public TestJsRuntimeParam(List<TestJsRuntimeModel> testJsRuntimeModelList) {
        this.testJsRuntimeModelList = testJsRuntimeModelList;
    }

    public TestJsRuntimeParam() {
    }
    public List<TestJsRuntimeModel> getTestJsRuntimeModelList() {
        return testJsRuntimeModelList;
    }
    public void setTestJsRuntimeModelList(List<TestJsRuntimeModel> testJsRuntimeModelList) {
        this.testJsRuntimeModelList = testJsRuntimeModelList;
    }
}
