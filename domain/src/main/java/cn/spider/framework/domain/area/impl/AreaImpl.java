package cn.spider.framework.domain.area.impl;

import cn.spider.framework.domain.area.AreaManger;
import cn.spider.framework.domain.area.data.AreaModel;
import cn.spider.framework.domain.area.data.QueryAreaModel;
import cn.spider.framework.domain.area.data.QueryDomainResult;
import cn.spider.framework.domain.area.data.UpdateBaseFieldParam;
import cn.spider.framework.domain.area.datasource.DatasourceManager;
import cn.spider.framework.domain.area.datasource.data.QueryDatasourceParam;
import cn.spider.framework.domain.area.datasource.data.QueryDatasourceResult;
import cn.spider.framework.domain.area.datasource.data.QueryTableInfoParam;
import cn.spider.framework.domain.area.datasource.data.QueryTableInfoResult;
import cn.spider.framework.domain.area.datasource.entity.AreaDatasourceInfo;
import cn.spider.framework.domain.area.domain.entity.SpiderArea;
import cn.spider.framework.domain.area.domain.service.ISpiderAreaService;
import cn.spider.framework.domain.area.sondomain.entity.QuerySonAreaInfoParam;
import cn.spider.framework.domain.area.sondomain.entity.*;
import cn.spider.framework.domain.area.sondomain.service.IAreaDomainBaseInfoService;
import cn.spider.framework.domain.area.sondomain.service.ISpiderSonAreaService;
import cn.spider.framework.domain.sdk.data.*;
import cn.spider.framework.domain.sdk.interfaces.AreaInterface;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
public class AreaImpl implements AreaInterface {

    private AreaManger areaManger;

    private ISpiderSonAreaService spiderSonAreaService;

    private DatasourceManager datasourceManager;

    private ISpiderAreaService spiderAreaService;

    private Executor spiderBusinessPool;

    private IAreaDomainBaseInfoService areaDomainBaseInfoService;

    public AreaImpl(AreaManger areaManger, ISpiderSonAreaService spiderSonAreaService, DatasourceManager datasourceManager, Executor spiderBusinessPool, ISpiderAreaService spiderAreaService, IAreaDomainBaseInfoService areaDomainBaseInfoService) {
        this.areaManger = areaManger;
        this.spiderSonAreaService = spiderSonAreaService;
        this.datasourceManager = datasourceManager;
        this.spiderBusinessPool = spiderBusinessPool;
        this.spiderAreaService = spiderAreaService;
        this.areaDomainBaseInfoService = areaDomainBaseInfoService;
    }

    /**
     * 更新sdk的地址以及加载信息
     *
     * @param data
     * @return
     */
    @Override
    public Future<Void> updateSdk(JsonObject data) {
        UploadSdkParam uploadSdkParam = data.mapTo(UploadSdkParam.class);
        return areaManger.uploadSdk(uploadSdkParam);
    }

    /**
     * 刷新sdk-在spider中的class信息
     *
     * @param data
     * @return
     */
    @Override
    public Future<Void> refreshSdk(JsonObject data) {
        RefreshSdkParam refreshSdkParam = data.mapTo(RefreshSdkParam.class);
        return areaManger.refreshSdk(refreshSdkParam);
    }

    /**
     * 插入域信息
     *
     * @param data
     * @return
     */
    @Override
    public Future<Void> insertArea(JsonObject data) {
        return areaManger.createArea(JSON.parseObject(data.toString(), AreaModel.class));
    }

    /**
     * 新增修改领域信息
     *
     * @param data 领域信息
     * @return Future
     */
    @Override
    public Future<Void> upsertAreaV2(JsonObject data) {
        Promise<Void> promise = Promise.promise();
        SpiderArea area = data.mapTo(SpiderArea.class);
        spiderBusinessPool.execute(() -> {
            try {
                if (StringUtils.isEmpty(area.getId())) {
                    area.setId(UUID.randomUUID().toString());
                    spiderAreaService.save(area);
                    promise.complete();
                    return;
                }
                spiderAreaService.updateById(area);
                promise.complete();
            } catch (Exception e) {
                promise.fail(e);
            }
        });
        return promise.future();
    }

    @Override
    public Future<Void> updateArea(JsonObject data) {
        return areaManger.updateArea(data.mapTo(AreaModel.class));
    }

    @Override
    public Future<JsonObject> queryArea(JsonObject data) {
        Promise<JsonObject> result = Promise.promise();
        spiderBusinessPool.execute(() -> {
            QueryAreaModel queryAreaModel = data.mapTo(QueryAreaModel.class);
            Page<SpiderArea> rowPage = new Page(queryAreaModel.getPage(), queryAreaModel.getSize());
            LambdaQueryWrapper<SpiderArea> queryWrapper = new LambdaQueryWrapper<SpiderArea>()
                    .eq(StringUtils.isNotEmpty(queryAreaModel.getId()), SpiderArea::getId, queryAreaModel.getId())
                    .likeRight(StringUtils.isNotEmpty(queryAreaModel.getAreaName()), SpiderArea::getAreaName, queryAreaModel.getAreaName());
            IPage page = spiderAreaService.page(rowPage, queryWrapper);
            QueryDomainResult queryDomainResult = new QueryDomainResult(page.getRecords(), page.getTotal());
            result.complete(JsonObject.mapFrom(queryDomainResult));
        });
        return result.future();
    }

