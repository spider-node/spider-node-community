package cn.spider.framework.flow.funtion;

import cn.spider.framework.common.data.enums.BpmnStatus;
import cn.spider.framework.common.data.enums.JarStatus;
import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.DestroyBpmnData;
import cn.spider.framework.common.event.data.DestroyClassData;
import cn.spider.framework.common.event.data.LoaderClassData;
import cn.spider.framework.common.event.data.DeployBpmnData;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.container.sdk.data.*;
import cn.spider.framework.container.sdk.interfaces.ContainerService;
import cn.spider.framework.flow.business.data.BusinessFunctions;
import cn.spider.framework.flow.funtion.data.Bpmn;
import cn.spider.framework.flow.funtion.data.BpmnRow;
import cn.spider.framework.flow.funtion.data.Sdk;
import cn.spider.framework.flow.funtion.data.SdkRow;
import cn.spider.framework.flow.load.loader.ClassLoaderManager;
import cn.spider.framework.flow.resource.factory.StartEventFactory;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.funtion
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-26  18:07
 * @Description: 功能生命周期管理实习类
 * @Version: 1.0
 */
@Slf4j
@Component
public class ContainerServiceImpl implements ContainerService {

    @Resource
    private StartEventFactory startEventFactory;

    @Resource
    private ClassLoaderManager classLoaderManager;

    @Resource
    private EventManager eventManager;

    @Resource
    private MySQLPool client;

