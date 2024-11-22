package cn.spider.node.host.plugin.center.application;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.node.host.plugin.center.model.entity.AreaDomainFunctionInfo;
import cn.spider.node.host.plugin.center.model.entity.SpiderApplicationTask;
import cn.spider.node.host.plugin.center.model.entity.SpiderHostApplication;
import cn.spider.node.host.plugin.center.model.entity.SpiderPluginDeployInfo;
import cn.spider.node.host.plugin.center.model.entity.enums.PluginStatus;
import cn.spider.node.host.plugin.center.model.entity.enums.TaskStatus;
import cn.spider.node.host.plugin.center.model.entity.enums.TaskType;
import cn.spider.node.host.plugin.center.model.service.IAreaDomainFunctionInfoService;
import cn.spider.node.host.plugin.center.model.service.ISpiderApplicationTaskService;
import cn.spider.node.host.plugin.center.model.service.ISpiderHostApplicationService;
import cn.spider.node.host.plugin.center.model.service.ISpiderPluginDeployInfoService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class HostApplicationManager {

    @Resource
    private ISpiderHostApplicationService hostApplicationService;

    @Resource
    private ISpiderPluginDeployInfoService deployInfoService;

    @Resource
    private ISpiderApplicationTaskService taskService;

    @Resource
    private IAreaDomainFunctionInfoService infoService;

    // 注册 宿主应用 直接新增
    public void online(String ip) {
        SpiderHostApplication hostApplication = SpiderHostApplication.builder()
                .ip(ip)
                .build();
        SpiderHostApplication hostApplication1 = hostApplicationService.lambdaQuery().eq(SpiderHostApplication::getIp, ip).one();
        if (Objects.nonNull(hostApplication1)) {
            return;
        }
        try {
            hostApplicationService.save(hostApplication);
        } catch (Exception e) {
            log.warn("应用上线出现异常 {}", ExceptionMessage.getStackTrace(e));
        }
    }

    // 卸载 宿主应用
    public void offline(String ip) {
        List<SpiderPluginDeployInfo> pluginDeployInfos = deployInfoService.lambdaQuery().eq(SpiderPluginDeployInfo::getIp, ip).list();
        if (CollectionUtils.isEmpty(pluginDeployInfos)) {
            return;
        }
        List<SpiderApplicationTask> tasks = new ArrayList<>();
        for (SpiderPluginDeployInfo info : pluginDeployInfos) {
            SpiderApplicationTask task = SpiderApplicationTask.builder()
                    .taskBusinessId(info.getFunctionId())
                    .taskType(TaskType.UNINSTALL)
                    .ip(info.getIp())
                    .status(TaskStatus.INIT)
                    .build();
            tasks.add(task);
        }
        taskService.saveBatch(tasks);
    }

    // 申请上线插件
    public void applyOnlinePlugin(Integer functionId) {
        AreaDomainFunctionInfo areaDomainFunctionInfo = infoService.getById(functionId);
        if (Objects.isNull(areaDomainFunctionInfo)) {
            Preconditions.checkArgument(false, "插件应用不存在，请校验");
            return;
        }
        List<SpiderApplicationTask> tasks = new ArrayList<>();
        for (int i = 0; i < areaDomainFunctionInfo.getInstanceNum(); i++) {
            SpiderApplicationTask task = SpiderApplicationTask.builder()
                    .taskBusinessId(functionId)
                    .taskType(TaskType.INSTALL)
                    .status(TaskStatus.INIT)
                    .build();
            tasks.add(task);
        }
        taskService.saveBatch(tasks);
    }

    // 申请卸载插件
    public void applyOfflinePlugin(Integer functionId) {
        List<SpiderPluginDeployInfo> spiderPluginDeployInfos = deployInfoService.lambdaQuery()
                .eq(SpiderPluginDeployInfo::getFunctionId, functionId)
                .eq(SpiderPluginDeployInfo::getStatus, PluginStatus.ING)
                .list();

        if (CollectionUtils.isEmpty(spiderPluginDeployInfos)) {
            Preconditions.checkArgument(false, "部署的插件不存在,请检查");
        }
        List<SpiderApplicationTask> tasks = new ArrayList<>();
        for (SpiderPluginDeployInfo spiderPluginDeployInfo : spiderPluginDeployInfos) {
            SpiderApplicationTask task = SpiderApplicationTask.builder()
                    .taskBusinessId(functionId)
                    .taskType(TaskType.UNINSTALL)
                    .status(TaskStatus.INIT)
                    .ip(spiderPluginDeployInfo.getIp())
                    .build();
            tasks.add(task);
        }
        taskService.saveBatch(tasks);
    }

    public AreaDomainFunctionInfo queryFunctionInfo(String taskComponent, String taskService,String domainFunctionVersionId) {
        AreaDomainFunctionInfo functionInfo = infoService.lambdaQuery()
                .eq(StringUtils.isNotEmpty(taskComponent),AreaDomainFunctionInfo::getTaskComponent, taskComponent)
                .eq(StringUtils.isNotEmpty(taskService),AreaDomainFunctionInfo::getTaskService, taskService)
                .eq(AreaDomainFunctionInfo::getDomainFunctionVersionId, domainFunctionVersionId)
                .one();
        if (Objects.isNull(functionInfo)) {
            return null;
        }
        return functionInfo;
    }
}
