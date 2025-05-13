package cn.spider.framework.domain.area.data;

import cn.spider.framework.domain.area.flowdata.data.FlowDataDesc;
import com.alibaba.fastjson.JSONObject;
import io.vertx.core.json.JsonObject;
import lombok.Data;

import java.util.List;

@Data
public class AnalysisDataFowModel {
    private Integer flowDataId;
    private List<JsonObject> sonDomainInfo;

    private JSONObject flowData;

    private FlowDataDesc dataFlowDesc;

    public AnalysisDataFowModel(Integer flowDataId, List<JsonObject> sonDomainInfo, JSONObject flowData) {
        this.flowDataId = flowDataId;
        this.sonDomainInfo = sonDomainInfo;
        this.flowData = flowData;
    }
}
