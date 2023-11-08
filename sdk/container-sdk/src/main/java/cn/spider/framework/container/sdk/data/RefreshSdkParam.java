package cn.spider.framework.container.sdk.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public class RefreshSdkParam {
    private String sdkName;

    private String classPath;

    private String sdkUrl;

    public String getSdkName() {
        return sdkName;
    }

    public void setSdkName(String sdkName) {
        this.sdkName = sdkName;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getSdkUrl() {
        return sdkUrl;
    }

    public void setSdkUrl(String sdkUrl) {
        this.sdkUrl = sdkUrl;
    }
}
