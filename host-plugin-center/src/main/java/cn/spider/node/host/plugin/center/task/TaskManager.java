package cn.spider.node.host.plugin.center.task;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.node.host.plugin.center.application.http.HostApplicationClient;
import cn.spider.node.host.plugin.center.application.http.data.InstallBizParam;
import cn.spider.node.host.plugin.center.application.http.data.UnInstallParam;
import cn.spider.node.host.plugin.center.model.entity.AreaDomainFunctionInfo;
import cn.spider.node.host.plugin.center.model.entity.SpiderApplicationTask;
import cn.spider.node.host.plugin.center.model.entity.SpiderHostApplication;
import cn.spider.node.host.plugin.center.model.entity.SpiderPluginDeployInfo;
import cn.spider.node.host.plugin.center.model.entity.enums.PluginStatus;
import cn.spider.node.host.plugin.center.model.entity.enums.TaskStatus;
import cn.spider.node.host.plugin.center.model.service.IAreaDomainFunctionInfoService;
import cn.spider.node.host.plugin.center.model.service.ISpiderApplicationTaskService;
import cn.spider.node.host.plugin.center.model.service.ISpiderHostApplicationService;
import cn.spider.node.host.plugin.center.model.service.ISpiderPluginDeployInfoService;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableSet;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TaskManager {
    private IAreaDomainFunctionInfoService functionInfoService;

    private HostApplicationClient hostApplicationClient;

    private ISpiderApplicationTaskService taskService;

    private ISpiderHostApplicationService hostApplicationService;

    private ISpiderPluginDeployInfoService deployInfoService;

    public TaskManager(IAreaDomainFunctionInfoService functionInfoService, HostApplicationClient hostApplicationClient, ISpiderApplicationTaskService taskService, ISpiderHostApplicationService hostApplicationService, ISpiderPluginDeployInfoService deployInfoService) {
        this.functionInfoService = functionInfoService;
        this.hostApplicationClient = hostApplicationClient;
        this.taskService = taskService;
        this.hostApplicationService = hostApplicationService;
        this.deployInfoService = deployInfoService;
    }

    // 执行任务
    public void run() {
        List<SpiderApplicationTask> tasks = taskService.lambdaQuery().eq(SpiderApplicationTask::getStatus, TaskStatus.INIT).orderByAsc(SpiderApplicationTask::getId).list();
        if (CollectionUtils.isEmpty(tasks)) {
            return;
        }
        log.info("获取到任务了");
        for (SpiderApplicationTask task : tasks) {
            switch (task.getTaskType()) {
                case INSTALL:
                    installTask(task);
                    break;
                case UNINSTALL:
                    uninstall(task);
                    break;
            }
        }

    }

    /**
     * @param task 执行部署的
     * @return
     */
    private void installTask(SpiderApplicationTask task) {
        AreaDomainFunctionInfo areaDomainFunctionInfo = functionInfoService.getById(task.getTaskBusinessId());
        // 寻找宿主应用
        List<SpiderPluginDeployInfo> spiderPluginDeployInfos = deployInfoService.lambdaQuery()
                .eq(SpiderPluginDeployInfo::getFunctionId, task.getTaskBusinessId())
                .in(SpiderPluginDeployInfo::getStatus, ImmutableSet.of(PluginStatus.ING))
                .list();
        // 已经获取到宿主应用

        // 获取倒-已经部署的ip
        Set<String> ips = spiderPluginDeployInfos.stream().map(SpiderPluginDeployInfo::getIp).collect(Collectors.toSet());
        // 获取倒可部署的ip
        List<SpiderHostApplication> hostApplications = hostApplicationService.lambdaQuery()
                .gt(SpiderHostApplication::getId, 0)
                .list();
        SpiderHostApplication hostApplication = hostApplications.stream().filter(host -> !ips.contains(host.getIp())).findFirst().orElse(null);
        if (Objects.isNull(hostApplication)) {
            log.info("没有获取到宿主应用");
            return;
        }
        log.info("获取到的宿主应用为 {}", JSON.toJSONString(hostApplication));
        // 设置ip倒task中
        task.setIp(hostApplication.getIp());
        // 构建部署的参数信息
        InstallBizParam installBizParam = InstallBizParam.builder()
                .bizName(areaDomainFunctionInfo.getArtifactId())
                .bizVersion(areaDomainFunctionInfo.getVersion())
                .bizUrl(areaDomainFunctionInfo.getBizUrl())
                .build();
        task.setStatus(TaskStatus.ING);
        taskService.updateById(task);
        // 远程跟宿主应用进行交互
        hostApplicationClient.installPlugin(task.getIp(), JsonObject.mapFrom(installBizParam))
                .onSuccess(installSuss -> {
                    task.setStatus(TaskStatus.SUSS);
                    taskService.updateById(task);
                    try {
                        SpiderPluginDeployInfo spiderPluginDeployInfo = SpiderPluginDeployInfo.builder()
                                .functionId(areaDomainFunctionInfo.getId())
                                .ip(task.getIp())
                                .taskComponent(areaDomainFunctionInfo.getTaskComponent())
                                .taskService(areaDomainFunctionInfo.getTaskService())
                                .version(areaDomainFunctionInfo.getVersion())
                                .status(PluginStatus.ING)
                                .build();
                        deployInfoService.save(spiderPluginDeployInfo);
                    } catch (Exception e) {
                        log.warn("部署异常信息 {}", ExceptionMessage.getStackTrace(e));
                        task.setStatus(TaskStatus.FAIL);
                        task.setError(ExceptionMessage.getStackTrace(e));
                        taskService.updateById(task);
                    }
                }).onFailure(installFail -> {
                    task.setStatus(TaskStatus.FAIL);
                    task.setError(ExceptionMessage.getStackTrace(installFail));
                    taskService.updateById(task);
                });
    }

    /**
     * 跟宿主应用交互，卸载插件
     *
     * @param task
     */
    public void uninstall(SpiderApplicationTask task) {
        // 获取倒插件信息
        AreaDomainFunctionInfo areaDomainFunctionInfo = functionInfoService.getById(task.getTaskBusinessId());
        // 获取部署的插件信息
        SpiderPluginDeployInfo deployInfo = deployInfoService.lambdaQuery()
                .eq(SpiderPluginDeployInfo::getIp, task.getId())
                .eq(SpiderPluginDeployInfo::getFunctionId, task.getTaskBusinessId())
                .eq(SpiderPluginDeployInfo::getStatus, PluginStatus.ING)
                .one();

        UnInstallParam unInstallParam = UnInstallParam.builder()
                .bizName(areaDomainFunctionInfo.getArtifactId())
                .bizVersion(areaDomainFunctionInfo.getVersion())
                .build();
        hostApplicationClient.unInstall(task.getIp(), JsonObject.mapFrom(unInstallParam))
                .onSuccess(suss -> {
                    task.setStatus(TaskStatus.SUSS);
                    deployInfoService.removeById(deployInfo.getId());
                    deployInfoService.updateById(deployInfo);
                    taskService.updateById(task);
                }).onFailure(fail -> {
                    task.setStatus(TaskStatus.FAIL);
                    task.setError(ExceptionMessage.getStackTrace(fail));
                    taskService.updateById(task);
                });
    }
}
