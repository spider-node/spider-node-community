package cn.spider.framework.area.method.param.analysis;

import cn.spider.framework.domain.sdk.data.RefreshAreaModel;
import cn.spider.framework.domain.sdk.data.RefreshAreaParam;
import cn.spider.framework.domain.sdk.interfaces.AreaInterface;
import cn.spider.framework.domain.sdk.interfaces.NodeInterface;
import cn.spider.framework.param.result.build.analysis.AnalysisClass;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParamRefreshManager {
    // 输出class信息
    private AnalysisClass analysisClass;

    private NodeInterface nodeInterface;

    private AreaInterface areaInterface;

    public ParamRefreshManager(AnalysisClass analysisClass, NodeInterface nodeInterface,AreaInterface areaInterface) {
        this.analysisClass = analysisClass;
        this.nodeInterface = nodeInterface;
        this.areaInterface = areaInterface;
    }

    /**
     * 刷新域功能的对应参数
     */
    public Future<Void> refreshMethod(String url, String classPath) {
        Promise<Void> promise = Promise.promise();
        // 查询领域信息
        JsonObject queryAreaParam = new JsonObject();
        queryAreaParam.put("classPath", classPath);
        queryAreaParam.put("sdkUrl", url);
        queryAreaParam.put("page", 1);
        queryAreaParam.put("size", 20);

        areaInterface.queryArea(queryAreaParam).onSuccess(areaSuss -> {
            JsonObject areas = areaSuss;
            JsonArray AreaArray = areas.getJsonArray("areaModes");
            // 获取到area数据
            JsonObject area = AreaArray.getJsonObject(0);
            RefreshAreaParam param = new RefreshAreaParam();
            try {
                Map<String, Map<String, Object>> refreshAreaParam = analysisClass.buildParam(url, classPath);
                List<RefreshAreaModel> areaModelList = new ArrayList<>();
                refreshAreaParam.forEach((key, value) -> {
                    String[] assembly = key.split("@");
                    RefreshAreaModel refreshAreaModel = new RefreshAreaModel();
                    refreshAreaModel.setTaskComponent(assembly[0]);
                    refreshAreaModel.setTaskService(assembly[1]);
                    refreshAreaModel.setParmMap(value);
                    refreshAreaModel.setAreaId(area.getString("id"));
                    areaModelList.add(refreshAreaModel);
                });
                param.setAreaModelList(areaModelList);
            } catch (Exception e) {
                promise.fail(e);
            }
            nodeInterface.refreshParam(JsonObject.mapFrom(param)).onSuccess(suss -> {
                promise.complete();
            }).onFailure(fail -> {
                promise.fail(fail);
            });

        }).onFailure(fail -> {
            promise.fail(fail);
        });
        return promise.future();
    }
}
