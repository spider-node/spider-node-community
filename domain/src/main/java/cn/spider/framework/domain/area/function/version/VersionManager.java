package cn.spider.framework.domain.area.function.version;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.container.sdk.data.RefreshBpmnParam;
import cn.spider.framework.container.sdk.interfaces.ContainerService;
import cn.spider.framework.domain.area.function.version.data.FunctionVersionModel;
import cn.spider.framework.domain.area.function.version.data.QueryVersionFunctionParam;
import cn.spider.framework.domain.area.function.version.data.VersionStopStartParam;
import cn.spider.framework.domain.area.function.version.data.enums.VersionStatus;
import cn.spider.framework.domain.sdk.data.UploadBpmnParam;
import com.google.common.collect.Lists;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.function.version
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-14  22:04
 * @Description: 版本管理
 * @Version: 1.0
 */
@Slf4j
public class VersionManager {

    private MySQLPool client;

    private ContainerService containerService;

    public VersionManager(MySQLPool client) {
        this.client = client;
    }

    private RowMapper<FunctionVersionModel> ROW_BUSINESS = row -> {
        FunctionVersionModel functionVersionModel = new FunctionVersionModel();
        functionVersionModel.setId(row.getString("id"));
        functionVersionModel.setFunctionName(row.getString("function_name"));
        functionVersionModel.setDesc(row.getString("desc"));
        functionVersionModel.setVersion(row.getString("version"));
        functionVersionModel.setFunctionId(row.getString("function_id"));
        functionVersionModel.setBpmnUrl(row.getString("bpmn_url"));
        functionVersionModel.setStartEventId(row.getString("start_event_id"));
        functionVersionModel.setBpmnName(row.getString("bpmn_name"));
        functionVersionModel.setBpmnStatus(row.getString("bpmn_status"));
        functionVersionModel.setReqParamClassType(row.getString("req_param_class_type"));
        functionVersionModel.setStatus(VersionStatus.valueOf(row.getString("status")));
        return functionVersionModel;
    };


    /**
     * 新增功能版本
     *
     * @param functionVersionModel
     * @return
     */
    public Future<Void> createFunctionVersion(FunctionVersionModel functionVersionModel) {
        Promise<Void> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();
        sql.append("insert into spider_business_function_version (`id`,`function_name`,`desc`,`version`,`function_id`,`bpmn_url`,`start_event_id`,`bpmn_name`,`bpmn_status`,`req_param_class_type`,`status`) values (#{id},#{functionName},#{desc},#{version},#{functionId},#{bpmnUrl},#{startEventId},#{bpmnName},#{bpmnStatus},#{reqParamClassType},#{status})");
        JsonObject param = JsonObject.mapFrom(functionVersionModel);
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
     * update功能版本
     *
     * @param functionVersionModel
     * @return
     */
    public Future<Void> updateFunctionVersion(FunctionVersionModel functionVersionModel) {
        Promise<Void> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();
        sql.append("update spider_business_function_version set function_name = #{functionName}, `desc` = #{desc} ,version = #{version},function_id = #{functionId},bpmn_url = #{bpmnUrl},start_event_id = #{startEventId} ,`bpmn_name` = #{bpmnName},`bpmn_status` = #{bpmnStatus},`req_param_class_type` = #{reqParamClassType},`status` = #{status} where id = #{id}");
        JsonObject param = JsonObject.mapFrom(functionVersionModel);
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
     * @param
     * @return
     */
    public Future<Void> startStop(VersionStopStartParam param) {
        Promise<Void> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();
        sql.append("update spider_business_function_version set `status` = #{status} where id = #{id}");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", param.getVersionId());
        parameters.put("status", param.getStatus().name());
        SqlTemplate
                .forUpdate(client, sql.toString())
                .execute(parameters)
                .onSuccess(function -> {
                    promise.future();
                }).onFailure(fail -> {
                    promise.fail(fail);
                });
        return promise.future();
    }

    /**
     * 查询版本信息
     *
     * @param param
     * @return
     */
    public Future<List<FunctionVersionModel>> selectVersion(QueryVersionFunctionParam param) {

        Promise<List<FunctionVersionModel>> promise = Promise.promise();

        StringBuilder sql = new StringBuilder();
        JsonObject params = JsonObject.mapFrom(param);
        param.setPage((param.getPage() - 1) * param.getSize());
        Map<String, Object> parameters = params.getMap();
        sql.append("select * from area_function_version where 1=1 ");
        if (StringUtils.isNotEmpty(param.getFunctionId())) {
            sql.append(" and function_id = #{functionId} ");
        }

        if (StringUtils.isNotEmpty(param.getStartEventId())) {
            sql.append(" and start_event_id = #{startEventId} ");
        }

        if (StringUtils.isNotEmpty(param.getStatus())) {
            sql.append(" and `status` = #{status} ");
        }

        if (StringUtils.isNotEmpty(param.getFunctionName())) {
            sql.append(" and `function_name` = #{functionName} ");
        }

        if (StringUtils.isNotEmpty(param.getVersionId())) {
            sql.append(" and `version_id` = #{versionId} ");
        }

        sql.append("#{page},#{size}");

        SqlTemplate
                .forQuery(client, "SELECT * FROM spider_business_function_version limit #{page},#{size}")
                .mapTo(ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(functions -> {
                    RowSet<FunctionVersionModel> function = functions;
                    List<FunctionVersionModel> businessFunctionList = Lists.newArrayList();
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

    // 刷新-bpmn
    public Future<Void> refreshBpmn(UploadBpmnParam param) {
        Promise<Void> promise = Promise.promise();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", param.getFunctionVersionId());
        SqlTemplate
                .forQuery(client, "SELECT * FROM spider_business_function_version where id = #{id}")
                .mapTo(ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(functions -> {
                    RowSet<FunctionVersionModel> function = functions;
                    List<FunctionVersionModel> businessFunctionList = Lists.newArrayList();
                    function.forEach(item -> {
                        businessFunctionList.add(item);
                    });
                    FunctionVersionModel functionVersionModel = businessFunctionList.get(0);
                    // 进行刷新
                    RefreshBpmnParam refreshBpmnParam = new RefreshBpmnParam();
                    refreshBpmnParam.setBpmnUrl(functionVersionModel.getBpmnUrl());
                    Future<Void> containerRefresh = containerService.refreshBpmn(JsonObject.mapFrom(refreshBpmnParam));
                    containerRefresh.onSuccess(containerRefreshSuss -> {
                        promise.complete();
                    }).onFailure(containerRefreshFail -> {
                        promise.fail(containerRefreshFail);
                    });
                }).onFailure(fail -> {
                    log.error("查询数据失败", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

}
