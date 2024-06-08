package cn.spider.framework.domain.area.worker;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.domain.area.worker.data.WorkerModel;
import cn.spider.framework.domain.sdk.interfaces.WorkerInterface;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.worker
 * @Author: dengdongsheng
 * @CreateTime: 2023-09-09  14:14
 * @Description: 操作工作服务
 * @Version: 1.0
 */
@Slf4j
public class WorkerImpl implements WorkerInterface {

    private MySQLPool client;

    private RowMapper<WorkerModel> ROW_WORKER = row -> {
        WorkerModel workerModel = new WorkerModel();
        workerModel.setId(row.getString("id"));
        workerModel.setWorkerName(row.getString("worker_name"));
        workerModel.setDesc(row.getString("desc"));
        workerModel.setRpcPort(row.getInteger("rpc_port"));
        workerModel.setStatus(row.getString("status"));
        return workerModel;
    };

    public WorkerImpl(MySQLPool client) {
        this.client = client;
    }

    @Override
    public Future<JsonObject> queryWorkerInfo(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        StringBuilder sql = new StringBuilder().append("select * from worker where worker_name = #{workerName}");
        Map<String, Object> parameters = data.getMap();
        SqlTemplate
                .forQuery(client, sql.toString())
                .mapTo(ROW_WORKER)
                .execute(parameters)
                .onSuccess(users -> {
                    RowSet<WorkerModel> function = users;
                    List<WorkerModel> businessFunctionList = Lists.newArrayList();
                    function.forEach(item -> {
                        businessFunctionList.add(item);
                    });
                    if(CollectionUtils.isEmpty(businessFunctionList)){
                        promise.complete();
                        return;
                    }
                    WorkerModel workerModel = businessFunctionList.get(0);
                    promise.complete(JsonObject.mapFrom(workerModel));
                }).onFailure(fail -> {
                    log.error("查询数据失败 {}", ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();

    }

    @Override
    public Future<Void> addWorker(JsonObject data) {
        return null;
    }
}
