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
import cn.spider.node.host.plugin.center.sdk.data.CheckDeployParam;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HostApplicationManager {

    @Autowired
    private ISpiderHostApplicationService hostApplicationService;

    @Autowired
    private ISpiderPluginDeployInfoService deployInfoService;

    @Autowired
    private ISpiderApplicationTaskService taskService;

    @Autowired
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

    // 基于ip下线服务
    public void deleteSpiderApplicationTask(String ip){
        taskService.lambdaUpdate().eq(SpiderApplicationTask::getIp, ip).remove();
    }

    // 使用deployInfoService 基于ip进行删除数据
    public void deletePluginDeployInfo(String ip) {
        deployInfoService.lambdaUpdate().eq(SpiderPluginDeployInfo::getIp, ip).remove();
    }

    public void deleteApplicationHost(String ip) {
        hostApplicationService.lambdaUpdate().eq(SpiderHostApplication::getIp, ip).remove();
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

    public AreaDomainFunctionInfo queryFunctionInfo(String taskComponent, String taskService, String domainFunctionVersionId) {
        AreaDomainFunctionInfo functionInfo = infoService.lambdaQuery()
                .eq(StringUtils.isNotEmpty(taskComponent), AreaDomainFunctionInfo::getTaskComponent, taskComponent)
                .eq(StringUtils.isNotEmpty(taskService), AreaDomainFunctionInfo::getTaskService, taskService)
                .eq(StringUtils.isNotEmpty(domainFunctionVersionId), AreaDomainFunctionInfo::getDomainFunctionVersionId, domainFunctionVersionId)
                .one();
        if (Objects.isNull(functionInfo)) {
            return new AreaDomainFunctionInfo();
        }
        return functionInfo;
    }

    public List<SpiderPluginDeployInfo> queryDeployInfo(String domainFunctionVersionId) {
        return deployInfoService.lambdaQuery().eq(SpiderPluginDeployInfo::getDomainFunctionVersionId, domainFunctionVersionId).list();
    }

    public void checkDeployInfo(CheckDeployParam param) {
        List<SpiderPluginDeployInfo> spiderPluginDeployInfos = deployInfoService.lambdaQuery().in(SpiderPluginDeployInfo::getDomainFunctionVersionId, param.getDomainVersionIds()).list();
        // 把spiderPluginDeployInfos 基于domainFunctionVersionId 进行分组
        Map<String, List<SpiderPluginDeployInfo>> deployInfoMap = spiderPluginDeployInfos.stream().collect(Collectors.groupingBy(SpiderPluginDeployInfo::getDomainFunctionVersionId));
        // 遍历 param中的domainVersionIds
        Set<String> versionIds = param.getDomainVersionIds().stream().filter(item->!deployInfoMap.containsKey(item)).collect(Collectors.toSet());
        if(CollectionUtils.isEmpty(versionIds)){
            return;
        }
        List<AreaDomainFunctionInfo> areaDomainFunctionInfos = infoService.lambdaQuery().in(AreaDomainFunctionInfo::getDomainFunctionVersionId, versionIds).list();
        if(CollectionUtils.isEmpty(areaDomainFunctionInfos)){
            return;
        }
        // 申请上线
        for (AreaDomainFunctionInfo areaDomainFunctionInfo : areaDomainFunctionInfos) {
            applyOnlinePlugin(areaDomainFunctionInfo.getId());
        }
    }
}
