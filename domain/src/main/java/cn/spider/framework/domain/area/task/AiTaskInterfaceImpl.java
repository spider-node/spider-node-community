package cn.spider.framework.domain.area.task;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.domain.area.datasource.DatasourceManager;
import cn.spider.framework.domain.area.datasource.data.RunGeneralSQLModel;
import cn.spider.framework.domain.area.task.data.*;
import cn.spider.framework.domain.area.task.data.enums.CodeCreateType;
import cn.spider.framework.domain.area.task.entity.SpiderDomainFunctionAiCoderStep;
import cn.spider.framework.domain.area.task.entity.SpiderTaskTestInfo;
import cn.spider.framework.domain.area.task.entity.enums.CaseExpect;
import cn.spider.framework.domain.area.task.entity.enums.StepStatus;
import cn.spider.framework.domain.area.task.entity.enums.TestStatus;
import cn.spider.framework.domain.area.task.service.ISpiderTaskTestInfoService;
import cn.spider.framework.domain.sdk.data.DemandAnalysisParam;
import cn.spider.framework.domain.sdk.data.UpdateDemandsParam;
import cn.spider.framework.domain.sdk.interfaces.AiTaskInterface;
import cn.spider.node.host.plugin.center.sdk.data.UpdateCoderParam;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
                String codeCreateType = param.getString("codeCreateType");
                String selectedVersion = param.getString("selectedVersion");
                taskManager.runDomainFunctionTask(versionId, CodeCreateType.valueOf(codeCreateType), selectedVersion);
                promise.complete();
            } catch (Exception e) {
                promise.fail(e);
                log.error("createCoder error", e);
            }
        });
        return promise.future();
    }

    @Override
    public Future<Void> updateCoder(JsonObject param) {
        return taskManager.updateCoder(param);
    }

    @Override
    public Future<Void> demandAiParse(JsonObject param) {
        Promise<Void> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            try {
                DemandAnalysisParam demandAnalysisParam = JSON.parseObject(param.toString(), DemandAnalysisParam.class);
                taskManager.analysisDemand(demandAnalysisParam);
                promise.complete();
            } catch (Exception e) {
                log.error("demandAiParse_error {}", ExceptionMessage.getStackTrace(e));
                promise.fail(e);
            }
        });
        return promise.future();
    }

    @Override
    public Future<Void> startTestCase(JsonObject param) {
        log.info("case_data {}", param.toString());
        StartTestCaseParam startTestCaseParam = JSON.parseObject(param.toString(), StartTestCaseParam.class);
        // startTestCaseParam.getCaseSqlModels() 使用caseCode 作为key，caseSql 作为value 转为map
        Map<String, CaseSqlModel> caseSqlModelMap = CollectionUtils.isNotEmpty(startTestCaseParam.getCaseSql()) ? startTestCaseParam.getCaseSql().stream().collect(Collectors.toMap(CaseSqlModel::getSceneCode, Function.identity(), (v1, v2) -> v2)) : new HashMap<>();
        // 根据startTestCaseParam.getMethodInputModes() 使用 sceneCode 作为key，MethodInputModel 作为value 转为map
        Map<String, MethodInputModel> methodInputModelMap = CollectionUtils.isNotEmpty(startTestCaseParam.getMethodInputModes()) ? startTestCaseParam.getMethodInputModes().stream().collect(Collectors.toMap(MethodInputModel::getSceneCode, Function.identity())) : new HashMap<>();
        methodInputModelMap.forEach((key, value) -> {
            MethodInputModel methodInputModel = value;
            if (caseSqlModelMap.containsKey(key)) {
                log.info("caseSqlModelMap-存在key一致 {}");
                // 进行对数据库操作
                CaseSqlModel caseSqlModel = caseSqlModelMap.get(key);
                RunGeneralSQLModel runGeneralSQLModel = new RunGeneralSQLModel(startTestCaseParam.getDatasource(), caseSqlModel.getSql(), caseSqlModel.getParam().getInnerMap());
                datasourceManager.runUpdateSql(runGeneralSQLModel).onSuccess(suss -> {
                    SpiderTaskTestInfo spiderTaskTestInfo = new SpiderTaskTestInfo();
                    spiderTaskTestInfo.setCases(methodInputModel.getScene());
                    spiderTaskTestInfo.setCaseInputParam(methodInputModel);
                    log.info("runUpdateSql {}", JSON.toJSONString(runGeneralSQLModel));
                    spiderTaskTestInfo.setCaseSql(caseSqlModel);
                    log.info("caseSqlModel {}", JSON.toJSONString(caseSqlModel));
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

    /**
     * 查询ai构造的测试用例
     *
     * @return 测试用例信息
     */
    @Override
    public Future<JsonObject> queryTestCaseInfo(JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            String domainFunctionVersionId = param.getString("domainFunctionVersionId");
            try {
                List<SpiderTaskTestInfo> spiderTaskTestInfos = spiderTaskTestInfoService.lambdaQuery().eq(SpiderTaskTestInfo::getDomainFunctionVersionId, domainFunctionVersionId).list();
                CaseInfoResult caseInfoResult = new CaseInfoResult(spiderTaskTestInfos);
                promise.complete(JsonObject.mapFrom(caseInfoResult));
            } catch (Exception e) {
                promise.fail(e);
                log.error("查询测试用例失败{}", ExceptionMessage.getStackTrace(e));
            }
        });
        return promise.future();
    }

    @Override
    public Future<Void> restartCase(JsonObject param) {
        Integer taskId = param.getInteger("id");
        spiderTaskTestInfoService.lambdaUpdate()
                .set(SpiderTaskTestInfo::getTestStatus, TestStatus.INIT)
                .set(SpiderTaskTestInfo::getRunResult, null)
                .set(SpiderTaskTestInfo::getExpect, CaseExpect.INIT)
                .set(SpiderTaskTestInfo::getError, "")
                .eq(SpiderTaskTestInfo::getId, taskId)
                .update();
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> syncAiCoderStep(JsonObject param) {
        Promise<Void> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            try {
                SpiderDomainFunctionAiCoderStep step = param.mapTo(SpiderDomainFunctionAiCoderStep.class);
                step.setStepStatus(StringUtils.isEmpty(step.getError()) ? StepStatus.SUSS : StepStatus.FAIL);
                taskManager.syncAiCoderStep(step);
                promise.complete();
            } catch (Exception e) {
                promise.fail(e);
                log.error(ExceptionMessage.getStackTrace(e));
            }
        });
        return promise.future();
    }

    @Override
    public Future<JsonObject> queryTaskStep(JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            try {
                QueryAiCoderStepResult queryAiCoderStepResult = taskManager.queryAiCoderStep(param.getString("functionVersionId"));
                promise.complete(JsonObject.mapFrom(queryAiCoderStepResult));
            } catch (Exception e) {
                promise.fail(e);
                log.error("查询任务步骤失败{}", ExceptionMessage.getStackTrace(e));
            }
        });
        return promise.future();
    }
}
