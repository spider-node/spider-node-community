package cn.spider.framework.common.event.data;

import cn.spider.framework.common.data.enums.RefreshAreaParam;
import io.vertx.core.json.JsonObject;

public class EscalationData extends EventData {
    /**
     * ip
     */
    private String ip;

    /**
     * 上报的领域信息
     */
    private JsonObject refreshAreaParam;

    /**
     * 上报类型
     */
    private String functionEscalationType;

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

    public JsonObject getRefreshAreaParam() {
        return refreshAreaParam;
    }

    public void setRefreshAreaParam(JsonObject refreshAreaParam) {
        this.refreshAreaParam = refreshAreaParam;
    }
}
