package cn.spider.framework.domain.area.http.data;

import cn.spider.framework.domain.area.http.entity.SpiderToolHttp;
import lombok.Data;

import java.util.List;

@Data
public class QueryHttpResult {
    private Long total;

    private List<SpiderToolHttp> datas;

    public QueryHttpResult(Long total, List<SpiderToolHttp> datas) {
        this.total = total;
        this.datas = datas;
    }
    public QueryHttpResult() {
    }
}
