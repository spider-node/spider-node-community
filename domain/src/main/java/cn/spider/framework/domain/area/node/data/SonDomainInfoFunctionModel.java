package cn.spider.framework.domain.area.node.data;

public class SonDomainInfoFunctionModel {
    /**
     * 子域id
     */
    private Integer sonDomainId;

    /**
     * 子域名称
     */
    private String sonDomainName;

    /**
     * 子域版本
     */
    private String sonDomainVersion;

    /**
     * 表名称
     */
    private String tableName;

    private Integer versionId;

    public Integer getSonDomainId() {
        return sonDomainId;
    }

    public void setSonDomainId(Integer sonDomainId) {
        this.sonDomainId = sonDomainId;
    }

    public String getSonDomainName() {
        return sonDomainName;
    }

    public void setSonDomainName(String sonDomainName) {
        this.sonDomainName = sonDomainName;
    }

    public String getSonDomainVersion() {
        return sonDomainVersion;
    }

    public void setSonDomainVersion(String sonDomainVersion) {
        this.sonDomainVersion = sonDomainVersion;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }
}
