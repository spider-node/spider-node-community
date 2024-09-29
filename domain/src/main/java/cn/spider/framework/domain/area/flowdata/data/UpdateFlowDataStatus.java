package cn.spider.framework.domain.area.flowdata.data;

public class UpdateFlowDataStatus {

    /**
     * 状态
     */
    private String status;

    /**
     * 数据流id
     */
    private Long id;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
