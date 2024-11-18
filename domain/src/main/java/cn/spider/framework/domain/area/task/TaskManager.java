package cn.spider.framework.domain.area.task;
import cn.spider.framework.domain.area.agent.AgentVertxClient;
import cn.spider.framework.domain.area.node.entity.SpiderAreaFunction;
import cn.spider.framework.domain.area.node.entity.SpiderAreaFunctionVersion;
import cn.spider.framework.domain.area.node.service.ISpiderAreaFunctionService;
import cn.spider.framework.domain.area.node.service.ISpiderAreaFunctionVersionService;
import cn.spider.framework.domain.area.sondomain.entity.AreaDomainBaseInfo;
import cn.spider.framework.domain.area.sondomain.entity.SpiderSonArea;
import cn.spider.framework.domain.area.sondomain.service.IAreaDomainBaseInfoService;
import cn.spider.framework.domain.area.sondomain.service.ISpiderSonAreaService;
import cn.spider.framework.domain.area.task.data.CreateCoderParam;
import cn.spider.framework.domain.area.task.data.enums.TaskStatus;
import cn.spider.framework.domain.area.task.data.enums.TaskType;
import cn.spider.framework.domain.area.task.entity.SpiderDomainFunctionTask;
import cn.spider.framework.domain.area.task.service.ISpiderDomainFunctionTaskService;
import com.alibaba.fastjson.JSON;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * ai任务的管理类
 */
@Slf4j
public class TaskManager {
    /**
     * 功能版本的service
     */
    private ISpiderAreaFunctionVersionService spiderAreaFunctionVersionService;

    /**
     * 领域功能的service
     */
    private ISpiderAreaFunctionService spiderAreaFunctionService;

    /**
     * 子域的service
     */
    private ISpiderSonAreaService spiderSonAreaService;

    /**
     * 子域的基础代码信息
     */
    private IAreaDomainBaseInfoService baseInfoService;

    private ISpiderDomainFunctionTaskService spiderDomainFunctionTaskService;

    private AgentVertxClient agentVertxClient;

    public TaskManager(ISpiderAreaFunctionVersionService spiderAreaFunctionVersionService,
                       ISpiderAreaFunctionService spiderAreaFunctionService,
                       ISpiderSonAreaService spiderSonAreaService,
                       IAreaDomainBaseInfoService baseInfoService,
                       ISpiderDomainFunctionTaskService spiderDomainFunctionTaskService,
                       AgentVertxClient agentVertxClient) {
        this.spiderAreaFunctionVersionService = spiderAreaFunctionVersionService;
        this.spiderAreaFunctionService = spiderAreaFunctionService;
        this.spiderSonAreaService = spiderSonAreaService;
        this.baseInfoService = baseInfoService;
        this.spiderDomainFunctionTaskService = spiderDomainFunctionTaskService;
        this.agentVertxClient = agentVertxClient;
    }

    // 新增领域功能的任务
    public void runDomainFunctionTask(String versionId) {
        SpiderAreaFunctionVersion functionVersion = spiderAreaFunctionVersionService.getById(versionId);
        SpiderSonArea sonArea = spiderSonAreaService.getById(functionVersion.getSonDomainId());
        String datasource = sonArea.getDatasource();
        AreaDomainBaseInfo areaDomainBaseInfo = baseInfoService.lambdaQuery()
                .eq(AreaDomainBaseInfo::getSonAreaId, sonArea.getId())
                .eq(AreaDomainBaseInfo::getVersion, functionVersion.getSonDomainVersion()).one();
        SpiderAreaFunction areaFunction = spiderAreaFunctionService.getById(functionVersion.getDomainFunctionId());
        String projectName = areaFunction.getName() + "_" + functionVersion.getVersion();
        CreateCoderParam createCoderParam = new CreateCoderParam(projectName, JsonObject.mapFrom(areaDomainBaseInfo), datasource, functionVersion.getFunctionFunctional().getFunctionalList());
        // 创建任务
        SpiderDomainFunctionTask domainFunctionTask = new SpiderDomainFunctionTask();
        domainFunctionTask.setDomainFunctionVersionId(functionVersion.getId());
        domainFunctionTask.setDomainFunctionId(functionVersion.getDomainFunctionId());
        domainFunctionTask.setTaskDomainId(sonArea.getAreaId());
        domainFunctionTask.setTaskSonDomainId(sonArea.getId());
        domainFunctionTask.setTaskType(TaskType.NEWLY_ADDED);
        domainFunctionTask.setStatus(TaskStatus.DATA_INIT);
        spiderDomainFunctionTaskService.save(domainFunctionTask);
        // 发起跟ai交互
        createCoderParam.setTaskId(domainFunctionTask.getId());
        createCoderParam.setBaseInfoId(areaDomainBaseInfo.getId());
        log.info("create_coder_info", JSON.toJSONString(createCoderParam));
        agentVertxClient.createCoder(JsonObject.mapFrom(createCoderParam));
    }
}