    /**
     * 部署bpmn
     *
     * @param data
     * @return
     */
    @Override
    public Future<Void> deployBpmn(JsonObject data) {
        Promise<Void> promise = Promise.promise();
        DeployBpmnRequest request = data.mapTo(DeployBpmnRequest.class);
        String sql = null;
        if (StringUtils.isEmpty(request.getId())) {
            sql = "insert into bpmn (id,bpmn_name,url,status) values (#{id},#{bpmnName},#{url},#{status})";
            String id = UUID.randomUUID().toString();
            data.put("id", id);
        } else {
            sql = "update bpmn set bpmn_name = #{bpmnName},url = #{url} where id = #{id}";
        }
        try {
            Map<String, Object> parameters = data.getMap();
            SqlTemplate
                    .forUpdate(client, sql)
                    .execute(parameters)
                    .onSuccess(function -> {
                        promise.complete();
                    }).onFailure(fail -> {
                        promise.fail(fail);
                    });
        } catch (Exception e) {
            log.error(ExceptionMessage.getStackTrace(e));
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<Void> refreshBpmn(JsonObject data) {
        RefreshBpmnParam param = data.mapTo(RefreshBpmnParam.class);
        // 执行一次卸载，
        startEventFactory.destroyBpmn(param.getBpmnUrl());
        // 执行一次加载
        startEventFactory.dynamicsLoaderBpmn(param.getBpmnUrl());

        // 发送事件-通知其他节点进行加载
        DeployBpmnData deployBpmnData = new DeployBpmnData();
        deployBpmnData.setUrl(param.getBpmnUrl());
        eventManager.sendMessage(EventType.DEPLOY_BPMN, deployBpmnData);

        return Future.succeededFuture();
    }

    @Override
    public Future<Void> startBpmn(JsonObject data) {
        Promise<Void> promise = Promise.promise();
        Map<String, Object> parameters = data.getMap();
        SqlTemplate
                .forQuery(client, "select * from bpmn where id = #{id}")
                .mapTo(BpmnRow.ROW_BPMN)
                .execute(parameters)
                .onSuccess(users -> {
                    RowSet<Bpmn> bpmns = users;
                    if (bpmns.size() == 0) {
                        promise.fail("没有找到对应的数据");
                    }
                    for (Bpmn bpmn : bpmns) {
                        if (bpmn.getStatus().equals(BpmnStatus.ENABLE)) {
                            promise.fail("数据状态不正确");
                            break;
                        }
                        String url = bpmn.getUrl();
                        // 加载bpmn
                        startEventFactory.dynamicsLoaderBpmn(url);
                        // 发送事件-通知其他节点进行加载
                        DeployBpmnData deployBpmnData = new DeployBpmnData();
                        deployBpmnData.setStatus(BpmnStatus.ENABLE);
                        deployBpmnData.setUrl(bpmn.getUrl());
                        deployBpmnData.setBpmnName(bpmn.getBpmnName());
                        deployBpmnData.setId(bpmn.getId());
                        eventManager.sendMessage(EventType.DEPLOY_BPMN, deployBpmnData);
                        parameters.put("status", BpmnStatus.ENABLE);
                        SqlTemplate
                                .forUpdate(client, "update bpmn set status = #{status} where id = #{id}")
                                .execute(parameters)
                                .onSuccess(function -> {
                                    promise.complete();
                                }).onFailure(fail -> {
                                    promise.fail(fail);
                                });
                        break;
                    }

                }).onFailure(fail -> {
                    log.error("查询数据失败", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

    @Override
    public Future<Void> unloadBpmn(JsonObject data) {
        UnloadBpmnParam param = data.mapTo(UnloadBpmnParam.class);
        // 执行一次卸载，
        startEventFactory.destroyBpmn(param.getBpmnUrl());
        // 发生事件通知需要卸载
        DestroyBpmnData destroyBpmnData = DestroyBpmnData.builder()
                .bpmnName(param.getBpmnUrl())
                .build();
        eventManager.sendMessage(EventType.DESTROY_BPMN, destroyBpmnData);
        return null;
    }

    @Override
    public Future<Void> destroyBpmn(JsonObject data) {
        Promise<Void> promise = Promise.promise();
        Map<String, Object> parameters = data.getMap();
        SqlTemplate
                .forQuery(client, "select * from bpmn where id = #{id}")
                .mapTo(BpmnRow.ROW_BPMN)
                .execute(parameters)
                .onSuccess(users -> {
                    RowSet<Bpmn> bpmns = users;
                    if (bpmns.size() == 0) {
                        promise.fail("没有找到对应的数据");
                    }
                    for (Bpmn bpmn : bpmns) {
                        if (bpmn.getStatus().equals(BpmnStatus.STOP)) {
                            promise.fail("数据状态不正确");
                            break;
                        }
                        String url = bpmn.getUrl();
                        // 加载bpmn
                        startEventFactory.destroyBpmn(url);
                        // 发送事件-通知其他节点进行加载
                        DestroyBpmnData destroyBpmnData = DestroyBpmnData.builder()
                                .bpmnName(bpmn.getBpmnName())
                                .build();
                        eventManager.sendMessage(EventType.DESTROY_BPMN, destroyBpmnData);

                        parameters.put("status", BpmnStatus.STOP);
                        SqlTemplate
                                .forUpdate(client, "update bpmn set status = #{status} where id = #{id}")
                                .execute(parameters)
                                .onSuccess(function -> {
                                    promise.complete();
                                }).onFailure(fail -> {
                                    promise.fail(fail);
                                });
                        break;
                    }

                }).onFailure(fail -> {
                    log.error("查询数据失败", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

    @Override
    public Future<Void> loaderClass(JsonObject data) {
        Promise<Void> respond = Promise.promise();
        LoaderClassRequest request = data.mapTo(LoaderClassRequest.class);
        String sql = null;
        if (StringUtils.isEmpty(request.getId())) {
            sql = "insert into sdk (id,jar_name,class_path,url,status) values(#{id},#{jarName},#{classPath},#{url},#{status})";
            String id = UUID.randomUUID().toString();
            data.put("id", id);
        } else {
            sql = "update sdk set jar_name = #{jarName}, class_path = #{classPath}, url=#{url} where id = #{id}";
        }
        Map<String, Object> parameters = data.getMap();
        SqlTemplate
                .forUpdate(client, sql)
                .execute(parameters)
                .onSuccess(function -> {
                    respond.complete();
                }).onFailure(fail -> {
                    respond.fail(fail);
                });
        return respond.future();
    }

    @Override
    public Future<Void> startClass(JsonObject data) {
        Promise<Void> promise = Promise.promise();
        Map<String, Object> parameters = data.getMap();
        SqlTemplate
                .forQuery(client, "select * from sdk where id = #{id}")
                .mapTo(SdkRow.ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(users -> {
                    RowSet<Sdk> sdks = users;
                    if (sdks.size() == 0) {
                        promise.fail("没有找到对应的数据");
                    }
                    for (Sdk sdk : sdks) {
                        if (sdk.getStatus().equals(JarStatus.ENABLE)) {
                            promise.fail("数据状态不正确");
                            break;
                        }
                        String url = sdk.getUrl();
                        // 加载sdk
                        classLoaderManager.loaderUrlJar(sdk.getJarName(), sdk.getClassPath(), url);
                        LoaderClassData loaderClassData = new LoaderClassData();
                        BeanUtils.copyProperties(sdk, loaderClassData);
                        eventManager.sendMessage(EventType.LOADER_JAR, loaderClassData);
                        parameters.put("status", BpmnStatus.ENABLE);
                        SqlTemplate
                                .forUpdate(client, "update sdk set status = #{status} where id = #{id}")
                                .execute(parameters)
                                .onSuccess(function -> {
                                    promise.complete();
                                }).onFailure(fail -> {
                                    promise.fail(fail);
                                });
                        break;
                    }

                }).onFailure(fail -> {
                    log.error("查询数据失败", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

    /**
     * 刷新sdk
     * @param data
     * @return
     */
    @Override
    public Future<Void> refreshSdk(JsonObject data) {
        RefreshSdkParam refreshSdkParam = data.mapTo(RefreshSdkParam.class);
        classLoaderManager.loaderUrlJar(refreshSdkParam.getSdkName(), refreshSdkParam.getClassPath(), refreshSdkParam.getSdkUrl());
        // 发事件
        LoaderClassData loaderClassData = new LoaderClassData();
        loaderClassData.setClassPath(refreshSdkParam.getClassPath());
        loaderClassData.setJarName(refreshSdkParam.getSdkName());
        loaderClassData.setUrl(refreshSdkParam.getSdkUrl());
        eventManager.sendMessage(EventType.LOADER_JAR, loaderClassData);
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> unloadSdk(JsonObject data) {
        UnloadSdkParam unloadSdkParam = data.mapTo(UnloadSdkParam.class);
        if (!classLoaderManager.unloadJar(unloadSdkParam.getJarName())) {
            return Future.failedFuture(new RuntimeException("卸载失败"));
        }
        DestroyClassData destroyClassData = DestroyClassData.builder()
                .jarName(unloadSdkParam.getJarName())
                .build();
        eventManager.sendMessage(EventType.DESTROY_JAR, destroyClassData);
        // 发事件
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> destroyClass(JsonObject data) {
        Map<String, Object> parameters = data.getMap();
        Promise<Void> promise = Promise.promise();
        SqlTemplate
                .forQuery(client, "select * from sdk where id = #{id}")
                .mapTo(SdkRow.ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(users -> {
                    RowSet<Sdk> sdks = users;
                    if (sdks.size() == 0) {
                        promise.fail("没有找到对应的数据");
                    }
                    for (Sdk sdk : sdks) {
                        if (sdk.getStatus().equals(JarStatus.STOP)) {
                            promise.fail("数据状态不正确");
                            break;
                        }

                        if (!classLoaderManager.unloadJar(sdk.getJarName())) {
                            promise.fail("卸载失败");
                            break;
                        }

                        DestroyClassData destroyClassData = DestroyClassData.builder()
                                .jarName(sdk.getJarName())
                                .build();
                        eventManager.sendMessage(EventType.DESTROY_JAR, destroyClassData);
                        log.info("卸载成功_jarName {}", sdk.getJarName());
                        parameters.put("status", JarStatus.STOP);
                        SqlTemplate
                                .forUpdate(client, "update sdk set status = #{status} where id = #{id}")
                                .execute(parameters)
                                .onSuccess(function -> {
                                    promise.complete();
                                }).onFailure(fail -> {
                                    promise.fail(fail);
                                });
                        break;
                    }

                }).onFailure(fail -> {
                    log.error("查询数据失败", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

    @Override
    public Future<JsonObject> queryBpmn(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        int page = data.getInteger("page") - 1;
        int size = data.getInteger("size");
        Map<String, Object> parameters = data.getMap();
        parameters.put("page", page * size);
        parameters.put("size", size);
        String sql = buildQueryBpmnSql(data);
        SqlTemplate
                .forQuery(client, sql)
                .mapTo(BpmnRow.ROW_BPMN)
                .execute(parameters)
                .onSuccess(users -> {
                    RowSet<Bpmn> bpmns = users;
                    List<JsonObject> bpmnList = Lists.newArrayList();
                    bpmns.forEach(item -> {
                        bpmnList.add(JsonObject.mapFrom(item));
                    });
                    QueryBpmnResponse queryBpmnResponse = new QueryBpmnResponse();
                    queryBpmnResponse.setBpmns(bpmnList);
                    promise.complete(JsonObject.mapFrom(queryBpmnResponse));
                }).onFailure(fail -> {
                    log.error("查询数据失败", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

    public String buildQueryBpmnSql(JsonObject data) {
        QueryBpmnRequest queryBpmnRequest = JSON.parseObject(data.toString(), QueryBpmnRequest.class);
        StringBuilder sql = new StringBuilder("select * from bpmn where 1=1");
        if (StringUtils.isNotEmpty(queryBpmnRequest.getBpmnName())) {
            sql.append(" and bpmn_name = #{bpmnName}");
        }

        if (Objects.nonNull(queryBpmnRequest.getStatus())) {
            sql.append(" and status = #{status}");
        }
        sql.append(" limit  #{page},#{size}");
        return sql.toString();
    }

    @Override
    public Future<JsonObject> querySdk(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        int page = data.getInteger("page") - 1;
        int size = data.getInteger("size");
        Map<String, Object> parameters = data.getMap();
        parameters.put("page", page * size);
        parameters.put("size", size);
        String sql = buildQuerySdkSql(data);
        SqlTemplate
                .forQuery(client, sql)
                .mapTo(SdkRow.ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(sdkSuss -> {
                    RowSet<Sdk> sdkRows = sdkSuss;
                    List<JsonObject> sdks = Lists.newArrayList();
                    sdkRows.forEach(item -> {
                        sdks.add(JsonObject.mapFrom(item));
                    });
                    QuerySdkResponse querySdkResponse = new QuerySdkResponse();
                    querySdkResponse.setSdk(sdks);
                    promise.complete(JsonObject.mapFrom(querySdkResponse));
                }).onFailure(fail -> {
                    log.error("查询数据失败", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

    public String buildQuerySdkSql(JsonObject data) {
        QuerySdkRequest querySdkRequest = JSON.parseObject(data.toString(), QuerySdkRequest.class);
        StringBuilder sql = new StringBuilder("select * from sdk where 1=1");

        if (StringUtils.isNotEmpty(querySdkRequest.getJarName())) {
            sql.append(" and jar_name = #{JarName}");
        }
        if (Objects.nonNull(querySdkRequest.getStatus())) {
            sql.append(" and status = #{status}");
        }
        sql.append(" limit  #{page},#{size}");
        return sql.toString();
    }

}
