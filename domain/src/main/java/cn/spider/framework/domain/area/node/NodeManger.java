package cn.spider.framework.domain.area.node;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.domain.area.AreaManger;
import cn.spider.framework.domain.area.data.AreaModel;
import cn.spider.framework.domain.area.data.QueryAreaModel;
import cn.spider.framework.domain.area.node.data.*;
import cn.spider.framework.domain.area.node.data.enums.NodeStatus;
import cn.spider.framework.domain.area.node.data.enums.ServiceTaskType;
import cn.spider.framework.domain.area.node.entity.SpiderAreaFunction;
import cn.spider.framework.domain.area.node.entity.SpiderAreaFunctionVersion;
import cn.spider.framework.domain.area.node.service.ISpiderAreaFunctionService;
import cn.spider.framework.domain.area.node.service.ISpiderAreaFunctionVersionService;
import cn.spider.framework.domain.area.task.service.ISpiderDomainFunctionTaskService;
import cn.spider.framework.domain.sdk.data.FunctionInfo;
import cn.spider.framework.domain.sdk.data.ParamPack;
import cn.spider.framework.domain.sdk.data.RefreshAreaModel;
import cn.spider.framework.param.result.build.model.NodeParamInfo;
import cn.spider.framework.param.result.build.model.NodeParamInfoBath;
import cn.spider.framework.param.result.build.model.ReportParamInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import cn.spider.framework.domain.area.task.entity.SpiderDomainFunctionTask;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.node
 * @Author: dengdongsheng
 * @CreateTime: 2023-09-04  22:54
 * @Description: 节点管理
 * @Version: 1.0
 */
@Slf4j
public class NodeManger {

    // 操作mysql的client
    private MySQLPool client;

    private AreaManger areaManger;

    private ISpiderAreaFunctionVersionService spiderAreaFunctionVersionService;

    private ISpiderAreaFunctionService spiderAreaFunctionService;

    private ISpiderDomainFunctionTaskService spiderDomainFunctionTaskService;

    public NodeManger(MySQLPool client, AreaManger areaManger, ISpiderAreaFunctionService spiderAreaFunctionService,ISpiderAreaFunctionVersionService spiderAreaFunctionVersionService,ISpiderDomainFunctionTaskService spiderDomainFunctionTaskService) {
        this.client = client;
        this.areaManger = areaManger;
        this.spiderAreaFunctionService = spiderAreaFunctionService;
        this.spiderAreaFunctionVersionService = spiderAreaFunctionVersionService;
        this.spiderDomainFunctionTaskService = spiderDomainFunctionTaskService;
    }

    private RowMapper<Node> ROW_BUSINESS = row -> {
        Node node = new Node();
        node.setId(row.getString("id"));
        node.setName(row.getString("name"));
        node.setDesc(row.getString("desc"));
        node.setAsync(row.getBoolean("async"));
        node.setTaskComponent(row.getString("task_component"));
        node.setTaskService(row.getString("task_service"));
        node.setStatus(NodeStatus.valueOf(row.getString("status")));
        node.setServiceTaskType(ServiceTaskType.valueOf(row.getString("service_task_type")));
        node.setAreaId(row.getString("area_id"));
        node.setAreaName(row.getString("area_name"));
        node.setTaskMethod(row.getString("task_method"));
        node.setWorkerId(row.getString("worker_id"));
        String resultMapping = row.getString("result_mapping");
        node.setResultMapping(StringUtils.isNotEmpty(resultMapping) ? new JsonObject(resultMapping) : new JsonObject());
        String paramMapping = row.getString("param_mapping");
        node.setParamMapping(StringUtils.isNotEmpty(paramMapping) ? new JsonObject(paramMapping) : new JsonObject());
        return node;
    };

