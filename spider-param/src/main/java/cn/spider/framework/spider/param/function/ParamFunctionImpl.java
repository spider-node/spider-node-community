package cn.spider.framework.spider.param.function;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.db.util.RocksdbUtil;
import cn.spider.framework.param.sdk.data.*;
import cn.spider.framework.param.sdk.interfaces.ParamInterface;
import cn.spider.framework.spider.param.config.Constants;
import cn.spider.framework.spider.param.engine.JsEngine;
import cn.spider.framework.spider.param.engine.JsRunResult;
import cn.spider.framework.spider.param.engine.metadata.MetadataManager;
import cn.spider.framework.spider.param.manager.ParamExampleManager;
import com.alibaba.fastjson.JSON;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.noear.snack.ONode;
import org.rocksdb.RocksDBException;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * 实现类
 */
@Slf4j
public class ParamFunctionImpl implements ParamInterface {

    private ParamExampleManager paramExampleManager;

    private Executor executor;

    private JsEngine jsEngine;
    // REQUEST_PARAM_NAME

    private MetadataManager metadataManager;


    public ParamFunctionImpl(ParamExampleManager paramExampleManager, Executor executor, JsEngine jsEngine, MetadataManager metadataManager) {
        this.paramExampleManager = paramExampleManager;
        this.executor = executor;
        this.jsEngine = jsEngine;
        this.metadataManager = metadataManager;
    }

    @Override
    public Future<JsonObject> queryRunParam(JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();
        executor.execute(() -> {
            QueryRequestParam queryRequestParam = param.mapTo(QueryRequestParam.class);
            paramExampleManager.get(queryRequestParam.getTaskComponent(), queryRequestParam.getTaskService(),
                    queryRequestParam.getRequestId(),
                    queryRequestParam.getParamsMapping(),
                    queryRequestParam.getAppointParam(),
                    queryRequestParam.getConversionParam(), queryRequestParam.getVersion()).onSuccess(suss -> {
                promise.complete(suss);
            }).onFailure(fail -> {
                promise.fail(fail);
            });
        });
        return promise.future();
    }

    // TODO 改造写入数组
    @Override
    public Future<Void> writeBack(JsonObject param) {
        Promise<Void> promise = Promise.promise();
        executor.execute(() -> {
            WriteBackParam writeBackParam = param.mapTo(WriteBackParam.class);
            if (Objects.isNull(writeBackParam.getResult())) {
                promise.complete();
                return;
            }
            writeBackParam.setNodeId(removeLastChar(writeBackParam.getNodeId()));
            String paramValue = writeBackParam.getResult().toString();
            log.info("notify_标识 {} 参数信息我为 {} nodeId {}", writeBackParam.getRequestId(),paramValue, writeBackParam.getNodeId());
            try {
                metadataManager.insert(writeBackParam.getRequestId(), writeBackParam.getNodeId(), paramValue);
                promise.complete();
                log.info("notify_suss {} 写入信息为 {}", writeBackParam.getRequestId(), paramValue);
            } catch (Exception e) {
                log.info("notify_fail_标识 {}  {} 异常信息 {}", writeBackParam.getRequestId(), JSON.toJSONString(writeBackParam), ExceptionMessage.getStackTrace(e));
                promise.fail(e);
            }
        });
        return promise.future();
    }

    /**
     * 根据表达式获取参数的结果
     *
     * @param param
     * @return
     */
    @Override
    public Future<JsonObject> getParamValue(JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();
        String finalExpression = buildFinalExpression(param.getString(Constant.EXPRESSION));
        String requestId = param.getString(Constant.REQUEST_ID);
        executor.execute(() -> {
            ExpressionQueryValueParam expressionQueryValueParam = new ExpressionQueryValueParam();
            expressionQueryValueParam.setTargetName(finalExpression);
            expressionQueryValueParam.setRequestId(requestId);
            QueryExpressionResult result1 = new QueryExpressionResult();

            try {
                // 移除=后的数据
                ONode result = paramExampleManager.queryValueByExpression(queryAreaParam(expressionQueryValueParam.getTargetName()), expressionQueryValueParam.getRequestId());
                // 获取到校验的结果
                String checkResultString = Objects.isNull(result) ? null : result.toString().replace("\"", "");
                log.info("checkResultString {},finalExpression {} result {}", checkResultString, finalExpression, result);
                Boolean checkResult = checkIsRun(finalExpression, checkResultString);
                result1.setResult(checkResult);
                promise.complete(JsonObject.mapFrom(result1));
            } catch (Exception e) {
                log.info("异常信息为-{} finalExpression {}", ExceptionMessage.getStackTrace(e), finalExpression);
                promise.fail(e);
            }
        });
        return promise.future();
    }


