package cn.spider.framework.linker.client.escalation;
import cn.spider.framework.linker.client.plugin.EscalationInfo;
import cn.spider.framework.linker.client.plugin.PluginReport;
import cn.spider.framework.param.result.build.analysis.SpiderPluginManager;
import cn.spider.framework.param.result.build.model.NodeParamInfoBath;
import cn.spider.framework.param.result.build.model.SpiderPlugin;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 领域功能插件信息的上报
 */
public class PluginEscalation {
    private PluginReport pluginReport;

    private SpiderPluginManager spiderPluginManager;

    private String moduleName;

    private String moduleVersion;


    public PluginEscalation(PluginReport pluginReport, SpiderPluginManager spiderPluginManager, String moduleName, String moduleVersion) {
        this.pluginReport = pluginReport;
        this.spiderPluginManager = spiderPluginManager;
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
    }

    /**
     * 上报所有信息到spider-node
     */
    public void escalationAreaPluginToBase() {
        List<SpiderPlugin> spiderPlugins = spiderPluginManager.allPlugin();
        for (SpiderPlugin spiderPlugin : spiderPlugins) {
            EscalationInfo escalationInfo = new EscalationInfo();
            escalationInfo.setModuleName(this.moduleName);
            escalationInfo.setModuleVersion(this.moduleVersion);
            escalationInfo.setComponentName(spiderPlugin.getComponentName());
            escalationInfo.setServiceName(spiderPlugin.getServiceName());
            escalationInfo.setMethodName(spiderPlugin.getMethodName());
        }
        // 上传参数信息
        NodeParamInfoBath refreshAreaParam = spiderPluginManager.getNodeParamInfoBath();
        pluginReport.escalationPlugInParam(refreshAreaParam);
    }
}
