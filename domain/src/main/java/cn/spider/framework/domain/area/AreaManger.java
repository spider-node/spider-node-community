package cn.spider.framework.domain.area;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.container.sdk.interfaces.ContainerService;
import cn.spider.framework.domain.area.data.AreaModel;
import cn.spider.framework.domain.area.data.QueryAreaModel;
import cn.spider.framework.domain.area.data.enums.SdkStatus;
import cn.spider.framework.domain.sdk.data.RefreshSdkParam;
import cn.spider.framework.domain.sdk.data.SdkInfo;
import cn.spider.framework.domain.sdk.data.UploadSdkParam;
import cn.spider.framework.param.result.build.interfaces.ParamRefreshInterface;
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

import java.util.*;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-14  21:54
 * @Description: 领域管理
 * @Version: 1.0
 */
@Slf4j
public class AreaManger {

    private MySQLPool client;

    private ContainerService containerService;

    private ParamRefreshInterface paramRefreshInterface;

    public AreaManger(MySQLPool client, ContainerService containerService, ParamRefreshInterface paramRefreshInterface) {
        this.client = client;
        this.containerService = containerService;
        this.paramRefreshInterface = paramRefreshInterface;
    }

    private RowMapper<AreaModel> ROW_BUSINESS = row -> {
        AreaModel areaModel = new AreaModel();
        areaModel.setId(row.getString("id"));
        areaModel.setDesc(row.getString("desc"));
        areaModel.setAreaName(row.getString("area_name"));
        areaModel.setSdkUrl(row.getString("sdk_url"));
        areaModel.setSdkName(row.getString("sdk_name"));
        areaModel.setSdkStatus(StringUtils.isEmpty(row.getString("sdk_status")) ? null : SdkStatus.valueOf(row.getString("sdk_status")));
        areaModel.setScanClassPath(row.getString("scan_class_path"));
        return areaModel;
    };

    /**
     * 新增域
     *
     * @param model
     * @return
     */
    public Future<Void> createArea(AreaModel model) {
        Promise<Void> promise = Promise.promise();
        if(Objects.isNull(model.getSdkStatus())){
            model.setSdkStatus(SdkStatus.INIT);
        }
        StringBuilder sql = new StringBuilder();
        model.setId(UUID.randomUUID().toString());
        sql.append("insert into spider_area (`id`,`area_name`,`desc`,sdk_url,sdk_status,scan_class_path,`sdk_name`) values (#{id},#{areaName},#{desc},#{sdkUrl},#{sdkStatus},#{scanClassPath},#{sdkName})");
        JsonObject param = JsonObject.mapFrom(model);
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
     * 修改域
     *
     * @param model
     * @return
     */
    public Future<Void> updateArea(AreaModel model) {
        Promise<Void> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();
        sql.append("update spider_area set `area_name` = #{areaName} ,`desc` = #{desc},sdk_url = #{sdkUrl},sdk_status = #{sdkStatus},scan_class_path = #{scanClassPath},sdk_name = #{sdkName} where id = #{id}");
        JsonObject param = JsonObject.mapFrom(model);
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
     * 查询域
     *
     * @param areaModel
     * @return
     */
    public Future<List<AreaModel>> queryAreaModel(QueryAreaModel areaModel) {
        Promise<List<AreaModel>> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();
        areaModel.setPage((areaModel.getPage() - 1) * areaModel.getSize());
        sql.append("select * from spider_area where 1=1 ");
        if (StringUtils.isNotEmpty(areaModel.getAreaName())) {
            sql.append(" and area_name = #{areaName}");
        }
        if(StringUtils.isNotEmpty(areaModel.getId())){
            sql.append(" and id = #{id}");
        }
        sql.append(" order by create_time limit #{page},#{size}");
        JsonObject params = JsonObject.mapFrom(areaModel);
        Map<String, Object> parameters = params.getMap();
        SqlTemplate
                .forQuery(client, sql.toString())
                .mapTo(ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(users -> {
                    RowSet<AreaModel> function = users;
                    List<AreaModel> areaModels = Lists.newArrayList();
                    function.forEach(item -> {
                        areaModels.add(item);
                    });
                    promise.complete(areaModels);
                }).onFailure(fail -> {
                    log.error("查询数据失败 {}", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

    // 上传sdk
    public Future<Void> uploadSdk(UploadSdkParam param) {
        Promise<Void> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();
        sql.append("update spider_area set sdk_url = #{sdkUrl},scan_class_path = #{scanClassPath},sdk_name = #{sdkName} where id = #{id})");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("sdkUrl", param.getSdkUrl());
        parameters.put("scanClassPath", param.getScanClassPath());
        parameters.put("id", param.getAreaId());
        parameters.put("sdkName", param.getSdkName());

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
     * 查询域对象钟的sdk信息-》调用containerService 进行刷新
     *
     * @param param
     * @return
     */
    public Future<Void> refreshSdk(RefreshSdkParam param) {
        Promise<Void> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();
        sql.append("select * from spider_area where id = #{id}");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", param.getAreaId());
        SqlTemplate
                .forQuery(client, sql.toString())
                .mapTo(ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(users -> {
                    RowSet<AreaModel> function = users;
                    List<AreaModel> areaModels = Lists.newArrayList();
                    function.forEach(item -> {
                        areaModels.add(item);
                    });
                    AreaModel areaModel = areaModels.get(0);
                    JsonObject refSdkJson = new JsonObject()
                            .put("sdkName", areaModel.getSdkName())
                            .put("classPath", areaModel.getScanClassPath())
                            .put("url", areaModel.getSdkUrl());
                    // 去刷新 -- 数据
                    paramRefreshInterface.refreshMethod(refSdkJson).onSuccess(suss->{
                        promise.complete();
                    }).onFailure(fail->{
                        promise.fail(fail);
                    });
                }).onFailure(fail -> {
                    log.error("查询数据失败 {}", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

    public Future<Set<SdkInfo>> querySdkUrl() {
        Promise<Set<SdkInfo>> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();
        sql.append("select * from spider_area where sdk_status = #{sdkStatus}");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("sdkStatus", SdkStatus.DEPLOY.name());
        SqlTemplate
                .forQuery(client, sql.toString())
                .mapTo(ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(users -> {
                    RowSet<AreaModel> function = users;
                    Set<SdkInfo> urls = new HashSet<>();
                    function.forEach(item -> {
                        if (StringUtils.isEmpty(item.getSdkUrl())) {
                            return;
                        }
                        urls.add(new SdkInfo(item.getSdkUrl(), item.getSdkName(), item.getScanClassPath()));
                    });
                    promise.complete(urls);
                }).onFailure(fail -> {
                    log.error("查询数据失败 {}", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }


}
