package cn.spider.framework.linker.client.config;

import cn.spider.framework.linker.client.escalation.PluginEscalation;
import cn.spider.framework.linker.client.escalation.PluginReportImpl;
import cn.spider.framework.linker.client.plugin.PluginReport;
import cn.spider.framework.param.result.build.analysis.SpiderPluginManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

public class PluginConfig {

    @Bean
    public PluginReport buildPluginReport(ApplicationContext context) {
        return new PluginReportImpl(context);
    }

    @Bean
    public SpiderPluginManager spiderPluginManager(ApplicationContext applicationContext, @Value("${spring.application.name}") String bizName,
                                                   @Value("${spider.pom.version}") String version,
                                                   @Value("${spider.taskId}") String taskId) {
        return new SpiderPluginManager(applicationContext, bizName, version, taskId);
    }

    @Bean
    public PluginEscalation buildPluginEscalation(PluginReport pluginReport, SpiderPluginManager spiderPluginManager,
                                                  @Value("${spider.pom.artifactId}") String moduleName,
                                                  @Value("${spider.pom.version}") String moduleVersion) {
        return new PluginEscalation(pluginReport, spiderPluginManager, moduleName, moduleVersion);
    }
}
