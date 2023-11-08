package cn.spider.framework.container.sdk.data;

import cn.spider.framework.common.data.enums.BpmnStatus;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.container.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-08  16:26
 * @Description: TODO
 * @Version: 1.0
 */
public class DestroyBpmn {

    private String id;

    private BpmnStatus status;
    private String bpmnName;

    private String url;

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

    public BpmnStatus getStatus() {
        return status;
    }

    public void setStatus(BpmnStatus status) {
        this.status = status;
    }

    public String getBpmnName() {
        return bpmnName;
    }

    public void setBpmnName(String bpmnName) {
        this.bpmnName = bpmnName;
    }
}
