package cn.spider.framework.spider.param.engine;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.spider.param.engine.enums.JsRunTimeErrorType;
import cn.spider.framework.spider.param.engine.function.js.JsFunctionExecutor;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Executor;

@Slf4j
public class JsEngine {
    private JsFunctionExecutor jsFunctionExecutor;

    // js的执行线程池
    private Executor executor;

    public JsEngine(JsFunctionExecutor jsFunctionExecutor, Executor executor) {
        this.jsFunctionExecutor = jsFunctionExecutor;
        this.executor = executor;
    }

    /**
     * js运行
     *
     * @param jsCode         JS 代码
     * @param jsFunctionName js 函数名
     * @param mockDataMap    执行数据
     * @param nodeId         节点id
     * @return Future
     */
    public Future<JsRunResult> run(String jsCode, String jsFunctionName, Map<String, Object> mockDataMap, String nodeId) {
        Promise<JsRunResult> promise = Promise.promise();
        this.executor.execute(() -> {
            String threadName = Thread.currentThread().getName();
            JsRunResult jsRunResult = new JsRunResult();
            jsRunResult.setFunctionName(jsFunctionName);
            jsRunResult.setNodeId(nodeId);
            log.info("load js start: {} nodeId {}", jsCode, nodeId);
            try {
                jsFunctionExecutor.loadFunction(jsFunctionName, jsCode, threadName);
            } catch (Exception e) {
                String errorMsg = ExceptionMessage.getStackTrace(e);
                jsRunResult.setErrorMsg(errorMsg);
                log.error("load js error: {}", errorMsg);
                jsRunResult.setRunStatus(false);
                jsRunResult.setJsRunTimeErrorType(JsRunTimeErrorType.LOAD_ERROR);
                promise.complete(jsRunResult);
                return;
            }

            try {
                long invokeStartTime = System.currentTimeMillis(); // 记录调用函数开始时间
                Object result = jsFunctionExecutor.invokeFunction(jsFunctionName, mockDataMap, threadName);
                long invokeEndTime = System.currentTimeMillis(); // 记录调用函数结束时间
                log.info("jsFunctionExecutor.invokeFunction took {} ms", invokeEndTime - invokeStartTime); // 打印调用函数耗时
                jsRunResult.setRunStatus(true);
                jsRunResult.setResult(result);
            } catch (Exception e) {
                String errorMsg = ExceptionMessage.getStackTrace(e);
                log.error("run js error: {}", errorMsg);
                jsRunResult.setRunStatus(false);
                jsRunResult.setJsRunTimeErrorType(JsRunTimeErrorType.JS_RUN_ERROR);
                jsRunResult.setErrorMsg(errorMsg);
            }
            promise.complete(jsRunResult);
        });
        return promise.future();
    }

    /**
     * 通过功能需要更换,先移除
     * @param functionName 函数名称
     */
    public void functionJsLose(String functionName) {
        jsFunctionExecutor.functionJsLose(functionName);
    }
}
