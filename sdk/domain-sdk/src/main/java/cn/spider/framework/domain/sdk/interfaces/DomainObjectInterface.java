package cn.spider.framework.domain.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/*** @ClassName DomainObjectInterface
 * @Description 领域对象配置接口
 * @Author dds
 * @Date 2025/4/22 22:44
 */
@ProxyGen
@VertxGen
public interface DomainObjectInterface {
    String ADDRESS = "DOMAIN_OBJECT";

    static DomainObjectInterface createProxy(Vertx vertx, String address) {
        return new DomainObjectInterfaceVertxEBProxy(vertx, address);
    }

    // 查询领域对象信息
    Future<JsonObject> queryDomainObject(JsonObject param);

    // 新增修改对象信息
    Future<Void> upsertDomainObject(JsonObject param);
}
