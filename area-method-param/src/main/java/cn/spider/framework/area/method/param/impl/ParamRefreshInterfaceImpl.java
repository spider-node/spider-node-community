package cn.spider.framework.area.method.param.impl;

import cn.spider.framework.area.method.param.analysis.ParamRefreshManager;
import cn.spider.framework.param.result.build.interfaces.ParamRefreshInterface;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public class ParamRefreshInterfaceImpl implements ParamRefreshInterface {

    private ParamRefreshManager paramRefreshManager;

    public ParamRefreshInterfaceImpl(ParamRefreshManager paramRefreshManager) {
        this.paramRefreshManager = paramRefreshManager;
    }

    @Override
    public Future<Void> refreshMethod(JsonObject param) {
        return paramRefreshManager.refreshMethod(param.getString("url"),param.getString("classPath"));
    }
}
