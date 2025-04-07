package cn.spider.framework.domain.sdk.data;

import io.vertx.core.json.JsonObject;

import java.util.Set;

public class NodeParamConfig {

    /**
     * bpmn的节点id
     */
    private String nodeId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 入参的js函数名称
     */
    private String jsFunctionalName;

    /**
     * 入参的js函数参数列表(context,request)
     */
    private Set<String> jsFunctionParams;

    /**
     * 实际需要的入参
     */
    private Set<String> realRequiredNodeParameters;

    /**
     * 模拟数据
     */
    private Object mockData;

    private String jsFunction;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getJsFunctionalName() {
        return jsFunctionalName;
    }

    public void setJsFunctionalName(String jsFunctionalName) {
        this.jsFunctionalName = jsFunctionalName;
    }

    public Set<String> getJsFunctionParams() {
        return jsFunctionParams;
    }

    public void setJsFunctionParams(Set<String> jsFunctionParams) {
        this.jsFunctionParams = jsFunctionParams;
    }

    public Set<String> getRealRequiredNodeParameters() {
        return realRequiredNodeParameters;
    }

    public void setRealRequiredNodeParameters(Set<String> realRequiredNodeParameters) {
        this.realRequiredNodeParameters = realRequiredNodeParameters;
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

    public String getJsFunction() {
        return jsFunction;
    }

    public void setJsFunction(String jsFunction) {
        this.jsFunction = jsFunction;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
}
