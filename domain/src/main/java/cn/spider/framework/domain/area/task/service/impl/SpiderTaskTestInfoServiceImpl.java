package cn.spider.framework.domain.area.task.service.impl;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.container.sdk.data.SimpleStartParam;
import cn.spider.framework.container.sdk.data.SimpleStartResult;
import cn.spider.framework.container.sdk.interfaces.FlowService;
import cn.spider.framework.domain.area.node.entity.SpiderAreaFunction;
import cn.spider.framework.domain.area.node.entity.SpiderAreaFunctionVersion;
import cn.spider.framework.domain.area.node.service.ISpiderAreaFunctionService;
import cn.spider.framework.domain.area.node.service.ISpiderAreaFunctionVersionService;
import cn.spider.framework.domain.area.task.entity.SpiderTaskTestInfo;
import cn.spider.framework.domain.area.task.entity.enums.CaseExpect;
import cn.spider.framework.domain.area.task.entity.enums.TestStatus;
import cn.spider.framework.domain.area.task.mapper.SpiderTaskTestInfoMapper;
import cn.spider.framework.domain.area.task.service.ISpiderTaskTestInfoService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 测试用例的信息 服务实现类
 * </p>
 *
 * @author dds
 * @since 2024-11-10
 */
@Slf4j
@Service
public class SpiderTaskTestInfoServiceImpl extends ServiceImpl<SpiderTaskTestInfoMapper, SpiderTaskTestInfo> implements ISpiderTaskTestInfoService {

    @Resource
    private ISpiderAreaFunctionVersionService spiderAreaFunctionVersionService;

    @Resource
    private ISpiderAreaFunctionService spiderAreaFunctionService;

    @Resource
    private FlowService flowService;

    @Resource
    private ISpiderTaskTestInfoService spiderTaskTestInfoService;

    @Override
    public List<SpiderTaskTestInfo> queryCanRunInitTask() {
        return super.lambdaQuery().eq(SpiderTaskTestInfo::getTestStatus, TestStatus.INIT)
                .orderByDesc(SpiderTaskTestInfo::getCreateTime).last("limit 0,50")
                .list();
    }
    @Override
    public void runCase() {
        List<SpiderTaskTestInfo> spiderTaskTestInfoList = queryCanRunInitTask();
        if (CollectionUtils.isEmpty(spiderTaskTestInfoList)) {
            return;
        }
        //获取spiderTaskTestInfoList中的 domainFunctionVersionId
        Set<String> domainFunctionVersionIds = spiderTaskTestInfoList.stream().map(SpiderTaskTestInfo::getDomainFunctionVersionId).collect(Collectors.toSet());
        List<SpiderAreaFunctionVersion> spiderAreaFunctionVersions = spiderAreaFunctionVersionService.lambdaQuery().in(SpiderAreaFunctionVersion::getId, domainFunctionVersionIds).list();

        if(CollectionUtils.isEmpty(spiderAreaFunctionVersions)){
            return;
        }
        log.info("获取到了——测试任务 {}", JSON.toJSONString(spiderTaskTestInfoList));
        // 获取spiderAreaFunctionVersions中的domainFunctionId
        Set<String> domainFunctionIds = spiderAreaFunctionVersions.stream().map(SpiderAreaFunctionVersion::getDomainFunctionId).collect(Collectors.toSet());
        List<SpiderAreaFunction> spiderAreaFunctions = spiderAreaFunctionService.lambdaQuery().in(SpiderAreaFunction::getId, domainFunctionIds).list();
        // 把spiderAreaFunctionVersions转成map id 为key value为SpiderAreaFunctionVersion
        Map<String, SpiderAreaFunctionVersion> spiderAreaFunctionVersionMap = spiderAreaFunctionVersions
                .stream()
                .collect(Collectors.toMap(SpiderAreaFunctionVersion::getId, Function.identity()));
        // 把spiderAreaFunctions转成map id 为key value为SpiderAreaFunction
        Map<String, SpiderAreaFunction> spiderAreaFunctionMap = spiderAreaFunctions
                .stream()
                .collect(Collectors.toMap(SpiderAreaFunction::getId, Function.identity()));
        for (SpiderTaskTestInfo spiderTaskTestInfo : spiderTaskTestInfoList) {
            SimpleStartParam simpleStartParam = new SimpleStartParam();
            // 查询领域功能,与
            SpiderAreaFunctionVersion functionVersion = spiderAreaFunctionVersionMap.get(spiderTaskTestInfo.getDomainFunctionVersionId());
            SpiderAreaFunction spiderAreaFunction = spiderAreaFunctionMap.get(functionVersion.getDomainFunctionId());
            simpleStartParam.setTaskComponent(spiderAreaFunction.getTaskComponent());
            simpleStartParam.setTaskService(spiderAreaFunction.getTaskService());
            simpleStartParam.setVersion(functionVersion.getVersion());
            simpleStartParam.setWorkerName(spiderAreaFunction.getWorkerId());
            simpleStartParam.setWorkerType(spiderAreaFunction.getWorkerType());
            simpleStartParam.setParamMap(spiderTaskTestInfo.getCaseInputParam().getInputParam().getInnerMap());
            spiderTaskTestInfo.setTestStatus(TestStatus.RUN);
            spiderTaskTestInfoService.updateById(spiderTaskTestInfo);
            flowService.simpleStartNode(JsonObject.mapFrom(simpleStartParam))
                    .onSuccess(suss -> {
                        log.info("case执行成功");
                        try {
                            SimpleStartResult runResult = JSON.parseObject(suss.toString(),SimpleStartResult.class);
                            TestStatus testStatus = runResult.getRunStatus() ? TestStatus.SUSS : TestStatus.FAIL;
                            spiderTaskTestInfo.setTestStatus(testStatus);
                            if(runResult.getRunStatus()){
                                CaseExpect expect = spiderTaskTestInfo.getCaseInputParam().getResultIsException() ? CaseExpect.NO_SATISFY : CaseExpect.SATISFY;
                                spiderTaskTestInfo.setExpect(expect);
                                spiderTaskTestInfo.setTestStatus(TestStatus.SUSS);
                                spiderTaskTestInfo.setRunResult(JSONObject.parseObject(runResult.getResultObject().toString()));
                            }else {
                                CaseExpect expect = spiderTaskTestInfo.getCaseInputParam().getResultIsException() ? CaseExpect.SATISFY : CaseExpect.NO_SATISFY;
                                spiderTaskTestInfo.setExpect(expect);
                                // 把runResult.getError() 截取前3000个字符
                                String error = runResult.getError().substring(0, Math.min(3000, runResult.getError().length()));
                                spiderTaskTestInfo.setError(error);
                                spiderTaskTestInfo.setTestStatus(TestStatus.FAIL);
                            }
                            spiderTaskTestInfoService.updateById(spiderTaskTestInfo);
                        } catch (Exception e) {
                            log.info("执行失败{}",ExceptionMessage.getStackTrace(e));
                        }
                    }).onFailure(fail -> {
                        spiderTaskTestInfo.setTestStatus(TestStatus.FAIL);
                        String error = ExceptionMessage.getStackTrace(fail);
                        log.error("runCase fail {}", error);
                        // 把error 截取前1000个字符
                        spiderTaskTestInfo.setError(error.substring(0, Math.min(1000, error.length())));
                        spiderTaskTestInfoService.updateById(spiderTaskTestInfo);
                    });
        }
    }
}
