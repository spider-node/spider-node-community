package cn.spider.framework.domain.area.node;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.domain.area.function.data.FunctionModel;
import cn.spider.framework.domain.area.function.version.data.FunctionVersionModel;
import cn.spider.framework.domain.area.function.version.data.enums.VersionStatus;
import cn.spider.framework.domain.area.node.data.Node;
import cn.spider.framework.domain.area.node.data.QueryNodeParam;
import cn.spider.framework.domain.area.node.data.enums.NodeStatus;
import cn.spider.framework.domain.area.node.data.enums.ServiceTaskType;
import com.google.common.collect.Lists;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

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

    public NodeManger(MySQLPool client) {
        this.client = client;
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
        return node;
    };

    // 新增节点
    public Future<Void> createNode(Node node) {
        Promise<Void> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();
        sql.append("insert into spider_area_function (`id`,`name`,`desc`,`async`,`task_component`,`task_service`,`service_task_type`,`status`,area_id) values (#{id},#{name},#{desc},#{async},#{taskComponent},#{taskService},#{serviceTaskType},#{status},#{areaId})");
        JsonObject param = JsonObject.mapFrom(node);
        Map<String, Object> parameters = param.getMap();
        SqlTemplate
                .forUpdate(client, sql.toString())
                .execute(parameters)
                .onSuccess(function -> {
                    promise.complete();
                }).onFailure(fail -> {
                    promise.fail(fail);
                });
        return promise.future();
    }

    // 编辑节点
    public Future<Void> updateNode(Node node) {
        Promise<Void> promise = Promise.promise();
        StringBuilder sql = new StringBuilder();
        sql.append("update spider_area_function set name = #{name},desc = #{desc},async = #{async},task_component = #{taskComponent},task_service = #{taskService},service_task_type#{serviceTaskType},status = #{status} where id = #{id}");
        JsonObject param = JsonObject.mapFrom(node);
        Map<String, Object> parameters = param.getMap();
        SqlTemplate
                .forUpdate(client, sql.toString())
                .execute(parameters)
                .onSuccess(function -> {
                    promise.complete();
                }).onFailure(fail -> {
                    promise.fail(fail);
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
                    log.error("查询数据失败", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }

    private String buildQuerySql(QueryNodeParam param) {
        StringBuilder querySql = new StringBuilder();
        querySql.append("select * from spider_area_function where 1=1 ");
        if (StringUtils.isNotEmpty(param.getName())) {
            querySql.append(" and name like '#{name}%'");
        }
        if (StringUtils.isNotEmpty(param.getAreaId())) {
            querySql.append(" and area_id = #{areaId}");

        }

        if (StringUtils.isNotEmpty(param.getTaskComponent())) {
            querySql.append("and task_component = #{taskComponent}");
        }

        if (StringUtils.isNotEmpty(param.getTaskService())) {
            querySql.append(" and task_service = #{taskService}");
        }
        querySql.append("limit #{page},#{size}");
        return querySql.toString();
    }


}
