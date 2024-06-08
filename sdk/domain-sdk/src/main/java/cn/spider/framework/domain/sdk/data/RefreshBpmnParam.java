package cn.spider.framework.domain.sdk.data;


import cn.spider.framework.domain.sdk.data.enums.UploadBpmnStatus;

public class RefreshBpmnParam {
    /**
     * 领域id
     */
    private String functionVersionId;

    private UploadBpmnStatus status;

    public UploadBpmnStatus getStatus() {
        return status;
    }

    public void setStatus(UploadBpmnStatus status) {
        this.status = status;
    }

    public String getFunctionVersionId() {
        return functionVersionId;
    }

    public void setFunctionVersionId(String functionVersionId) {
        this.functionVersionId = functionVersionId;
    }
}
