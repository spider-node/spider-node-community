package cn.spider.framework.domain.sdk.data;

public class SdkInfo {
    private String url;

    private String sdkName;

    private String sdkScanPatch;

    public SdkInfo(String url, String sdkName, String sdkScanPatch) {
        this.url = url;
        this.sdkName = sdkName;
        this.sdkScanPatch = sdkScanPatch;
    }

    public SdkInfo() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSdkName() {
        return sdkName;
    }

    public void setSdkName(String sdkName) {
        this.sdkName = sdkName;
    }

    public String getSdkScanPatch() {
        return sdkScanPatch;
    }

    public void setSdkScanPatch(String sdkScanPatch) {
        this.sdkScanPatch = sdkScanPatch;
    }
}