    @Override
    public Future<Void> writeRequestParam(JsonObject param) {
        Promise<Void> promise = Promise.promise();
        executor.execute(() -> {
            // TODO 写入请求
            WriteRequestInfo writeRequestInfo = param.mapTo(WriteRequestInfo.class);
            try {
                metadataManager.insert(writeRequestInfo.getRequestId(), Constants.REQUEST_KEY, JSON.toJSONString(writeRequestInfo.getRequest()));
                promise.complete();
            } catch (Exception e) {
                promise.fail(e);
            }
        });
        return promise.future();
    }

    /**
     * 根据表达式去域中获取对应的值
     *
     * @param param
     * @return
     */
    @Override
    public Future<JsonObject> queryFunctionResult(JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();
        QueryFunctionParam queryFunctionParam = JSON.parseObject(param.toString(), QueryFunctionParam.class);
        executor.execute(() -> {
            promise.complete();
        });
        return promise.future();
    }

    @Override
    public Future<JsonObject> testJsRuntime(JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();
        executor.execute(() -> {
            TestJsRuntimeParam testJsRuntimeParam = param.mapTo(TestJsRuntimeParam.class);
            List<TestJsRuntimeModel> testJsRuntimeModelList = testJsRuntimeParam.getTestJsRuntimeModelList();
            List<Future> needFutures = new ArrayList<>();
            for (TestJsRuntimeModel testJsRuntimeModel : testJsRuntimeModelList) {
                JsonObject mockData = testJsRuntimeModel.mockDataJson();
                Map<String, Object> mockDataMap = new HashMap<>();
                // 判断mock数据是否包含request数据
                if (mockData.containsKey(Constants.REQUEST_KEY)) {
                    mockDataMap.put(Constants.REQUEST_KEY, mockData.getJsonObject(Constants.REQUEST_KEY).getMap());
                    // 判断参数列表是否包含request数据
                } else if (testJsRuntimeModel.getJsFunctionParam().contains(Constants.REQUEST_KEY)) {
                    mockDataMap.put(Constants.REQUEST_KEY, new HashMap<>());
                }
                mockData.remove(Constants.REQUEST_KEY);
                // 判断mock数据是否包含context数据
                if (!mockData.isEmpty()) {
                    mockDataMap.put(Constants.CONTEXT_KEY, mockData.getMap());
                    // 判断参数列表是否包含context数据
                } else if (testJsRuntimeModel.getJsFunctionParam().contains(Constants.CONTEXT_KEY)) {
                    mockDataMap.put(Constants.CONTEXT_KEY, new HashMap<>());
                }

                Future<JsRunResult> future = jsEngine.run(testJsRuntimeModel.getJsFunctionCode(), testJsRuntimeModel.getJsFunctionName(), mockDataMap, testJsRuntimeModel.getNodeId());
                needFutures.add(future);
            }
            CompositeFuture.all(needFutures).onSuccess(suss -> {
                int size = suss.size();
                List<TestJsRuntimeResultModel> testJsRuntimeResultModelList = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    JsRunResult result = suss.resultAt(i);
                    TestJsRuntimeResultModel testJsRuntimeResultModel = new TestJsRuntimeResultModel();
                    testJsRuntimeResultModel.setRunStatus(result.getRunStatus());
                    testJsRuntimeResultModel.setNodeId(result.getNodeId());
                    testJsRuntimeResultModel.setJsFunctionName(result.getFunctionName());
                    testJsRuntimeResultModelList.add(testJsRuntimeResultModel);
                }
                TestJsRuntimeResult testJsRuntimeResult = new TestJsRuntimeResult(testJsRuntimeResultModelList);
                promise.complete(JsonObject.mapFrom(testJsRuntimeResult));
            }).onFailure(fail -> {
                promise.fail(fail);
            });
        });

