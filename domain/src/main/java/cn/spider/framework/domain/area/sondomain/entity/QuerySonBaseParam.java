package cn.spider.framework.domain.area.sondomain.entity;

import java.util.List;
import java.util.Objects;

public class QuerySonBaseParam {
    private List<Long> sonIds;

    private List<Long> sonBaseIds;
    private Long page;

    private Long size;

    public Long getPage() {
        return Objects.isNull(this.page) ? 1 :  page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getSize() {
        return Objects.isNull(this.size) ? 10 : size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public List<Long> getSonIds() {
        return sonIds;
    }

    public void setSonIds(List<Long> sonIds) {
        this.sonIds = sonIds;
    }

    public List<Long> getSonBaseIds() {
        return sonBaseIds;
    }

    public void setSonBaseIds(List<Long> sonBaseIds) {
        this.sonBaseIds = sonBaseIds;
    }
}
