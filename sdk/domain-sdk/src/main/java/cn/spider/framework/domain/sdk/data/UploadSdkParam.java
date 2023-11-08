package cn.spider.framework.domain.sdk.data;

/**
 * 上传sdk的参数
 */

public class UploadSdkParam {
    /**
     * 领域id
     */
    private String areaId;

    /**
     * sdk-url
     */
    private String sdkUrl;

    /**
     * 扫描刷新的路径
     */
    private String scanClassPath;

    /**
     * sdkName
     */
    private String sdkName;

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getSdkUrl() {
        return sdkUrl;
    }

    public void setSdkUrl(String sdkUrl) {
        this.sdkUrl = sdkUrl;
    }

    public String getScanClassPath() {
        return scanClassPath;
    }

    public void setScanClassPath(String scanClassPath) {
        this.scanClassPath = scanClassPath;
    }

    public String getSdkName() {
        return sdkName;
    }

    public void setSdkName(String sdkName) {
        this.sdkName = sdkName;
    }
}
