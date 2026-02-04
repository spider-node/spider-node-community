package cn.spider.framework.param.sdk.data;
import io.vertx.core.json.JsonObject;
import java.util.Set;

public class TestJsRuntimeModel {

    /**
     * js 函数名称
     */
    private String jsFunctionName;

    /**
     * js 函数代码
     */
    private String jsFunctionCode;

    /**
     * js 函数参数
     */
    private Set<String> jsFunctionParam;

    /**
     * js 函数参数真实值
     */
    private Set<String> jsFunctionParamReal;

    /**
     * 节点id
     */
    private String nodeId;

    /**
     * mock 执行的数据
     */
    private Object mockData;

    public TestJsRuntimeModel(String jsFunctionName, String jsFunctionCode, Set<String>  jsFunctionParam, Set<String>  jsFunctionParamReal, Object mockData, String nodeId) {
        this.jsFunctionName = jsFunctionName;
        this.jsFunctionCode = jsFunctionCode;
        this.jsFunctionParam = jsFunctionParam;
        this.jsFunctionParamReal = jsFunctionParamReal;

        this.mockData = mockData;
        this.nodeId = nodeId;
    }

    public TestJsRuntimeModel() {
    }

    public String getJsFunctionName() {
        return jsFunctionName;
    }

    public void setJsFunctionName(String jsFunctionName) {
        this.jsFunctionName = jsFunctionName;
    }

    public String getJsFunctionCode() {
        return jsFunctionCode;
    }

    public void setJsFunctionCode(String jsFunctionCode) {
        this.jsFunctionCode = jsFunctionCode;
    }

    public Set<String> getJsFunctionParam() {
        return jsFunctionParam;
    }

    public void setJsFunctionParam(Set<String> jsFunctionParam) {
        this.jsFunctionParam = jsFunctionParam;
    }

    public Set<String> getJsFunctionParamReal() {
        return jsFunctionParamReal;
    }

    public void setJsFunctionParamReal(Set<String> jsFunctionParamReal) {
        this.jsFunctionParamReal = jsFunctionParamReal;
    }

    public Object getMockData() {
        return mockData;
    }

    public JsonObject mockDataJson() {

        return JsonObject.mapFrom(this.mockData);
    }

    public void setMockData(Object mockData) {
        this.mockData = mockData;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
