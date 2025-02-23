package cn.spider.node.host.plugin.center.sdk.data;

public class QueryFunctionVersionResult {
    /**
     * 业务方法的入参
     */
    private String areaFunctionParamClass;

    /**
     * 业务方法的出参
     */
    private String areaFunctionResultClass;

    public String getAreaFunctionParamClass() {
        return areaFunctionParamClass;
    }

    public void setAreaFunctionParamClass(String areaFunctionParamClass) {
        this.areaFunctionParamClass = areaFunctionParamClass;
    }

    public String getAreaFunctionResultClass() {
        return areaFunctionResultClass;
    }

    public void setAreaFunctionResultClass(String areaFunctionResultClass) {
        this.areaFunctionResultClass = areaFunctionResultClass;
    }
}
