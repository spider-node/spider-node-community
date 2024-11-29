package cn.spider.framework.domain.area.task;

import cn.spider.framework.domain.area.datasource.DatasourceManager;
import cn.spider.framework.domain.area.datasource.data.RunGeneralSQLModel;
import cn.spider.framework.domain.area.task.data.CaseSqlModel;
import cn.spider.framework.domain.area.task.data.MethodInputModel;
import cn.spider.framework.domain.area.task.data.StartTestCaseParam;
import cn.spider.framework.domain.area.task.entity.SpiderTaskTestInfo;
import cn.spider.framework.domain.area.task.service.ISpiderTaskTestInfoService;
import cn.spider.framework.domain.sdk.interfaces.AiTaskInterface;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class AiTaskInterfaceImpl implements AiTaskInterface {

    private TaskManager taskManager;

    private Executor spiderBusinessPool;

    private DatasourceManager datasourceManager;

    private ISpiderTaskTestInfoService spiderTaskTestInfoService;

    public AiTaskInterfaceImpl(TaskManager taskManager, Executor spiderBusinessPool,
                               ISpiderTaskTestInfoService spiderTaskTestInfoService,
                               DatasourceManager datasourceManager) {
        this.taskManager = taskManager;
        this.spiderBusinessPool = spiderBusinessPool;
        this.spiderTaskTestInfoService = spiderTaskTestInfoService;
        this.datasourceManager = datasourceManager;

    }

    @Override
    public Future<Void> createCoder(JsonObject param) {
        Promise<Void> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            try {
                String versionId = param.getString("domainFunctionVersionId");
                taskManager.runDomainFunctionTask(versionId);
                promise.complete();
            } catch (Exception e) {
                promise.fail(e);
                log.error("createCoder error", e);
            }
        });
        return promise.future();
    }

    @Override
    public Future<Void> startTestCase(JsonObject param) {
        log.info("case_data {}",param.toString());
        StartTestCaseParam startTestCaseParam = JSON.parseObject(param.toString(), StartTestCaseParam.class);
        // startTestCaseParam.getCaseSqlModels() 使用caseCode 作为key，caseSql 作为value 转为map

        Map<String, CaseSqlModel> caseSqlModelMap = CollectionUtils.isNotEmpty(startTestCaseParam.getCaseSqlModels()) ? startTestCaseParam.getCaseSqlModels().stream().collect(Collectors.toMap(CaseSqlModel::getSceneCode, Function.identity())) : new HashMap<>();

        // 根据startTestCaseParam.getMethodInputModes() 使用 sceneCode 作为key，MethodInputModel 作为value 转为map
        Map<String, MethodInputModel> methodInputModelMap = CollectionUtils.isNotEmpty(startTestCaseParam.getMethodInputModes()) ? startTestCaseParam.getMethodInputModes().stream().collect(Collectors.toMap(MethodInputModel::getScene, Function.identity())): new HashMap<>();

        methodInputModelMap.forEach((key, value) -> {
            MethodInputModel methodInputModel = value;
            if (caseSqlModelMap.containsKey(key)) {
                // 进行对数据库操作
                CaseSqlModel caseSqlModel = caseSqlModelMap.get(key);
                RunGeneralSQLModel runGeneralSQLModel = new RunGeneralSQLModel(startTestCaseParam.getDatasource(), caseSqlModel.getSql(), caseSqlModel.getParam().getInnerMap());
                datasourceManager.runUpdateSql(runGeneralSQLModel).onSuccess(suss -> {

                    SpiderTaskTestInfo spiderTaskTestInfo = new SpiderTaskTestInfo();
                    spiderTaskTestInfo.setCases(methodInputModel.getScene());
                    spiderTaskTestInfo.setCaseInputParam(methodInputModel);
                    spiderTaskTestInfo.setCaseSql(caseSqlModel);
                    spiderTaskTestInfo.setTaskId(startTestCaseParam.getTaskId());
                    spiderTaskTestInfo.setDomainFunctionVersionId(startTestCaseParam.getDomainFunctionVersionId());
                    spiderTaskTestInfoService.save(spiderTaskTestInfo);
                });
                return;
            }
            SpiderTaskTestInfo spiderTaskTestInfo = new SpiderTaskTestInfo();
            spiderTaskTestInfo.setCases(methodInputModel.getScene());
            spiderTaskTestInfo.setCaseInputParam(methodInputModel);
            spiderTaskTestInfo.setDomainFunctionVersionId(startTestCaseParam.getDomainFunctionVersionId());
            spiderTaskTestInfo.setTaskId(startTestCaseParam.getTaskId());
            spiderTaskTestInfoService.save(spiderTaskTestInfo);
        });
        return Future.succeededFuture();
    }
}
