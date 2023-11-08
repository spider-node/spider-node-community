package cn.spider.framework.domain.area.function;

import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.FunctionStartStopEventData;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.domain.area.function.data.*;
import cn.spider.framework.domain.area.function.version.VersionManager;
import cn.spider.framework.domain.area.function.version.data.FunctionVersionModel;
import cn.spider.framework.domain.area.function.version.data.QueryVersionFunctionParam;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.function
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-14  22:00
 * @Description: 功能管理
 * @Version: 1.0
 */
@Slf4j
public class FunctionManger {

    private MySQLPool client;

    private EventManager eventManager;

    private VersionManager versionManager;

    private RowMapper<FunctionModel> ROW_BUSINESS = row -> {
        FunctionModel businessFunctions = new FunctionModel();
        businessFunctions.setId(row.getString("id"));
        businessFunctions.setFunctionName(row.getString("function_name"));
        businessFunctions.setDesc(row.getString("desc"));
        businessFunctions.setDirector(row.getString("director"));
        businessFunctions.setAreaId(row.getString("area_id"));
        businessFunctions.setStatus(FunctionStatus.valueOf(row.getString("status")));
        return businessFunctions;
    };

    public FunctionManger(MySQLPool client, EventManager eventManager,VersionManager versionManager) {
        this.client = client;
        this.eventManager = eventManager;
        this.versionManager = versionManager;
    }

    /**
     * 新增功能
     *
     * @param functionModel
     */
    public Future<Void> increaseFunctionManger(FunctionModel functionModel) {
        Promise<Void> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();
        functionModel.setId(UUID.randomUUID().toString());
        sql.append("insert into spider_business_function (`id`,`function_name`,`desc`,`director`,`status`,`area_id`) values (#{id},#{functionName},#{desc},#{director},#{status},#{areaId})");

        JsonObject param = JsonObject.mapFrom(functionModel);
        Map<String, Object> parameters = param.getMap();
        SqlTemplate
                .forUpdate(client, sql.toString())
                .execute(parameters)
                .onSuccess(function -> {
                    promise.complete();
                }).onFailure(fail -> {
                    promise.fail(fail);
                });
        return promise.future();
    }

    /**
     * 更新功能信息
     *
     * @param functionModel
     * @return
     */
    public Future<Void> updateFunctionManger(FunctionModel functionModel) {
        Promise<Void> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();
        sql.append("update spider_business_function set function_name = #{functionName},`desc` = #{desc},director = #{director}, `status` = #{status}, `area_id` = #{areaId} where id = #{id}");
        JsonObject param = JsonObject.mapFrom(functionModel);
        Map<String, Object> parameters = param.getMap();
        SqlTemplate
                .forUpdate(client, sql.toString())
                .execute(parameters)
                .onSuccess(function -> {
                    promise.complete();
                }).onFailure(fail -> {
                    promise.fail(fail);
                });
        return promise.future();
    }


    /**
     * 功能启停-整体功能开关
     *
     * @param model
     * @return
     */
    public Future<Void> startStopFunction(FunctionStartStopModel model) {
        Promise<Void> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();
        sql.append("update spider_business_function set status = #{status} where id = #{functionId}");
        JsonObject param = JsonObject.mapFrom(model);
        Map<String, Object> parameters = param.getMap();

        SqlTemplate
                .forUpdate(client, sql.toString())
                .execute(parameters)
                .onSuccess(function -> {
                    // 发送启停的事件
                    FunctionStartStopEventData functionStartStopEventData = FunctionStartStopEventData.builder()
                            .functionId(model.getFunctionId())
                            .status(model.getStatus().name())
                            .build();
                    eventManager.sendMessage(EventType.FUNCTION_START_STOP, functionStartStopEventData);
                    promise.complete();
                }).onFailure(fail -> {
                    promise.fail(fail);
                });
        return promise.future();
    }

    /**
     * 查询功能
     *
     * @param param
     * @return
     */
    // 查询功能
    public Future<List<FunctionModel>> selectFunction(QueryFunctionParam param) {

        Promise<List<FunctionModel>> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();
        param.setPage((param.getPage() - 1) * param.getSize());
        JsonObject params = JsonObject.mapFrom(param);
        Map<String, Object> parameters = params.getMap();
        sql.append("select * from spider_business_function where 1=1 ");
        if (StringUtils.isNotEmpty(param.getFunctionName())) {
            sql.append(" and function_name = #{functionName} ");
        }
        if (StringUtils.isNotEmpty(param.getStatus())) {
            sql.append(" and status = #{status} ");
        }
        if (StringUtils.isNotEmpty(param.getAreaId())) {
            sql.append(" and area_id = #{areaId} ");
        }
        sql.append("order by id limit #{page},#{size}");

        SqlTemplate
                .forQuery(client, sql.toString())
                .mapTo(ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(users -> {
                    RowSet<FunctionModel> function = users;
                    List<FunctionModel> businessFunctionList = Lists.newArrayList();
                    function.forEach(item -> {
                        businessFunctionList.add(item);
                    });
                    promise.complete(businessFunctionList);
                }).onFailure(fail -> {
                    log.error("查询数据失败", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

    /**
     * 获取功能中可执行的版本信息
     */
    public Future<ExecuteFunctionInfo> queryFunctionInfo(QueryExecuteFunctionInfo param) {
        Promise<ExecuteFunctionInfo> promise = Promise.promise();

        String sql = "select * from spider_business_function where id = #{id} and status = #{status}";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", param.getFunctionId());
        parameters.put("status", "START");
        SqlTemplate
                .forQuery(client, sql.toString())
                .mapTo(ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(function -> {
                    RowSet<FunctionModel> functions = function;
                    if (functions.size() == 0) {
                        promise.fail("没有找到对应的执行功能版本可以执行");
                    }
                    QueryVersionFunctionParam versionParam = new QueryVersionFunctionParam();
                    versionParam.setFunctionId(param.getFunctionId());
                    versionParam.setStatus("START");
                    versionParam.setPage(1);
                    versionParam.setSize(1);
                    Future<List<FunctionVersionModel>> versions = versionManager.selectVersion(versionParam);
                    versions.onSuccess(versionSuss -> {
                        List<FunctionVersionModel> functionVersionModels = versionSuss;
                        if (CollectionUtils.isEmpty(functionVersionModels)) {
                            promise.fail("没有找到可以执行的功能版本");
                        }
                        FunctionVersionModel functionVersionModel = functionVersionModels.get(0);
                        ExecuteFunctionInfo functionInfo = ExecuteFunctionInfo.builder()
                                .functionId(functionVersionModel.getFunctionId())
                                .startId(functionVersionModel.getStartEventId())
                                .versionId(functionVersionModel.getId())
                                .build();
                        promise.complete(functionInfo);
                    }).onFailure(fail -> {
                        promise.fail(fail);
                    });
                }).onFailure(fail -> {
                    log.error("查询数据失败", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }
}
