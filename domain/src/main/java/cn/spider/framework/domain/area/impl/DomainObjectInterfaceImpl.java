package cn.spider.framework.domain.area.impl;

import cn.spider.framework.domain.area.domain.entity.SpiderDomainObject;
import cn.spider.framework.domain.area.domain.service.ISpiderDomainObjectService;
import cn.spider.framework.domain.area.flowdata.data.QueryDomainObjectParam;
import cn.spider.framework.domain.area.flowdata.data.QueryDomainObjectResult;
import cn.spider.framework.domain.sdk.interfaces.DomainObjectInterface;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/*** @ClassName DomainObjectInterfaceImpl
 * @Description TODO
 * @Author dds
 * @Date 2025/4/22 22:49
 */
public class DomainObjectInterfaceImpl implements DomainObjectInterface {

    private ISpiderDomainObjectService spiderDomainObjectService;

    public DomainObjectInterfaceImpl(ISpiderDomainObjectService spiderDomainObjectService) {
        this.spiderDomainObjectService = spiderDomainObjectService;
    }

    @Override
    public Future<JsonObject> queryDomainObject(JsonObject param) {
        QueryDomainObjectParam queryDomainObjectParam = param.mapTo(QueryDomainObjectParam.class);
        QueryDomainObjectResult result = spiderDomainObjectService.queryDomainObjectParam(queryDomainObjectParam);
        return Future.succeededFuture(JsonObject.mapFrom(result));
    }

    @Override
    public Future<Void> upsertDomainObject(JsonObject param) {
        SpiderDomainObject spiderDomainObject = param.mapTo(SpiderDomainObject.class);
        spiderDomainObjectService.saveOrUpdate(spiderDomainObject);
        return Future.succeededFuture();
    }
}