    // 新增节点
    public Future<Void> createNode(Node node) {
        node.setStatus(NodeStatus.START);
        Promise<Void> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();
        node.setId(UUID.randomUUID().toString());
        // 根据领域id查询领域名称
        QueryAreaModel queryAreaModel = new QueryAreaModel();
        queryAreaModel.setPage(1);
        queryAreaModel.setSize(10);
        // queryAreaModel.setId(node.getAreaId());
        areaManger.queryAreaModel(queryAreaModel).onSuccess(suss -> {
            List<AreaModel> areaModels = suss;
            AreaModel areaModel = areaModels.get(0);
            node.setAreaName(areaModel.getAreaName());
            // 新增域功能入参，出参，方法名称，服务标识
            sql.append("insert into spider_area_function (`id`,`name`,`desc`,`async`,`task_component`,`task_service`,`service_task_type`,`status`,area_id,area_name,param_mapping,result_mapping,task_method,worker_id) values (#{id},#{name},#{desc},#{async},#{taskComponent},#{taskService},#{serviceTaskType},#{status},#{areaId},#{areaName},#{paramMapping},#{resultMapping},#{taskMethod},#{workerId})");
            JsonObject param = JsonObject.mapFrom(node);
            param.put("paramMapping", Objects.isNull(node.getParamMapping()) ? null : node.getParamMapping().toString());
            param.put("resultMapping", Objects.isNull(node.getResultMapping()) ? null : node.getResultMapping().toString());
            Map<String, Object> parameters = param.getMap();
            SqlTemplate
                    .forUpdate(client, sql.toString())
                    .execute(parameters)
                    .onSuccess(function -> {
                        promise.complete();
                    }).onFailure(fail -> {
                        log.info("----执行异常 {}", ExceptionMessage.getStackTrace(fail));
                        promise.fail(fail);
                    });
        }).onFailure(fail -> {
            promise.fail(fail);
            log.info("----执行异常 {}", ExceptionMessage.getStackTrace(fail));
        });

        return promise.future();
    }

    // 编辑节点
    public Future<Void> updateNode(Node node) {
        Promise<Void> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();
        sql.append("update spider_area_function set name = #{name},`desc` = #{desc},task_component = #{taskComponent},task_service = #{taskService},service_task_type = #{serviceTaskType},`status` = #{status},param_mapping = #{paramMapping}, result_mapping = #{resultMapping},task_method = #{taskMethod},worker_id = #{workerId} where id = #{id}");
        JsonObject param = JsonObject.mapFrom(node);
        param.put("paramMapping", node.getParamMapping().toString());
        param.put("resultMapping", node.getResultMapping().toString());
        Map<String, Object> parameters = param.getMap();
        SqlTemplate
                .forUpdate(client, sql.toString())
                .execute(parameters)
                .onSuccess(function -> {
                    promise.complete();
                }).onFailure(fail -> {
                    promise.fail(fail);
                    log.error("更新失败{}", ExceptionMessage.getStackTrace(fail));
                });
        return promise.future();
    }
    // 查询节点

