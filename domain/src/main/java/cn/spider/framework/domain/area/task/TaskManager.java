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
        // 判断下状态，如果是编译通过，就不能进行代码生成
        SpiderAreaFunctionVersion functionVersion = spiderAreaFunctionVersionService.getById(versionId);
        SpiderSonArea sonArea = spiderSonAreaService.getById(functionVersion.getSonDomainId());
        String datasource = sonArea.getDatasource();
        AreaDomainBaseInfo areaDomainBaseInfo = baseInfoService.lambdaQuery()
                .eq(AreaDomainBaseInfo::getSonAreaId, sonArea.getId())
                .eq(AreaDomainBaseInfo::getVersion, functionVersion.getSonDomainVersion()).one();
        SpiderAreaFunction areaFunction = spiderAreaFunctionService.getById(functionVersion.getDomainFunctionId());
        String projectName = areaFunction.getName() + "_" + functionVersion.getVersion();
        JsonObject domainBaseInfo = JsonObject.mapFrom(areaDomainBaseInfo);
        domainBaseInfo.put("domainObjectName",firstLowerCase(areaDomainBaseInfo.getDomainObjectEntityName()));
        domainBaseInfo.put("taskComponent",firstLowerCase(areaFunction.getTaskComponent()));
        domainBaseInfo.put("taskService",firstLowerCase(areaFunction.getTaskService()));

        CreateCoderParam createCoderParam = new CreateCoderParam(projectName, domainBaseInfo, datasource, functionVersion.getFunctionFunctional().getFunctionalList());
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
        createCoderParam.setDomainFunctionVersionId(versionId);
        log.info("create_coder_info {}", JSON.toJSONString(createCoderParam));
        agentVertxClient.createCoder(JsonObject.mapFrom(createCoderParam));
    }

    /**
     * 新增工具一个方法，支持把一个英文首字母变小写
     */
    private String firstLowerCase(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }


}
