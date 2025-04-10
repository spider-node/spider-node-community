package cn.spider.node.host.plugin.center.sdk.data;

public class PluginOfflineParam {
    private String areaVersionId;

    public PluginOfflineParam(String areaVersionId) {
        this.areaVersionId = areaVersionId;
    }

    public PluginOfflineParam() {
    }

    public String getAreaVersionId() {
        return areaVersionId;
    }

    public void setAreaVersionId(String areaVersionId) {
        this.areaVersionId = areaVersionId;
    }
}
