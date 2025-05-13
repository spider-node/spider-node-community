package cn.spider.framework.domain.area.flowdata.data;

import cn.spider.framework.domain.area.domain.entity.SpiderDomainObject;
import lombok.Data;

import java.util.List;

/*** @ClassName QueryDomainObjectResult
 * @Description 查询object的结果
 * @Author dds
 * @Date 2025/4/22 23:01
 */
@Data
public class QueryDomainObjectResult {
    private Long total;

    private List<SpiderDomainObject> domainObject;

    public QueryDomainObjectResult(Long total, List<SpiderDomainObject> domainObject) {
        this.total = total;
        this.domainObject = domainObject;
    }

    public QueryDomainObjectResult() {
    }

}
