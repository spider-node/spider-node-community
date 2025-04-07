/*
 *
 *  * Copyright (c) 2020-2023, Lykan (jiashuomeng@gmail.com).
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package cn.spider.framework.flow.component.expression;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.flow.bus.StoryBus;
import cn.spider.framework.flow.load.SpringBeanUtils;
import cn.spider.framework.flow.util.AssertUtil;
import cn.spider.framework.param.sdk.data.QueryExpressionResult;
import cn.spider.framework.param.sdk.data.QueryJsRequestParam;
import cn.spider.framework.param.sdk.interfaces.ParamInterface;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * @author dds
 */
@Slf4j
public class ConditionExpressionImpl implements ConditionExpression {

    /**
     * 原表达式
     */
    private String expression;

    /**
     * js的功能名称
     */
    private String jsFunctionName;

    /**
     * js的参数列表
     */
    private Set<String> jsParams;

    /**
     * JS的真实参数列表
     */
    private Set<String> jsParamReal;

    private String nodeId;

    private String nodeName;

    /**
     * 真实参与计算的表达式
     */
    private String conditionExpression;

    /**
     * 需要解析表达式
     */
    private boolean needParserExpression;

    /**
     * 表达式顺序
     */
    private int order;

    /**
     * 计算表达式行为，由具体业务指定
     */
    private final BiPredicate<StoryBus, String> testCondition;

    private ParamInterface paramInterface;

    public ConditionExpressionImpl(BiPredicate<StoryBus, String> testCondition) {
        AssertUtil.notNull(testCondition);
        this.testCondition = testCondition;
        if (Objects.isNull(this.paramInterface)) {
            this.paramInterface = SpringBeanUtils.getBean(ParamInterface.class);
        }
    }

    /**
     * 远程调用param服务,进行并行计算, 返回true,false
     *
     * @param storyBus scopeData
     * @return
     */
    @Override
    public Future<Boolean> condition(StoryBus storyBus) {
        if (storyBus == null) {
            Future.succeededFuture(false);
        }
        AssertUtil.notBlank(this.expression);
        // 进行查询 param角色获取true,false
        Promise<Boolean> promise = Promise.promise();
        QueryJsRequestParam queryJsRequestParam = new QueryJsRequestParam(this.jsFunctionName, this.expression, this.jsParams, this.jsParamReal, this.nodeId, storyBus.queryRequestId());
        Future<JsonObject> future = paramInterface.getExpression(JsonObject.mapFrom(queryJsRequestParam));
        future.onSuccess(suss -> {
            QueryExpressionResult expressionResult = suss.mapTo(QueryExpressionResult.class);
            promise.complete(expressionResult.getResult());
        }).onFailure(fail -> {
            log.info("查询表达式失败,原因:{}", ExceptionMessage.getStackTrace(fail));
            promise.fail(fail);
        });
        return promise.future();
    }

    @Override
    public boolean match(String expression) {
        return false;
    }


    // TODO 优化后,该方法,将不会被调用
    @Override
    public void parserConditionExpression(ExpressionAliasParser aliasParser) {
        if (StringUtils.isNotBlank(this.conditionExpression)) {
            return;
        }
        if (isNeedParserExpression()) {
            this.conditionExpression = aliasParser.parserExpression(expression);
        } else {
            this.conditionExpression = this.expression;
        }
    }

    @Override
    public int getOrder() {
        return order;
    }

    public boolean isNeedParserExpression() {
        return needParserExpression;
    }

    /**
     * 创建实际参与工作的表达式对象
     *
     * @param expression 表达式
     * @return 表达式对象
     */
    public ConditionExpression newWorkConditionExpression(String expression, int order, boolean needParserExpression) {
        ConditionExpressionImpl conditionExpression = new ConditionExpressionImpl(this.testCondition);
        conditionExpression.order = order;
        conditionExpression.expression = expression;
        conditionExpression.needParserExpression = needParserExpression;
        return conditionExpression;
    }

    /**
     * 创建实际参与工作的表达式对象
     *
     * @param expression 表达式
     * @return 表达式对象
     */
    public ConditionExpression newWorkExpressionJs(String expression, String jsFunctionName, String jsParams, String jsParamReal, String nodeId,String nodeName) {
        ConditionExpressionImpl conditionExpression = new ConditionExpressionImpl(this.testCondition);
        conditionExpression.order = order;
        conditionExpression.expression = expression;
        conditionExpression.needParserExpression = StringUtils.isNotEmpty(jsFunctionName);
        conditionExpression.jsFunctionName = jsFunctionName;
        if (StringUtils.isNotEmpty(jsParams)) {
            String[] jsParamsArray = jsParams.trim().split(",");
            // 将数组转换为 Set
            conditionExpression.jsParams = new HashSet<>(Arrays.asList(jsParamsArray));
        }

        if (StringUtils.isNotEmpty(jsParamReal)) {
            String[] paramsArray = jsParamReal.trim().split(",");
            conditionExpression.jsParamReal = new HashSet<>(Arrays.asList(paramsArray));
        }
        conditionExpression.nodeId = nodeId;
        conditionExpression.nodeName = nodeName;
        log.info("创建表达式对象,表达式为:{},jsFunctionName:{},jsParams:{},jsParamReal:{},nodeId:{}", expression, jsFunctionName, jsParams, jsParamReal, nodeId);
        return conditionExpression;
    }

    public String getExpression() {
        return expression;
    }

    public String getConditionExpression() {
        return conditionExpression;
    }
}