    @Override
    public Future<JsonObject> queryAreaSdk() {
        Promise<JsonObject> promise = Promise.promise();
        Future<Set<SdkInfo>> sdkUrlFuture = areaManger.querySdkUrl();
        sdkUrlFuture.onSuccess(suss -> {
            SdkUrlQueryResult sdkUrlQueryResult = new SdkUrlQueryResult();
            sdkUrlQueryResult.setSdkInfos(Lists.newArrayList(suss));
            promise.complete(JsonObject.mapFrom(sdkUrlQueryResult));
        }).onFailure(fail -> {
            promise.fail(fail);
        });
        return promise.future();
    }

    @Override
    public Future<JsonObject> querySonArea(JsonObject data) {
        QuerySonAreaParam areaParam = data.mapTo(QuerySonAreaParam.class);
        QuerySonAreaResult areaResult = spiderSonAreaService.querySonAreaBase(areaParam);
        return Future.succeededFuture(JsonObject.mapFrom(areaResult));
    }

    @Override
    public Future<JsonObject> querySonBase(JsonObject data) {
        QuerySonBaseParam param = data.mapTo(QuerySonBaseParam.class);
        if (CollectionUtils.isNotEmpty(param.getSonBaseIds())) {
            List<AreaDomainBaseInfo> areaDomainBaseInfos = areaDomainBaseInfoService.lambdaQuery().in(AreaDomainBaseInfo::getId, param.getSonBaseIds()).select(AreaDomainBaseInfo::getSonAreaId).list();
            // 获取areaDomainBaseInfos中的SonAreaId 转为list 把
            List<Long> sonAreaIds = areaDomainBaseInfos.stream().map(item -> Long.valueOf(item.getSonAreaId().intValue())).collect(Collectors.toList());
            param.setSonIds(sonAreaIds);
        }
        List<SpiderSonArea> spiderSonAreas = spiderSonAreaService.lambdaQuery().in(SpiderSonArea::getId, param.getSonIds()).list();
        QuerySonBaseResult result = new QuerySonBaseResult(spiderSonAreas);
        return Future.succeededFuture(JsonObject.mapFrom(result));
    }

    @Override
    public Future<JsonObject> querySonAreaInfos(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            try {
                QuerySonAreaInfoParam querySonAreaInfoParam = data.mapTo(QuerySonAreaInfoParam.class);
                QuerySonAreaInfoResult querySonAreaInfoResult = spiderSonAreaService.querySonAreaInfos(querySonAreaInfoParam);
                promise.complete(JsonObject.mapFrom(querySonAreaInfoResult));
            } catch (Exception e) {
                promise.fail(e);
            }

        });
        return promise.future();
    }

    @Override
    public Future<Void> upsertSonAreaInfo(JsonObject data) {
        Promise<Void> promise = Promise.promise();
        SpiderSonArea sonArea = data.mapTo(SpiderSonArea.class);
        spiderBusinessPool.execute(() -> {
            try {
                spiderSonAreaService.saveOrUpdate(sonArea);
                promise.complete();
            } catch (Exception e) {
                promise.fail(e);
            }
        });
        return promise.future();
    }

    @Override
    public Future<JsonObject> queryDatasource(JsonObject data) {
        QueryDatasourceParam param = data.mapTo(QueryDatasourceParam.class);
        QueryDatasourceResult result = datasourceManager.queryDatasource(param);
        return Future.succeededFuture(JsonObject.mapFrom(result));
    }

    @Override
    public Future<JsonObject> queryDatasourcePage(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        QueryDatasourceParam param = data.mapTo(QueryDatasourceParam.class);
        spiderBusinessPool.execute(() -> {
            try {
                QueryDatasourceResult datasourceResult = datasourceManager.queryDatasourceResultPage(param);
                promise.complete(JsonObject.mapFrom(datasourceResult));
            } catch (Exception e) {
                promise.fail(e);
            }
        });
        return promise.future();
    }

    @Override
    public Future<JsonObject> queryTableInfo(JsonObject data) {
        QueryTableInfoParam param = data.mapTo(QueryTableInfoParam.class);
        QueryTableInfoResult result = datasourceManager.queryTableInfos(param);
        return Future.succeededFuture(JsonObject.mapFrom(result));
    }

    @Override
    public Future<Void> upsertDatasource(JsonObject data) {
        Promise<Void> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            datasourceManager.upsertDatasource(data.mapTo(AreaDatasourceInfo.class));
            promise.complete();
        });
        return promise.future();
    }

    @Override
    public Future<JsonObject> querySonDomainVersion(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            try {
                QuerySonAreaVersionParam param = data.mapTo(QuerySonAreaVersionParam.class);
                QuerySonAreaVersionResultV2 result = areaDomainBaseInfoService.querySonAreaBaseV2(param);
                promise.complete(JsonObject.mapFrom(result));
            } catch (Exception e) {
                promise.fail(e);
                log.error("querySonDomainVersion error", e);
            }
        });
        return promise.future();
    }

    @Override
    public Future<JsonObject> updateSonDomainField(JsonObject data) {
        UpdateBaseFieldParam param = JSON.parseObject(data.toString(), UpdateBaseFieldParam.class);
        areaDomainBaseInfoService.lambdaUpdate()
                .set(AreaDomainBaseInfo::getDomainObject, param.getTableFieldInfos())
                .eq(AreaDomainBaseInfo::getId, param.getId())
                .update();
        return Future.succeededFuture();
    }

    @Override
    public Future<JsonObject> querySonAreaBaseV2(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            QuerySonAreaVersionParam param = data.mapTo(QuerySonAreaVersionParam.class);
            QuerySonAreaVersionResultV2 result = areaDomainBaseInfoService.querySonAreaBaseV2(param);
            promise.complete(JsonObject.mapFrom(result));
        });
        return promise.future();
    }

}
