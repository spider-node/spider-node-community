package cn.spider.framework.domain.area.node;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.domain.area.AreaManger;
import cn.spider.framework.domain.area.data.AreaModel;
import cn.spider.framework.domain.area.data.QueryAreaModel;
import cn.spider.framework.domain.area.data.QueryParamConfigParam;
import cn.spider.framework.domain.area.function.data.FunctionModel;
import cn.spider.framework.domain.area.function.version.data.FunctionVersionModel;
import cn.spider.framework.domain.area.function.version.data.enums.VersionStatus;
import cn.spider.framework.domain.area.node.data.Node;
import cn.spider.framework.domain.area.node.data.QueryNodeParam;
import cn.spider.framework.domain.area.node.data.enums.NodeStatus;
import cn.spider.framework.domain.area.node.data.enums.ServiceTaskType;
import cn.spider.framework.domain.sdk.data.RefreshAreaModel;
import cn.spider.framework.domain.sdk.data.RefreshAreaParam;
import com.alibaba.fastjson.JSON;
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

import java.util.*;

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

    public NodeManger(MySQLPool client, AreaManger areaManger) {
        this.client = client;
        this.areaManger = areaManger;
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
        node.setStatus(NodeStatus.STOP);
        Promise<Void> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();
        node.setId(UUID.randomUUID().toString());
        // 根据领域id查询领域名称
        QueryAreaModel queryAreaModel = new QueryAreaModel();
        queryAreaModel.setPage(1);
        queryAreaModel.setSize(10);
        queryAreaModel.setId(node.getAreaId());
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
                    log.error("查询数据失败 {}", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

    private String buildQuerySql(QueryNodeParam param) {
        StringBuilder querySql = new StringBuilder();
        querySql.append("select * from spider_area_function where 1=1 ");
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
                        promise.fail("没有找到对应的节点");
                        return;
                    }
                    promise.complete(nodes.get(0));
                });
        return promise.future();
    }

    /**
     * 批量更新域的参数信息
     */
    public void refreshNodeParam(RefreshAreaParam areaParam) {
        List<RefreshAreaModel> areaModelList = areaParam.getAreaModelList();
        if (CollectionUtils.isEmpty(areaModelList)) {
            return;
        }
        for (RefreshAreaModel refreshAreaModel : areaModelList) {
            StringBuilder sql = new StringBuilder();
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(Constant.TASK_COMPONENT, refreshAreaModel.getTaskComponent());
            parameters.put(Constant.TASK_SERVICE, refreshAreaModel.getTaskService());
            // 构造出入的参数结构
            parameters.put(Constant.PARAM_MAPPING, refreshAreaModel.getParmMap().get("param"));
            parameters.put(Constant.RESULT_MAPPING, refreshAreaModel.getParmMap().get("result"));
            parameters.put("worker", refreshAreaModel.getParmMap().get("worker"));
            parameters.put("taskMethod", refreshAreaModel.getParmMap().get("method"));
            sql.append("update spider_area_function set param_mapping = #{paramMapping}, result_mapping = #{resultMapping},worker_id = #{worker},task_method = #{taskMethod}  where task_component = #{taskComponent} and task_service = #{taskService}");
            SqlTemplate
                    .forUpdate(client, sql.toString())
                    .execute(parameters)
                    .onFailure(fail -> {
                        log.error("更新失败{}", ExceptionMessage.getStackTrace(fail));
                    });
        }
    }

    /*public Future<Node> queryNodeConfig(QueryParamConfigParam param) {
        Map<String, Object> parameters = new HashMap<>();

        Promise<Node> promise = Promise.promise();
        SqlTemplate
                .forQuery(client, "select * from spider_area_function")
                .mapTo(ROW_BUSINESS)
                .execute(parameters)
                .onSuccess(users -> {
                    RowSet<Node> function = users;
                    List<Node> nodes = Lists.newArrayList();
                    function.forEach(item -> {
                        if (param.getTaskComponents().contains(item.getTaskComponent()) && param.getTaskServices().contains(item.getTaskService())) {
                            return;
                        }
                    });
                    promise.complete(nodes);
                }).onFailure(fail -> {
                    log.error("查询数据失败 {}", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }*/

}
