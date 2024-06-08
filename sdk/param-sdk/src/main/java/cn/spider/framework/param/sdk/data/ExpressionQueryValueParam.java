package cn.spider.framework.param.sdk.data;
import java.util.List;

public class ExpressionQueryValueParam {
    /**
     * 表达式名称
     */
    private String targetName;

    /**
     * 请求ID
     */
    private String requestId;

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
