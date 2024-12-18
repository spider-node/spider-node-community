package cn.spider.framework.domain.area.node.data;

import java.util.Objects;

public class QueryDomainFunctionVersionParam {
    private String sonDomainVersion;

    private String domainFunctionId;

    private String domainFunctionVersionId;

    private String domainFunctionName;

    private Integer page;

    private Integer size;

    public void setDomainFunctionVersionId(String domainFunctionVersionId) {
        this.domainFunctionVersionId = domainFunctionVersionId;
    }

    public String getDomainFunctionName() {
        return domainFunctionName;
    }

    public void setDomainFunctionName(String domainFunctionName) {
        this.domainFunctionName = domainFunctionName;
    }

    public String getSonDomainVersion() {
        return sonDomainVersion;
    }

    public void setSonDomainVersion(String sonDomainVersion) {
        this.sonDomainVersion = sonDomainVersion;
    }

    public String getDomainFunctionId() {
        return domainFunctionId;
    }

    public void setDomainFunctionId(String domainFunctionId) {
        this.domainFunctionId = domainFunctionId;
    }

    public String getDomainFunctionVersionId() {
        return domainFunctionVersionId;
    }

    public Integer getPage() {
        if(Objects.isNull(page)){
            return 1;
        }
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        if(Objects.isNull(size)){
            return 10;
        }
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
