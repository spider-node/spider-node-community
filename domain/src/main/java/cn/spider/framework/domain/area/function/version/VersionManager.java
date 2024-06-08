package cn.spider.framework.domain.area.function.version;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.container.sdk.data.UnloadBpmnParam;
import cn.spider.framework.container.sdk.interfaces.ContainerService;
import cn.spider.framework.domain.area.data.enums.BpmnStatus;
import cn.spider.framework.domain.area.function.FunctionManger;
import cn.spider.framework.domain.area.function.data.QueryFunctionParam;
import cn.spider.framework.domain.area.function.version.data.FunctionVersionModel;
import cn.spider.framework.domain.area.function.version.data.QueryVersionFunctionParam;
import cn.spider.framework.domain.area.function.version.data.VersionStopStartParam;
import cn.spider.framework.domain.area.function.version.data.enums.VersionStatus;
import cn.spider.framework.domain.sdk.data.RefreshBpmnParam;
import cn.spider.framework.domain.sdk.data.enums.UploadBpmnStatus;
import com.alibaba.fastjson.JSON;
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

    public VersionManager(MySQLPool client, ContainerService containerService) {
        this.client = client;
        this.containerService = containerService;
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
        if(StringUtils.isNotEmpty(row.getString("bpmn_status"))){
            functionVersionModel.setBpmnStatus(BpmnStatus.valueOf(row.getString("bpmn_status")));
        }
        functionVersionModel.setResultMapping(row.getString("result_mapping"));
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
        functionVersionModel.setId(UUID.randomUUID().toString());
        functionVersionModel.setId(UUID.randomUUID().toString());
        if (Objects.isNull(functionVersionModel.getStatus())) {
            functionVersionModel.setStatus(VersionStatus.STOP);
        }
        functionVersionModel.setBpmnStatus(BpmnStatus.INIT);
        JsonObject param = JsonObject.mapFrom(functionVersionModel);
        Map<String, Object> parameters = param.getMap();

        sql.append("insert into spider_business_function_version (id,`desc`,version,function_name,function_id,`status`,`bpmn_status`) values (#{id},#{desc},#{version},#{functionName},#{functionId},#{status},#{bpmnStatus})");
        SqlTemplate
                .forUpdate(client, sql.toString())
                .execute(parameters)
                .onSuccess(function -> {
                    promise.complete();
                }).onFailure(fail -> {
                    log.info("新增version错误信息为 {}",ExceptionMessage.getStackTrace(fail));
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

        sql.append("update spider_business_function_version set function_name = #{functionName}, `desc` = #{desc} ,version = #{version},function_id = #{functionId},bpmn_url = #{bpmnUrl},start_event_id = #{startEventId} ,`bpmn_name` = #{bpmnName},`bpmn_status` = #{bpmnStatus},`status` = #{status} where id = #{id}");
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
        QueryVersionFunctionParam queryVersionFunctionParam = new QueryVersionFunctionParam();
        queryVersionFunctionParam.setVersionId(param.getVersionId());
        queryVersionFunctionParam.setStatus(VersionStatus.START.name());
        queryVersionFunctionParam.setPage(1);
        queryVersionFunctionParam.setSize(1);
        Future<List<FunctionVersionModel>> functionFuture = selectVersion(queryVersionFunctionParam);
        functionFuture.onSuccess(suss->{
            List<FunctionVersionModel> functionVersionModels = suss;
            if(param.equals(VersionStatus.START)){
                if(CollectionUtils.isEmpty(functionVersionModels)){
                    promise.fail("该功能中存在一个有效版本,不允许启动其他版本");
                }
            }
            sql.append("update spider_business_function_version set `status` = #{status} where id = #{id}");
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("id", param.getVersionId());
            parameters.put("status", param.getStatus().name());
            SqlTemplate
                    .forUpdate(client, sql.toString())
                    .execute(parameters)
                    .onSuccess(function -> {
                        RefreshBpmnParam refreshBpmnParam = new RefreshBpmnParam();
                        refreshBpmnParam.setFunctionVersionId(param.getVersionId());
                        refreshBpmnParam.setStatus(param.getStatus().equals(VersionStatus.START) ? UploadBpmnStatus.DEPLOY : UploadBpmnStatus.INIT);
                        Future<Void> refreshFuture = refreshBpmn(refreshBpmnParam);
                        refreshFuture.onSuccess(refreshSuss -> {
                            promise.complete();
                        }).onFailure(refreshFail -> {
                            promise.fail(refreshFail);
                        });
                        // 发生事件---->该节点信息-
                    }).onFailure(fail -> {
                        promise.fail(fail);
                    });

        }).onFailure(fail->{
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
        param.setPage((param.getPage() - 1) * param.getSize());
        JsonObject params = JsonObject.mapFrom(param);
        Map<String, Object> parameters = params.getMap();
        sql.append("select * from spider_business_function_version where 1=1 ");
        if (StringUtils.isNotEmpty(param.getFunctionId())) {
            sql.append(" and function_id = #{functionId} ");
        }

        if (StringUtils.isNotEmpty(param.getStartEventId())) {
            sql.append(" and start_event_id = #{startEventId} ");
        }

        if(StringUtils.isNotEmpty(param.getVersion())){
            sql.append(" and version = #{version} ");
        }

        if (StringUtils.isNotEmpty(param.getStatus())) {
            sql.append(" and `status` = #{status} ");
        }

        if (StringUtils.isNotEmpty(param.getFunctionName())) {
            sql.append(" and function_name = #{functionName} ");
        }

        if (StringUtils.isNotEmpty(param.getVersionId())) {
            sql.append(" and id = #{versionId} ");
        }

        sql.append(" order by create_time limit #{page},#{size}");

        SqlTemplate
                .forQuery(client, sql.toString())
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
                    log.error("查询数据失败 {}", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

    // 刷新-bpmn
    public Future<Void> refreshBpmn(RefreshBpmnParam param) {
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
                    if(StringUtils.isEmpty(functionVersionModel.getBpmnUrl())){
                        promise.complete();
                        return;
                    }
                    // 进行刷新
                    Future<Void> containerRefresh = deployBpmns(functionVersionModel);
                    containerRefresh.onSuccess(containerRefreshSuss -> {
                        BpmnStatus bpmnStatus = BpmnStatus.DEPLOY;
                        functionVersionModel.setBpmnStatus(bpmnStatus);
                        updateFunctionVersion(functionVersionModel);
                        promise.complete();
                    }).onFailure(containerRefreshFail -> {
                        promise.fail(containerRefreshFail);
                    });
                }).onFailure(fail -> {
                    log.error("查询数据失败 {}", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

    /**
     * 部署bpmn
     *
     * @param functionVersionModel
     * @return
     */
    private Future<Void> deployBpmns(FunctionVersionModel functionVersionModel) {
        cn.spider.framework.container.sdk.data.RefreshBpmnParam refreshBpmnParam = new cn.spider.framework.container.sdk.data.RefreshBpmnParam();
        refreshBpmnParam.setBpmnUrl(functionVersionModel.getBpmnUrl());
        return containerService.refreshBpmn(JsonObject.mapFrom(refreshBpmnParam));
    }

    /**
     * 卸载
     *
     * @param functionVersionModel
     * @return
     */
    private Future<Void> unload(FunctionVersionModel functionVersionModel) {
        UnloadBpmnParam unloadBpmnParam = new UnloadBpmnParam();
        unloadBpmnParam.setBpmnUrl(functionVersionModel.getBpmnUrl());
        return containerService.unloadBpmn(JsonObject.mapFrom(unloadBpmnParam));
    }


    // 初始化bpmn
    public void deployBpmn() {
        Map<String, Object> parameters = new HashMap<>();
        SqlTemplate
                .forQuery(client, "SELECT * FROM spider_business_function_version")
                .mapTo(ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(functions -> {
                    RowSet<FunctionVersionModel> function = functions;
                    List<FunctionVersionModel> businessFunctionList = Lists.newArrayList();
                    function.forEach(item -> {
                        businessFunctionList.add(item);
                    });

                    for (FunctionVersionModel functionVersionModel : businessFunctionList) {
                        // 进行刷新
                        cn.spider.framework.container.sdk.data.RefreshBpmnParam refreshBpmnParam = new cn.spider.framework.container.sdk.data.RefreshBpmnParam();
                        refreshBpmnParam.setBpmnUrl(functionVersionModel.getBpmnUrl());
                        Future<Void> containerRefresh = containerService.refreshBpmn(JsonObject.mapFrom(refreshBpmnParam));
                        containerRefresh.onSuccess(containerRefreshSuss -> {

                        }).onFailure(containerRefreshFail -> {

                        });
                    }
                }).onFailure(fail -> {
                    log.error("查询版本信息失败 {}", ExceptionMessage.getStackTrace(fail));
                });
    }

    /**
     * 查询bpmn-url
     */
    public Future<Set<String>> getBpmnUrl() {
        Promise<Set<String>> promise = Promise.promise();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("status", VersionStatus.START.name());
        parameters.put("bpmnStatus", BpmnStatus.DEPLOY.name());
        SqlTemplate
                .forQuery(client, "SELECT * FROM spider_business_function_version where status = #{status} and bpmn_status = #{bpmnStatus}")
                .mapTo(ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(functions -> {
                    RowSet<FunctionVersionModel> function = functions;
                    Set<String> bpmnUrls = new HashSet<>();
                    function.forEach(item -> {
                        if (StringUtils.isEmpty(item.getBpmnUrl())) {
                            return;
                        }
                        bpmnUrls.add(item.getBpmnUrl());
                    });
                    promise.complete(bpmnUrls);
                }).onFailure(fail -> {
                    promise.fail(fail);
                });
        return promise.future();
    }


}
