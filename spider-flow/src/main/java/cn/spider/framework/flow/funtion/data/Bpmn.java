package cn.spider.framework.flow.funtion.data;

import cn.spider.framework.common.data.enums.BpmnStatus;
/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.funtion.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-03  20:03
 * @Description: TODO
 * @Version: 1.0
 */
public class Bpmn {
    private String bpmnName;

    private BpmnStatus status;

    private String url;

    private String id;

    public String getBpmnName() {
        return bpmnName;
    }

    public void setBpmnName(String bpmnName) {
        this.bpmnName = bpmnName;
    }

    public BpmnStatus getStatus() {
        return status;
    }

    public void setStatus(BpmnStatus status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
