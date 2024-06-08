package cn.spider.framework.area.method.param.analysis;

import cn.spider.framework.domain.sdk.data.RefreshAreaModel;
import cn.spider.framework.domain.sdk.data.RefreshAreaParam;
import cn.spider.framework.domain.sdk.interfaces.NodeInterface;
import cn.spider.framework.param.result.build.analysis.AnalysisClass;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParamRefreshManager {
    // 输出class信息
    private AnalysisClass analysisClass;

    private NodeInterface nodeInterface;

    public ParamRefreshManager(AnalysisClass analysisClass, NodeInterface nodeInterface) {
        this.analysisClass = analysisClass;
        this.nodeInterface = nodeInterface;
    }

    /**
     * 刷新域功能的对应参数
     */
    public Future<Void> refreshMethod(String url, String classPath) {
        Promise<Void> promise = Promise.promise();
        try {
            Map<String, Map<String, Object>> refreshAreaParam = analysisClass.buildParam(url,classPath);
            List<RefreshAreaModel> areaModelList = new ArrayList<>();
            refreshAreaParam.forEach((key, value) -> {
                String[] assembly = key.split("@");
                RefreshAreaModel refreshAreaModel = new RefreshAreaModel();
                refreshAreaModel.setTaskComponent(assembly[0]);
                refreshAreaModel.setTaskService(assembly[1]);
                refreshAreaModel.setParmMap(value);
                areaModelList.add(refreshAreaModel);
            });
            RefreshAreaParam param = new RefreshAreaParam();
            param.setAreaModelList(areaModelList);
            nodeInterface.refreshParam(JsonObject.mapFrom(param)).onSuccess(suss -> {
                promise.complete();
            }).onFailure(fail -> {
                promise.fail(fail);
            });
        } catch (MalformedURLException e) {
            return Future.failedFuture(e);
        }
        return promise.future();
    }
}
