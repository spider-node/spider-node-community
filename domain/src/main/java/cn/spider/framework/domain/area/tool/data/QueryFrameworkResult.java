package cn.spider.framework.domain.area.tool.data;

import cn.spider.framework.domain.area.tool.entity.SpiderToolFramework;

import java.util.List;

public class QueryFrameworkResult {
    private Long total;

    private List<SpiderToolFramework> datas;

    public QueryFrameworkResult(Long total, List<SpiderToolFramework> datas) {
        this.total = total;
        this.datas = datas;
    }

    public QueryFrameworkResult() {
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<SpiderToolFramework> getDatas() {
        return datas;
    }

    public void setDatas(List<SpiderToolFramework> datas) {
        this.datas = datas;
    }
}
