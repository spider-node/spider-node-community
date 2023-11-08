package cn.spider.framework.flow.funtion;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.common.utils.IdWorker;
import cn.spider.framework.common.utils.SnowFlake;
import cn.spider.framework.common.utils.SnowIdDto;
import cn.spider.framework.container.sdk.data.StartFlowRequest;
import cn.spider.framework.flow.business.BusinessManager;
import cn.spider.framework.flow.business.data.BusinessFunctions;
import cn.spider.framework.flow.business.enums.IsAsync;
import cn.spider.framework.flow.business.enums.IsRetry;
import cn.spider.framework.flow.engine.example.enums.FlowExampleRole;
import cn.spider.framework.flow.engine.example.enums.VerifyStatus;
import cn.spider.framework.flow.engine.facade.TaskResponse;
import cn.spider.framework.container.sdk.interfaces.FlowService;
import cn.spider.framework.flow.engine.StoryEngine;
import cn.spider.framework.flow.engine.facade.ReqBuilder;
import cn.spider.framework.flow.engine.facade.StoryRequest;
import cn.spider.framework.flow.load.loader.ClassLoaderManager;
import cn.spider.framework.flow.timer.SpiderTimer;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Objects;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.funtion
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-26  20:07
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
@Component
public class FlowServiceImpl implements FlowService {

    @Resource
    private StoryEngine storyEngine;

    @Resource
    private BusinessManager businessManager;
    @Resource
    private ClassLoaderManager classLoaderManager;

    private final String REQUEST_PREFIX = "REQUEST_PREFIX";

    @Resource
    private SpiderTimer spiderTimer;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 执行实例核心类
     *
     * @param data
     * @return
     */
    @Override
    public Future<JsonObject> startFlow(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        StartFlowRequest request = data.mapTo(StartFlowRequest.class);

        Future<BusinessFunctions> functionsFuture = businessManager.queryStartIdByFunctionId(request.getFunctionId());
        functionsFuture.onSuccess(querySuss -> {
            if (Objects.isNull(querySuss)) {
                promise.fail("没有查询到对应的功能信息");
                return;
            }
            // step1: 获取功能信息
            BusinessFunctions functions = querySuss;
            // stet2: 校验功能是异步还是同步
            if (functions.getIsAsync().equals(IsAsync.ASYNC)) {
                // 通知客户端收到该消息
                endSuss(promise, new JsonObject());
            }
            // step3 执行具体功能
            run(request, functions,promise,functions.getIsAsync().equals(IsAsync.ASYNC),data);
        }).onFailure(fail -> {
            log.info("获取功能信息失败 {}", ExceptionMessage.getStackTrace(fail));
            promise.fail(fail);
        });
        return promise.future();
    }

    public void endSuss(Promise<JsonObject> promise, JsonObject result) {
        promise.complete(result);
    }

    public void endFail(Promise<JsonObject> promise, Throwable throwable) {
        promise.fail(throwable);
    }


    public void run(StartFlowRequest request, BusinessFunctions functions,Promise<JsonObject> promise,Boolean isAsync,JsonObject data) {
        // 获取参数
        Object requestParam = request.getRequest(classLoaderManager.queryClassLoader(request.getRequestClassType()));
        String requestId = buildRequestId();
        StoryRequest<Object> req = ReqBuilder.returnType(Object.class)
                .startId(functions.getStartId())
                .functionName(functions.getName())
                .startFlowRequest(request)
                .requestId(requestId)
                // 默认给leader
                .flowExampleRole(FlowExampleRole.LEADER)
                .functionId(functions.getId())
                .request(requestParam)
                .build();

        Future<TaskResponse<Object>> fire = storyEngine.fire(req);
        fire.onSuccess(suss -> {
            TaskResponse<Object> result = suss;
            if (result.isSuccess()) {
                if (isAsync) {
                    return;
                }
                JsonObject resultJson = new JsonObject();
                if (!Objects.isNull(result.getResult())) {
                    resultJson = new JsonObject(JSON.toJSONString(result.getResult()));
                }
                resultJson.put("requestId", requestId);
                endSuss(promise, resultJson);
            } else {
                if (isAsync) {
                    if(functions.getIsRetry().equals(IsRetry.RETRY) && functions.getRetryCount() >0){
                        spiderTimer.registerRetry(requestId,data);
                    }
                    return;
                }
                endFail(promise,result.getResultException());
            }
        }).onFailure(fail -> {
            if (isAsync) {
                log.info("异步执行失败 {}",ExceptionMessage.getStackTrace(fail));
                if(functions.getIsRetry().equals(IsRetry.RETRY) && functions.getRetryCount() >0){
                    // 注册延迟
                    spiderTimer.registerRetry(requestId,data);
                }
                return;
            }
            log.error("fail {}",ExceptionMessage.getStackTrace(fail));
            endFail(promise,fail);
        });
    }

    /**
     * @param data
     * @return
     */
    @Override
    public Future<Void> activation(JsonObject data) {
        if (!data.containsKey("requestId")) {
            return Future.failedFuture("没有找到对应的requestId");
        }
        Promise<Void> promise = Promise.promise();

        Future<Void> future = storyEngine.activationExample(data.getString("requestId"), VerifyStatus.valueOf(data.getString("verifyResult")));
        future.onSuccess(suss -> {
            promise.complete();
        }).onFailure(fail -> {
            promise.fail(fail);
        });
        return promise.future();
    }

    @Override
    public Future<JsonObject> queryRunNumber() {
        Integer exampleSize = storyEngine.getFlowExampleManager().queryExampleNum();
        JsonObject result = new JsonObject();
        result.put("size",exampleSize);
        return Future.succeededFuture(result);
    }

    private String buildRequestId() {
        SnowIdDto snowIdDto = IdWorker.calculateDataIdAndWorkId2(this.redissonClient, REQUEST_PREFIX);
        SnowFlake snowFlake = new SnowFlake(snowIdDto.getWorkerId(), snowIdDto.getDataCenterId(), snowIdDto.getTimestamp());
        return snowFlake.nextId() + "";
    }


}
