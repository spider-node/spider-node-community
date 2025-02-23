package cn.spider.framework.param.result.build.enventData;
import cn.spider.framework.common.event.data.EventData;
import cn.spider.framework.param.result.build.model.ReportParamInfo;

public class EscalationData extends EventData {
    /**
     * ip
     */
    private String ip;

    /**
     * 上报的领域信息
     */
    private ReportParamInfo refreshAreaParam;
    /**
     * 上报类型
     */
    private String functionEscalationType;

    /**
     * 节点类型
     */
    private String workerType;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getFunctionEscalationType() {
        return functionEscalationType;
    }

    public void setFunctionEscalationType(String functionEscalationType) {
        this.functionEscalationType = functionEscalationType;
    }

    public ReportParamInfo getRefreshAreaParam() {
        return refreshAreaParam;
    }

    public void setRefreshAreaParam(ReportParamInfo refreshAreaParam) {
        this.refreshAreaParam = refreshAreaParam;
    }

    public String getWorkerType() {
        return workerType;
    }

    public void setWorkerType(String workerType) {
        this.workerType = workerType;
    }
}
