package cn.spider.framework.container.sdk.data;

import cn.spider.framework.common.data.enums.BpmnStatus;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.container.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-03  22:35
 * @Description: TODO
 * @Version: 1.0
 */
public class QueryBpmnRequest {
    private BpmnStatus status;
    private String bpmnName;

    private Integer page;

    private Integer size;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
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
