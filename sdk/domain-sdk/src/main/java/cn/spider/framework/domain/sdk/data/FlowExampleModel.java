package cn.spider.framework.domain.sdk.data;

import java.util.List;

public class FlowExampleModel {
    /**
     * 请求的requestId-当作id存储
     */
    private String id;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 功能id
     */
    private String functionId;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 节点信息
     */
    private List<FlowElementModel> flowElementModelList;

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(String requestParam) {
        this.requestParam = requestParam;
    }

    public List<FlowElementModel> getFlowElementModelList() {
        return flowElementModelList;
    }

    public void setFlowElementModelList(List<FlowElementModel> flowElementModelList) {
        this.flowElementModelList = flowElementModelList;
    }
}
