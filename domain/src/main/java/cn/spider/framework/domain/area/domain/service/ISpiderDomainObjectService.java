package cn.spider.framework.domain.area.domain.service;

import cn.spider.framework.domain.area.domain.entity.SpiderDomainObject;
import cn.spider.framework.domain.area.flowdata.data.QueryDomainObjectParam;
import cn.spider.framework.domain.area.flowdata.data.QueryDomainObjectResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 领域对象 服务类
 * </p>
 *
 * @author dds
 * @since 2025-04-22
 */
public interface ISpiderDomainObjectService extends IService<SpiderDomainObject> {
    QueryDomainObjectResult queryDomainObjectParam(QueryDomainObjectParam param);
}
