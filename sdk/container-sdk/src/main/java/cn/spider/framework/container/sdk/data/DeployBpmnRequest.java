package cn.spider.framework.container.sdk.data;

import cn.spider.framework.common.data.enums.BpmnStatus;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.container.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-26  18:16
 * @Description: TODO
 * @Version: 1.0
 */
public class DeployBpmnRequest {

    private String id;

    private String bpmnName;

    private String url;

    private BpmnStatus status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

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
}
