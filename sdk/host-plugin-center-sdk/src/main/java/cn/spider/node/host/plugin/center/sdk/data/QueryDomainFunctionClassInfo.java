package cn.spider.node.host.plugin.center.sdk.data;

public class QueryDomainFunctionClassInfo {
    private String domainFunctionId;

    private String returnsClass;

    private String parametersClass;

    private String otherClass;

    public QueryDomainFunctionClassInfo(String domainFunctionId, String returnsClass, String parametersClass,String otherClass) {
        this.domainFunctionId = domainFunctionId;
        this.returnsClass = returnsClass;
        this.parametersClass = parametersClass;
        this.otherClass = otherClass;
    }

    public QueryDomainFunctionClassInfo() {
    }

    public String getDomainFunctionId() {
        return domainFunctionId;
    }

    public void setDomainFunctionId(String domainFunctionId) {
        this.domainFunctionId = domainFunctionId;
    }

    public String getReturnsClass() {
        return returnsClass;
    }

    public void setReturnsClass(String returnsClass) {
        this.returnsClass = returnsClass;
    }

    public String getParametersClass() {
        return parametersClass;
    }

    public void setParametersClass(String parametersClass) {
        this.parametersClass = parametersClass;
    }

    public String getOtherClass() {
        return otherClass;
    }

    public void setOtherClass(String otherClass) {
        this.otherClass = otherClass;
    }
}
