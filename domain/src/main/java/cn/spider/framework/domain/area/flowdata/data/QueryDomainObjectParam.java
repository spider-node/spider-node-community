package cn.spider.framework.domain.area.flowdata.data;

import lombok.Data;

/*** @ClassName QueryDomainObjectParam
 * @Description TODO
 * @Author dds
 * @Date 2025/4/22 22:52
 */
@Data
public class QueryDomainObjectParam {

    private Long page;

    private Long size;

    private String domainObjectName;
}
