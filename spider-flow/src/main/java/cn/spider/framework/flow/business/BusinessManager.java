package cn.spider.framework.flow.business;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.domain.sdk.interfaces.FunctionInterface;
import cn.spider.framework.domain.sdk.interfaces.VersionInterface;
import cn.spider.framework.flow.business.data.BusinessFunctions;
import cn.spider.framework.flow.business.data.DerailFunctionVersion;
import cn.spider.framework.flow.business.data.ExecuteFunctionInfo;
import cn.spider.framework.flow.business.data.FunctionWeight;
import cn.spider.framework.flow.business.enums.FunctionStatus;
import cn.spider.framework.flow.business.enums.IsAsync;
import cn.spider.framework.flow.business.enums.IsRetry;
import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.business
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-11  15:42
 * @Description: 业务功能的管理
 * @Version: 1.0
 */
@Slf4j
public class BusinessManager {
    private MySQLPool client;

    private FunctionInterface functionInterface;

    private RowMapper<BusinessFunctions> ROW_BUSINESS = row -> {
        BusinessFunctions businessFunctions = new BusinessFunctions();
        businessFunctions.setId(row.getString("id"));
        businessFunctions.setName(row.getString("name"));
        businessFunctions.setDesc(row.getString("desc"));
        businessFunctions.setBpmnName(row.getString("bpmn_name"));
        businessFunctions.setVersion(row.getString("version"));
        businessFunctions.setStartId(row.getString("start_id"));
        businessFunctions.setIsAsync(IsAsync.valueOf(row.getString("is_async")));
        businessFunctions.setIsRetry(IsRetry.valueOf(row.getString("is_retry")));
        businessFunctions.setRetryCount(row.getInteger("retry_count"));
        businessFunctions.setStatus(FunctionStatus.valueOf(row.getString("status")));
        return businessFunctions;
    };

    private final static Cache<String, BusinessFunctions> cache = CacheBuilder.newBuilder()
            //设置cache的初始大小为10，要合理设置该值
            .initialCapacity(10)
            //设置并发数为10，即同一时间最多只能有10个线程往cache执行写入操作
            .concurrencyLevel(2)
            //设置cache中的数据在写入之后的存活时间为10分钟
            .expireAfterWrite(10, TimeUnit.MINUTES)
            //构建cache实例
            .build();

    public BusinessManager(MySQLPool client,FunctionInterface functionInterface) {
        this.client = client;
        this.functionInterface = functionInterface;
    }


    public void removeCache(String functionId) {
        cache.invalidate(functionId);
    }