    public Future<List<Node>> queryNode(QueryNodeParam queryNodeParam) {

        Promise<List<Node>> promise = Promise.promise();

        String sql = buildQuerySql(queryNodeParam);

        queryNodeParam.setPage((queryNodeParam.getPage() - 1) * queryNodeParam.getSize());

        JsonObject param = JsonObject.mapFrom(queryNodeParam);
        Map<String, Object> parameters = param.getMap();

        SqlTemplate
                .forQuery(client, sql.toString())
                .mapTo(ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(users -> {
                    RowSet<Node> function = users;
                    List<Node> nodes = Lists.newArrayList();
                    function.forEach(item -> {
                        nodes.add(item);
                    });
                    promise.complete(nodes);
                }).onFailure(fail -> {
                    log.info("查询数据失败 {}", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

    private String buildQuerySql(QueryNodeParam param) {
        StringBuilder querySql = new StringBuilder();
        querySql.append("select * from spider_area_function where 1=1 ");
        if (StringUtils.isNotEmpty(param.getId())) {
            querySql.append(" and id = #{id}");
        }
        if (StringUtils.isNotEmpty(param.getName())) {
            querySql.append(" and name = #{name}");
        }
        if (StringUtils.isNotEmpty(param.getAreaId())) {
            querySql.append(" and area_id = #{areaId}");

        }

        if (Objects.nonNull(param.getStatus())) {
            querySql.append(" and `status` = #{status} ");
        }

        if (StringUtils.isNotEmpty(param.getAreaName())) {
            querySql.append(" and area_name = #{areaName}");
        }

        if (StringUtils.isNotEmpty(param.getTaskComponent())) {
            querySql.append(" and task_component = #{taskComponent}");
        }

        if (StringUtils.isNotEmpty(param.getTaskService())) {
            querySql.append(" and task_service = #{taskService}");
        }

        querySql.append(" order by create_time limit #{page},#{size}");
        return querySql.toString();
    }

    public Future<Node> queryNodeByComTaskService(String taskComponent, String taskService) {
        Promise<Node> promise = Promise.promise();
        String sql = "select * from spider_area_function where task_component = #{taskComponent} and task_service = #{taskService}";
        JsonObject param = new JsonObject();
        Map<String, Object> parameters = param.getMap();
        parameters.put("taskComponent", taskComponent);
        parameters.put("taskService", taskService);
        SqlTemplate
                .forQuery(client, sql.toString())
                .mapTo(ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(users -> {
                    RowSet<Node> function = users;
                    List<Node> nodes = Lists.newArrayList();
                    function.forEach(item -> {
                        nodes.add(item);
                    });
                    if (CollectionUtils.isEmpty(nodes)) {
                        promise.complete();
                        return;
                    }
                    promise.complete(nodes.get(0));
                });
        return promise.future();
    }

    /**
     * 批量更新域的参数信息
     */
    public void refreshNodeParam(ReportParamInfo areaParam) {
        List<NodeParamInfoBath> areaModelList = areaParam.getNodeParamInfoBathList();
        if (CollectionUtils.isEmpty(areaModelList)) {
            return;
        }
        for (NodeParamInfoBath areaModel : areaModelList) {
            // 获取areaModel.getNodeParamInfoList()中的taskId转成set
            // 把areaModel.getNodeParamInfoList() 转成map taskId为key,并且把taskId转为Integer作为key
            Set<Integer> taskIds = areaModel.getNodeParamInfoList().stream().map(item-> Integer.parseInt(item.getTaskId())).collect(Collectors.toSet());
            List<SpiderDomainFunctionTask> spiderDomainFunctionTasks = spiderDomainFunctionTaskService.lambdaQuery().in(SpiderDomainFunctionTask::getId, taskIds).list();
            // 基于domainFunctionVersionId为可以spiderDomainFunctionTasks转map
            Map<Integer, SpiderDomainFunctionTask> spiderDomainFunctionTaskMap = spiderDomainFunctionTasks
                    .stream()
                    .collect(Collectors.toMap(SpiderDomainFunctionTask::getId, Function.identity()));

            // 获取spiderDomainFunctionTasks中的 domainFunctionVersionId
            Set<String> domainFunctionVersionIds = spiderDomainFunctionTasks.stream().map(SpiderDomainFunctionTask::getDomainFunctionVersionId).collect(Collectors.toSet());
            // 获取获取spiderDomainFunctionTasks中的对应的domainFunctionId
            List<SpiderAreaFunctionVersion> spiderAreaFunctionVersions = spiderAreaFunctionVersionService.lambdaQuery().in(SpiderAreaFunctionVersion::getId, domainFunctionVersionIds).list();
            // 把spiderAreaFunctionVersions转成map id 为key value为SpiderAreaFunctionVersion
            Map<String, SpiderAreaFunctionVersion> spiderAreaFunctionVersionMap = spiderAreaFunctionVersions
                    .stream()
                    .collect(Collectors.toMap(SpiderAreaFunctionVersion::getId, Function.identity()));
            // 获取areaModel.getNodeParamInfoList()中的taskServervice 转成set
            Set<String> taskServices = areaModel.getNodeParamInfoList().stream().map(NodeParamInfo::getTaskService).collect(Collectors.toSet());
            // 获取areaModel.getNodeParamInfoList()中的taskComponent 转成set
            Set<String> taskComponents = areaModel.getNodeParamInfoList().stream().map(NodeParamInfo::getTaskComponent).collect(Collectors.toSet());
            // 查询出所有的taskService 和 taskComponent的 spiderAreaFunction
            List<SpiderAreaFunction> spiderAreaFunctions = spiderAreaFunctionService.lambdaQuery().in(SpiderAreaFunction::getTaskService, taskServices).in(SpiderAreaFunction::getTaskComponent, taskComponents).list();
            // 把spiderAreaFunctions转成map taskComponent+taskServer为key value为SpiderAreaFunction
            Map<String, SpiderAreaFunction> spiderAreaFunctionMap = spiderAreaFunctions
                    .stream()
                    .collect(Collectors.toMap(item -> item.getTaskComponent() + item.getTaskService(), Function.identity()));
            List<SpiderAreaFunctionVersion> updateList = new ArrayList<>();
            for (NodeParamInfo nodeParamInfo : areaModel.getNodeParamInfoList()) {
                String key = nodeParamInfo.getTaskComponent() + nodeParamInfo.getTaskService();
                SpiderDomainFunctionTask task = spiderDomainFunctionTaskMap.get(Integer.parseInt(nodeParamInfo.getTaskId()));
                SpiderAreaFunctionVersion functionVersion = spiderAreaFunctionVersionMap.get(task.getDomainFunctionVersionId());
                if (!spiderAreaFunctionMap.containsKey(key)) {
                    spiderAreaFunctionService.lambdaUpdate()
                            .set(SpiderAreaFunction::getTaskService, nodeParamInfo.getTaskService())
                            .set(SpiderAreaFunction::getTaskComponent, nodeParamInfo.getTaskComponent())
                            .set(SpiderAreaFunction::getTaskMethod, nodeParamInfo.getMethod())
                            //.set(SpiderAreaFunction::getWorkerType, areaModel.getWorkerType().name())
                            .eq(SpiderAreaFunction::getId, functionVersion.getDomainFunctionId())
                            .update();
                }
                functionVersion.setVersionDesc(nodeParamInfo.getDesc());
                functionVersion.setVersion(nodeParamInfo.getVersion());
                functionVersion.setRunMapping(new ParamPack(nodeParamInfo.getOutputParamDefs()));
                functionVersion.setResultMapping(new ParamPack(nodeParamInfo.getInputParamDefs()));
                functionVersion.setStatus(NodeStatus.START.name());
                updateList.add(functionVersion);
            }
            spiderAreaFunctionVersionService.updateBatchById(updateList);
        }
    }

    public FunctionInfo queryNodeVersion(String taskComponent, String taskService, String version) {
        // 通过 taskComponent 和 taskService 使用spiderAreaFunctionService 查询一条功能信息
        SpiderAreaFunction spiderAreaFunction = spiderAreaFunctionService.lambdaQuery()
                .eq(SpiderAreaFunction::getTaskComponent, taskComponent)
                .eq(SpiderAreaFunction::getTaskService, taskService).one();
        SpiderAreaFunctionVersion spiderAreaFunctionVersion = spiderAreaFunctionVersionService.lambdaQuery()
                .eq(SpiderAreaFunctionVersion::getDomainFunctionId, spiderAreaFunction.getId())
                .eq(SpiderAreaFunctionVersion::getVersion, version).one();
        return new FunctionInfo(spiderAreaFunctionVersion.getVersion(), spiderAreaFunctionVersion.getResultMapping(), spiderAreaFunctionVersion.getRunMapping(), spiderAreaFunction.getTaskMethod(), spiderAreaFunction.getWorkerId());

    }

    public void updateNodeInfo(RefreshAreaModel refreshAreaModel) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(Constant.TASK_COMPONENT, refreshAreaModel.getTaskComponent());
        parameters.put(Constant.TASK_SERVICE, refreshAreaModel.getTaskService());
        // 构造出入的参数结构
        parameters.put(Constant.PARAM_MAPPING, refreshAreaModel.getParmMap().get("param"));
        parameters.put(Constant.RESULT_MAPPING, refreshAreaModel.getParmMap().get("result"));
        parameters.put("worker", refreshAreaModel.getParmMap().get("worker"));
        parameters.put("taskMethod", refreshAreaModel.getParmMap().get("method"));
        parameters.put("areaId", refreshAreaModel.getAreaId());
        parameters.put("name", refreshAreaModel.getParmMap().get("functionName"));
        parameters.put("desc", refreshAreaModel.getParmMap().get("desc"));
        StringBuilder sql = new StringBuilder();

        sql.append("update spider_area_function set param_mapping = #{paramMapping}, result_mapping = #{resultMapping},worker_id = #{worker},task_method = #{taskMethod}");
        if (refreshAreaModel.getParmMap().containsKey("functionName")) {
            sql.append(", `name` = #{name}");
        }
        if (refreshAreaModel.getParmMap().containsKey("desc")) {
            sql.append(", `desc` = #{desc}");
        }
        if (StringUtils.isNotEmpty(refreshAreaModel.getAreaId())) {
            sql.append(", area_id = #{areaId}");
        }
        sql.append(" where task_component = #{taskComponent} and task_service = #{taskService}");
        log.info("sql:{}", sql.toString());
        SqlTemplate
                .forUpdate(client, sql.toString())
                .execute(parameters)
                .onFailure(fail -> {
                    log.error("更新失败{}", ExceptionMessage.getStackTrace(fail));
                });
    }

    // 新增功能节点信息
    public void upsertDomainFunction(SpiderAreaFunction spiderAreaFunction) {
        if (StringUtils.isEmpty(spiderAreaFunction.getId())) {
            spiderAreaFunction.setId(UUID.randomUUID().toString());
            spiderAreaFunctionService.save(spiderAreaFunction);
            return;
        }
        spiderAreaFunctionService.updateById(spiderAreaFunction);
    }

    // 新增版本信息
    public void upsertDomainFunctionVersion(SpiderAreaFunctionVersion spiderAreaFunctionVersion) {
        spiderAreaFunctionVersionService.saveOrUpdate(spiderAreaFunctionVersion);
    }

    // 新增case
    public void updateCase(String versionId, List<String> cases) {
        SpiderAreaFunctionVersion functionVersion = spiderAreaFunctionVersionService.getById(versionId);
        if (Objects.isNull(functionVersion.getTestCase()) || CollectionUtils.isEmpty(functionVersion.getTestCase().getCases())) {
            TestCase testCase = new TestCase();
            testCase.setCases(cases);
            functionVersion.setTestCase(testCase);
            spiderAreaFunctionVersionService.updateById(functionVersion);
            return;
        }
        functionVersion.getTestCase().getCases().addAll(cases);
        spiderAreaFunctionVersionService.updateById(functionVersion);
    }

    public QueryDomainFunctionResult queryDomainFunction(QueryDomainFunctionParam param) {
        Page<SpiderAreaFunction> rowPage = new Page(param.getPage(), param.getSize());
        LambdaQueryWrapper<SpiderAreaFunction> queryWrapper = new LambdaQueryWrapper<SpiderAreaFunction>()
                .likeRight(StringUtils.isNotEmpty(param.getName()), SpiderAreaFunction::getName, param.getName())
                .likeRight(StringUtils.isNotEmpty(param.getAreaName()), SpiderAreaFunction::getAreaName, param.getAreaName())
                .likeRight(StringUtils.isNotEmpty(param.getSonAreaName()), SpiderAreaFunction::getSonDomainName, param.getSonAreaName());

        IPage page = spiderAreaFunctionService.page(rowPage, queryWrapper);
        return new QueryDomainFunctionResult(page.getRecords(), page.getTotal());
    }

    // 查询版本
    public QueryDomainFunctionVersionResult queryDomainFunctionVersion(QueryDomainFunctionVersionParam param) {
        List<SpiderAreaFunctionVersion> functionVersions = spiderAreaFunctionVersionService.lambdaQuery()
                .eq(StringUtils.isNotEmpty(param.getDomainFunctionId()), SpiderAreaFunctionVersion::getDomainFunctionId, param.getDomainFunctionId())
                .likeRight(StringUtils.isNotEmpty(param.getSonDomainVersion()), SpiderAreaFunctionVersion::getSonDomainVersion, param.getSonDomainVersion())
                .list();
        return new QueryDomainFunctionVersionResult(functionVersions);
    }
}
