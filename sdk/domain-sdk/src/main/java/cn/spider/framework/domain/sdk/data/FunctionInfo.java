package cn.spider.framework.domain.sdk.data;

public class FunctionInfo {
    /**
     * 版本号
     */
    private String version;

    /**
     * 返回的字段信息
     */
    private ParamPack resultMapping;

    /**
     * 执行参数
     */
    private ParamPack runMapping;

    /**
     * 方法名
     */
    private String taskMethod;

    /**
     * 工作服务的名称
     */
    private String workerId;

    public FunctionInfo(String version, ParamPack resultMapping, ParamPack runMapping, String taskMethod, String workerId) {
        this.version = version;
        this.resultMapping = resultMapping;
        this.runMapping = runMapping;
        this.taskMethod = taskMethod;
        this.workerId = workerId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ParamPack getResultMapping() {
        return resultMapping;
    }

    public void setResultMapping(ParamPack resultMapping) {
        this.resultMapping = resultMapping;
    }

    public ParamPack getRunMapping() {
        return runMapping;
    }

    public void setRunMapping(ParamPack runMapping) {
        this.runMapping = runMapping;
    }

    public String getTaskMethod() {
        return taskMethod;
    }

    public void setTaskMethod(String taskMethod) {
        this.taskMethod = taskMethod;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }
}
