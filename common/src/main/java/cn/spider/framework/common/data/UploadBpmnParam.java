package cn.spider.framework.common.data;

import cn.spider.framework.common.data.enums.BpmnStatus;
import lombok.Data;

@Data
public class UploadBpmnParam {
    private String bpmnName;

    private BpmnStatus status;

    private String url;

}