    // 注册功能
    public Future<String> registerBusinessFunction(BusinessFunctions businessFunctions) {
        StringBuilder sql = new StringBuilder();

        if (StringUtils.isEmpty(businessFunctions.getId())) {
            //sql = "insert into spider_function (id,name,version,start_id,status,desc,bpmn_name) values (#{id},#{name},#{version},#{startId},#{status},#{desc},#{bpmnName})";
            sql.append("insert into spider_function (id,`name`,version,start_id,`status`,`desc`,bpmn_name) values (#{id},#{name},#{version},#{startId},#{status},#{desc},#{bpmnName})");
            String id = UUID.randomUUID().toString();
            businessFunctions.setId(id);
        } else {
            sql.append("update spider_function set `name` = #{name}, `version` = #{version}, start_id=#{startId},`status`=#{status},`desc`=#{desc},bpmn_name=#{bpmnName} where id = #{id}");
        }
        Promise<String> promise = Promise.promise();

        JsonObject param = JsonObject.mapFrom(businessFunctions);
        Map<String, Object> parameters = param.getMap();
        log.info("sql {} param {}", sql.toString(), JSON.toJSONString(parameters));
        SqlTemplate
                .forUpdate(client, sql.toString())
                .execute(parameters)
                .onSuccess(function -> {
                    log.info("新增功能节点信息成功数据为 {}", param.toString());
                    promise.complete(businessFunctions.getId());
                    // 插入redis
                    cache.put(businessFunctions.getId(), businessFunctions);

                }).onFailure(fail -> {
                    log.error("新增功能数据 {} 失败 {}", param.toString(), ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });

        return promise.future();
    }

    public void deleteAll() {
        cache.cleanUp();
    }

    public Future<List<BusinessFunctions>> queryBusinessFunctions(int page, int size) {
        Promise<List<BusinessFunctions>> promise = Promise.promise();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("page", (page - 1) * size);
        parameters.put("size", size);
        SqlTemplate
                .forQuery(client, "SELECT * FROM spider_function limit #{page},#{size}")
                .mapTo(ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(users -> {
                    RowSet<BusinessFunctions> businessFunctions = users;
                    List<BusinessFunctions> businessFunctionList = Lists.newArrayList();
                    businessFunctions.forEach(item -> {
                        businessFunctionList.add(item);
                    });
                    promise.complete(businessFunctionList);
                }).onFailure(fail -> {
                    log.error("查询数据失败 {}", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });


        return promise.future();
    }

    public Future<Void> deleteFunction(String id) {
        Promise<Void> promise = Promise.promise();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", id);
        SqlTemplate
                .forUpdate(client, "delete from  spider_function where id = #{id}")
                .execute(parameters)
                .onSuccess(function -> {
                    promise.complete();
                    log.info("删除功能的id {}", id);
                }).onFailure(fail -> {
                    log.error("删除功能数据的id {} 失败 {}", id, ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

    public Future<Void> updateStatus(String id, FunctionStatus status) {
        Promise<Void> promise = Promise.promise();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", id);
        parameters.put("status", status);
        SqlTemplate
                .forUpdate(client, "update spider_function set status = #{status} where id = #{id}")
                .execute(parameters)
                .onSuccess(function -> {
                    promise.complete();
                    log.info("删除功能的id {}", id);
                }).onFailure(fail -> {
                    log.error("删除功能数据的id {} 失败 {}", id, ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

    // 配置权重
    public void functionWeightConfig(FunctionWeight weight) {

    }

    /**
     * 开关版本-- 只对后续有效-- 移除权重
     */
    public void derailFunctionVersion(DerailFunctionVersion derailFunctionVersion) {
    }

    /**
     * 获取startId
     *
     * @param functionId
     * @return
     */
    public Future<BusinessFunctions> queryStartIdByFunctionId(String functionId) {
        // 获取缓存信息
        BusinessFunctions functions = cache.getIfPresent(functionId);
        if (Objects.nonNull(functions)) {
            return Future.succeededFuture(functions);
        }
        Promise<BusinessFunctions> promise = Promise.promise();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", functionId);
        // 查询功能版本信息
        SqlTemplate
                .forQuery(client, "SELECT * FROM spider_function where id = #{id}")
                .mapTo(ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(users -> {
                    RowSet<BusinessFunctions> businessFunctions = users;
                    if (businessFunctions.size() == 0) {
                        promise.fail("没有查询到功能信息");
                        return;
                    }
                    List<BusinessFunctions> businessFunctionList = Lists.newArrayList();
                    businessFunctions.forEach(item -> {
                        businessFunctionList.add(item);
                    });
                    if (CollectionUtils.isNotEmpty(businessFunctionList)) {
                        BusinessFunctions function = businessFunctionList.get(0);
                        cache.put(function.getId(), function);
                    }
                    promise.complete(CollectionUtils.isEmpty(businessFunctionList) ? null : businessFunctionList.get(0));
                }).onFailure(fail -> {
                    promise.fail(fail);
                });

        return promise.future();
    }

    /**
     * 从域中获取可执行的-BusinessFunctions 信息
     */
    public Future<BusinessFunctions> queryBusinessFunctions(String functionId) {
        // 该spider-node版本没有支持功能多版本，-需要下个版本的规划
        BusinessFunctions functions = cache.getIfPresent(functionId);
        if (Objects.nonNull(functions)) {
            return Future.succeededFuture(functions);
        }
        Promise<BusinessFunctions> promise = Promise.promise();
        Future<JsonObject> functionObject = functionInterface.findExecuteFunction(new JsonObject().put("functionId", functionId));
        functionObject.onSuccess(suss -> {
            ExecuteFunctionInfo functionInfo = suss.mapTo(ExecuteFunctionInfo.class);
            BusinessFunctions businessFunctions = new BusinessFunctions();
            businessFunctions.setId(functionId);
            businessFunctions.setStartId(functionInfo.getStartId());
            businessFunctions.setName(functionInfo.getFunctionName());
            businessFunctions.setIsAsync(IsAsync.AYNC);
            businessFunctions.setResultMapping(functionInfo.getResultMapping());
            businessFunctions.setRequestClass(functionInfo.getRequestClass());
            cache.put(businessFunctions.getId(), businessFunctions);
            promise.complete(businessFunctions);
        }).onFailure(fail -> {
            promise.fail(fail);
        });
        return promise.future();
    }


}