        return promise.future();
    }

    public String removeLastChar(String str) {
        if (str.contains("-")) {
            String[] parts = str.split("-");
            if (parts.length > 1 && parts[1].matches("\\d+")) {
                return parts[0];
            }
        }
        return str;
    }

    @Override
    public Future<JsonObject> queryRunParamJs(JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();
        QueryJsRequestParam queryJsRequestParam = param.mapTo(QueryJsRequestParam.class);
        queryJsRequestParam.setNodeId(removeLastChar(queryJsRequestParam.getNodeId()));
        baseRunJs(queryJsRequestParam).onSuccess(suss -> {
            JsRunResult Jsresult = suss;
            if (!Jsresult.getRunStatus()) {
                log.info("js执行失败 {}", Jsresult.getErrorMsg());
                promise.fail(Jsresult.getErrorMsg());
                return;
            }
            Object result = Jsresult.getResult();
            if (Objects.isNull(result)) {
                promise.fail("没有获取到参数信息");
                return;
            }
            QueryRequestResult queryRequestResult = new QueryRequestResult();
            queryRequestResult.setRunParam(JsonObject.mapFrom(result));
            promise.complete(JsonObject.mapFrom(queryRequestResult));
        }).onFailure(fail -> {
            // js执行失败
            log.info("js执行失败 {}", ExceptionMessage.getStackTrace(fail));
            promise.fail(fail);
        });

        return promise.future();
    }

    /**
     * 提供 给条件线路
     *
     * @param param 条件
     * @return Future
     */
    @Override
    public Future<JsonObject> getExpression(JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();
        QueryJsRequestParam queryJsRequestParam = param.mapTo(QueryJsRequestParam.class);
        queryJsRequestParam.setNodeId(removeLastChar(queryJsRequestParam.getNodeId()));
        baseRunJs(queryJsRequestParam)
                .onSuccess(suss -> {
                    JsRunResult Jsresult = suss;
                    if (!Jsresult.getRunStatus()) {
                        log.info("js执行失败 {}", Jsresult.getErrorMsg());
                        promise.fail(Jsresult.getErrorMsg());
                        return;
                    }
                    Object result = Jsresult.getResult();
                    if (Objects.isNull(result)) {
                        promise.fail("没有获取到参数信息");
                        return;
                    }
                    // 构造返回的对象信息
                    QueryExpressionResult results = new QueryExpressionResult();
                    results.setResult((Boolean) Jsresult.getResult());
                    promise.complete(JsonObject.mapFrom(results));
                })
                .onFailure(fail -> {
                    promise.fail(fail);
                });
        return promise.future();
    }

    public Future<JsRunResult> baseRunJs(QueryJsRequestParam queryJsRequestParam) {
        Set<String> realParamKey = queryJsRequestParam.getJsFunctionParamReal();
        Set<String> jsFunctionParams = queryJsRequestParam.getJsFunctionParam();
        Map<String, Object> runJsFunctionParam = new HashMap<>();
        // 获取参数节点信息
        if (jsFunctionParams.contains(Constants.REQUEST_KEY)) {
            Set<String> queryRequestParam = new HashSet<>();
            queryRequestParam.add(Constants.REQUEST_KEY);
            Map<String, Map<String, Object>> requestParam = queryNodeValue(queryRequestParam, queryJsRequestParam.getRequestId());
            runJsFunctionParam.put(Constants.REQUEST_KEY, requestParam.get(Constants.REQUEST_KEY));
        }
        if (jsFunctionParams.contains(Constants.CONTEXT_KEY)) {
            if (CollectionUtils.isEmpty(realParamKey)) {
                runJsFunctionParam.put(Constants.CONTEXT_KEY, new HashMap<>());
            } else {
                Set<String> convertRealKey = convertArrayToBaseKey(realParamKey);
                Map<String, Map<String, Object>> nodeParam = queryNodeValue(convertRealKey, queryJsRequestParam.getRequestId());
                runJsFunctionParam.put(Constants.CONTEXT_KEY, buildResult(nodeParam));
            }
        }
        log.info("执行js的参数为 {}", JSON.toJSONString(runJsFunctionParam));
        // 执行获取参数信息
        return jsEngine.run(queryJsRequestParam.getJsFunctionCode(), queryJsRequestParam.getJsFunctionName(), runJsFunctionParam, queryJsRequestParam.getNodeId());
    }

    private Set<String> convertArrayToBaseKey(Set<String> inputArray) {
        return inputArray.stream()
                .map(s -> s.substring(0, s.lastIndexOf('.')))
                .collect(Collectors.toSet());
    }

    /**
     * 根据real的值获取到rocksdb中存的内容
     *
     * @param convertRealKey
     * @param requestId
     * @return
     */
    private Map<String, Map<String, Object>> queryNodeValue(Set<String> convertRealKey, String requestId) {
        Map<String, Map<String, Object>> resultMap = new HashMap<>(convertRealKey.size());
        convertRealKey.forEach(key -> {
            try {
                resultMap.put(key, metadataManager.query(requestId, key).getMap());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return resultMap;
    }

    /**
     * 转换结果,新增一层result
     */
    private Map<String, Object> buildResult(Map<String, Map<String, Object>> resultMap) {

        Map<String, Object> result = new HashMap<>();
        resultMap.forEach((key, value) -> {
            Map<String, Map<String, Object>> resultConversion = new HashMap<>();
            resultConversion.put("result", value);
            result.put(key, resultConversion);
        });
        return result;
    }


    /**
     *
     * @param expression
     * @return
     */
    private String buildFinalExpression(String expression) {
        String finalExpression = expression;
        if (finalExpression.startsWith("sta.")) {
            // 字符串expression中 移除前缀sta.
            finalExpression = expression.substring(4);

        } else if (expression.startsWith("var.")) {
            finalExpression = expression.substring(4);
        }
        return finalExpression;
    }

    private Boolean checkIsRun(String expression, String value) {
        if (expression.contains(Constant.f)) {
            expression = expression.replace(Constant.f, "");
        }
        Map<String, String> expressionMap = new HashMap<>();
        // 拆分
        String[] split = null;
        if (expression.contains(Constant.DOUBLE_EQUALS)) {
            expressionMap.put(Constant.OPERATOR, Constant.DOUBLE_EQUALS);
            split = expression.split(Constant.DOUBLE_EQUALS);
        } else if (expression.contains(Constant.NO_EQUALS)) {
            expressionMap.put(Constant.OPERATOR, Constant.NO_EQUALS);
            split = expression.split(Constant.NO_EQUALS);
        } else if (expression.contains(Constant.EQUALS)) {
            split = expression.split(Constant.EQUALS);
            expressionMap.put(Constant.OPERATOR, Constant.EQUALS);
        } else if (expression.contains(Constant.GREATER_THAN)) {
            split = expression.split(Constant.GREATER_THAN);
            expressionMap.put(Constant.OPERATOR, Constant.GREATER_THAN);
        } else if (expression.contains(Constant.GREATER_THAN_EQUAL)) {
            split = expression.split(Constant.GREATER_THAN_EQUAL);
            expressionMap.put(Constant.OPERATOR, Constant.GREATER_THAN_EQUAL);
        } else if (expression.contains(Constant.LESS_THAN)) {
            split = expression.split(Constant.LESS_THAN);
            expressionMap.put(Constant.OPERATOR, Constant.LESS_THAN);
        } else if (expression.contains(Constant.LESS_THAN_EQUAL)) {
            split = expression.split(Constant.LESS_THAN_EQUAL);
            expressionMap.put(Constant.OPERATOR, Constant.LESS_THAN_EQUAL);
        } else if (expression.contains(Constant.CONTAIN)) {
            expressionMap.put(Constant.OPERATOR, Constant.CONTAIN);
        } else if (expression.contains(Constant.CONTAIN_SET)) {
            expressionMap.put(Constant.OPERATOR, Constant.CONTAIN_SET);
        }

        for (int i = 0; i < split.length; i++) {
            String a = split[i];
            split[i] = a.replace(Constant.k, Constant.kn);
        }

        expressionMap.put(Constant.KEY, split[0]);
        expressionMap.put(Constant.VALUE, split[1]);
        return express(expressionMap, value);
    }


    private Boolean express(Map<String, String> expressionMap, String value) {
        String valueOne = expressionMap.get(Constant.VALUE);

        switch (expressionMap.get(Constant.OPERATOR)) {
            case Constant.DOUBLE_EQUALS:
                if (valueOne.equals(Constant.NULL)) {
                    return StringUtils.isEmpty(value);
                }
                // 校验是否为数字
                if (isNumeric(valueOne)) {
                    return compareSizes(valueOne, value, Constant.DOUBLE_EQUALS);
                }
                return valueOne.equals(value);
            case Constant.NO_EQUALS:
                if (valueOne.equals(Constant.NULL)) {
                    return !StringUtils.isEmpty(value);
                }
                if (isNumeric(valueOne)) {
                    return compareSizes(valueOne, value, Constant.NO_EQUALS);
                }
                return !valueOne.equals(value);
            case Constant.EQUALS:
                if (valueOne.equals(Constant.NULL)) {
                    return StringUtils.isEmpty(value);
                }
                if (isNumeric(valueOne)) {
                    return compareSizes(valueOne, value, Constant.EQUALS);
                }
                return valueOne.equals(value);
            case Constant.GREATER_THAN:
                return compareSizes(valueOne, value, Constant.GREATER_THAN);
            case Constant.GREATER_THAN_EQUAL:
                return compareSizes(valueOne, value, Constant.GREATER_THAN_EQUAL);
            case Constant.LESS_THAN:
                return compareSizes(valueOne, value, Constant.LESS_THAN);
            case Constant.LESS_THAN_EQUAL:
                return compareSizes(valueOne, value, Constant.LESS_THAN_EQUAL);
            case Constant.CONTAIN:
                return value.contains(valueOne);
            case Constant.CONTAIN_SET:
                Set<String> data = JSON.parseObject(valueOne, Set.class);
                return data.contains(value);
        }
        return false;
    }


    private String queryAreaParam(String expression) {
        if (expression.contains(Constant.DOUBLE_EQUALS)) {
            return queryAreaParamSymbol(expression, Constant.DOUBLE_EQUALS);
        } else if (expression.contains(Constant.NO_EQUALS)) {
            return queryAreaParamSymbol(expression, Constant.NO_EQUALS);
        } else if (expression.contains(Constant.EQUALS)) {
            return queryAreaParamSymbol(expression, Constant.EQUALS);
        } else if (expression.contains(Constant.GREATER_THAN)) {
            return queryAreaParamSymbol(expression, Constant.GREATER_THAN);
        } else if (expression.contains(Constant.GREATER_THAN_EQUAL)) {
            return queryAreaParamSymbol(expression, Constant.GREATER_THAN_EQUAL);
        } else if (expression.contains(Constant.LESS_THAN)) {
            return queryAreaParamSymbol(expression, Constant.LESS_THAN);
        } else if (expression.contains(Constant.LESS_THAN_EQUAL)) {
            return queryAreaParamSymbol(expression, Constant.LESS_THAN_EQUAL);
        } else if (expression.contains(Constant.CONTAIN)) {
            return queryAreaParamSymbol(expression, Constant.CONTAIN);
        } else if (expression.contains(Constant.CONTAIN_SET)) {
            return queryAreaParamSymbol(expression, Constant.CONTAIN_SET);
        }
        return expression;
    }

    private String queryAreaParamSymbol(String expression, String specialSymbol) {

        int equalSignIndex = expression.indexOf(specialSymbol); // 获取"=="的索引位置

        if (equalSignIndex != -1) { // 检查"=="是否存在
            return expression.substring(0, equalSignIndex); // 截取从开头到"=="之前的部分
        }
        return expression;
    }

    private boolean isNumeric(String str) {
        return str.matches("^[0-9]+$");
    }

    private Boolean compareSizes(String source, String target, String operator) {
        BigDecimal sourceValue = new BigDecimal(source);
        BigDecimal targetValue = new BigDecimal(target);
        // 校验是否为数字
        switch (operator) {
            case Constant.DOUBLE_EQUALS:
                return sourceValue.compareTo(targetValue) == 0;
            case Constant.NO_EQUALS:
                return !(sourceValue.compareTo(targetValue) == 0);
            case Constant.EQUALS:
                return sourceValue.compareTo(targetValue) == 0;
            case Constant.GREATER_THAN:
                return targetValue.compareTo(sourceValue) == 1;
            case Constant.GREATER_THAN_EQUAL:
                return targetValue.compareTo(sourceValue) > -1;
            case Constant.LESS_THAN:
                return targetValue.compareTo(sourceValue) == -1;
            case Constant.LESS_THAN_EQUAL:
                return targetValue.compareTo(sourceValue) < 1;
            default:
                return false;
        }
    }
}
