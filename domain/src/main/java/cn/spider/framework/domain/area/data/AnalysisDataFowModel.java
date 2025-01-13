package cn.spider.framework.domain.area.data;

import com.alibaba.fastjson.JSONObject;
import io.vertx.core.json.JsonObject;
import java.util.List;

public class AnalysisDataFowModel {
    private Integer flowDataId;
    private List<JsonObject> sonDomainInfo;

    private JSONObject flowData;

    private String dataFlowDesc;

    public AnalysisDataFowModel(Integer flowDataId, List<JsonObject> sonDomainInfo, JSONObject flowData) {
        this.flowDataId = flowDataId;
        this.sonDomainInfo = sonDomainInfo;
        this.flowData = flowData;
    }

    public Integer getFlowDataId() {
        return flowDataId;
    }

    public void setFlowDataId(Integer flowDataId) {
        this.flowDataId = flowDataId;
    }

    public List<JsonObject> getSonDomainInfo() {
        return sonDomainInfo;
    }

    public void setSonDomainInfo(List<JsonObject> sonDomainInfo) {
        this.sonDomainInfo = sonDomainInfo;
    }

    public JSONObject getFlowData() {
        return flowData;
    }

    public void setFlowData(JSONObject flowData) {
        this.flowData = flowData;
    }

    public String getDataFlowDesc() {
        return dataFlowDesc;
    }

    public void setDataFlowDesc(String dataFlowDesc) {
        this.dataFlowDesc = dataFlowDesc;
    }
}
