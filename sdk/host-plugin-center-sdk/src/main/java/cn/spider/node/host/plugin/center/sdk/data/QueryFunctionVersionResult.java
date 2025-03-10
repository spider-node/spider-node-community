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

    private String serviceName;

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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
