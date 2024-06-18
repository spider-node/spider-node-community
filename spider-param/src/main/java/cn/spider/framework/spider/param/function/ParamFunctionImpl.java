package cn.spider.framework.spider.param.function;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.param.sdk.data.*;
import cn.spider.framework.param.sdk.interfaces.ParamInterface;
import cn.spider.framework.spider.param.manager.ParamExampleManager;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.noear.snack.ONode;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * 实现类
 */
@Slf4j
public class ParamFunctionImpl implements ParamInterface {

    private ParamExampleManager paramExampleManager;

    private Executor executor;
    // REQUEST_PARAM_NAME
    private final String REQUEST_PARAM_NAME = "param";

    private final String REQUEST_ID = "requestId";


    public ParamFunctionImpl(ParamExampleManager paramExampleManager, Executor executor) {
        this.paramExampleManager = paramExampleManager;
        this.executor = executor;
    }

    @Override
    public Future<JsonObject> queryRunParam(JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();
        executor.execute(() -> {
            QueryRequestParam queryRequestParam = param.mapTo(QueryRequestParam.class);
            paramExampleManager.get(queryRequestParam.getTaskComponent(), queryRequestParam.getTaskService(),
                    queryRequestParam.getRequestId(),
                    queryRequestParam.getParamsMapping(), queryRequestParam.getAppointParam()).onSuccess(suss -> {
                promise.complete(suss);
            }).onFailure(fail -> {
                promise.fail(fail);
            });
        });
        return promise.future();
    }

    @Override
    public Future<Void> writeBack(JsonObject param) {
        Promise<Void> promise = Promise.promise();
        executor.execute(() -> {
            WriteBackParam writeBackParam = JSON.parseObject(param.toString(), WriteBackParam.class);
            writeBackParam.setResult(param.getJsonObject("result"));
            if (Objects.isNull(writeBackParam.getResult())) {
                promise.complete();
                return;
            }
            paramExampleManager.notifyResult(writeBackParam.getTaskComponent(),
                    writeBackParam.getTaskService(), writeBackParam.getRequestId(), new JsonObject(writeBackParam.getResult().toString())).onSuccess(suss -> {
                promise.complete();
            }).onFailure(fail -> {
                promise.fail(fail);
            });
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
            if (!param.containsKey(REQUEST_PARAM_NAME)) {
                promise.complete();
                return;
            }
            paramExampleManager.insertRequestParam(param.getString(REQUEST_ID), param.getJsonObject(REQUEST_PARAM_NAME));
            promise.complete();
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
            Map<String, Object> resultMap = new HashMap<>(queryFunctionParam.getParams().size());
            queryFunctionParam.getParams().forEach((key, value) -> {
                try {
                    ONode result = paramExampleManager.queryValueByExpression(value, queryFunctionParam.getRequestId());
                    resultMap.put(key, result.toObject());
                } catch (Exception e) {
                    log.error("查询参数失败 {}", ExceptionMessage.getStackTrace(e));
                    promise.fail(e);
                    throw new RuntimeException(e);
                }
            });
            promise.complete(JsonObject.mapFrom(resultMap));
        });
        return promise.future();
    }


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
                Set<String> data = JSON.parseObject(valueOne,Set.class);
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
