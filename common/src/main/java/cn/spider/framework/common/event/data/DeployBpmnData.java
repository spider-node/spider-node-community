package cn.spider.framework.common.event.data;

import cn.spider.framework.common.data.enums.BpmnStatus;
import lombok.Builder;
import lombok.Data;

@Data
public class DeployBpmnData extends EventData {
    private String bpmnName;

    private BpmnStatus status;

    private String url;

    private String id;
}
