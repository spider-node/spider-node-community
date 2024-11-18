package cn.spider.framework.linker.client.plugin;

import cn.spider.framework.param.result.build.model.NodeParamInfoBath;

/**
 * 用于-插件与宿主机通信
 */
public interface PluginReport {

    void escalationPlugInParam(NodeParamInfoBath areaFunctionParam);

    void init();
}
