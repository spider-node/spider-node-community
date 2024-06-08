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

import cn.spider.framework.flow.bus.StoryBus;
import cn.spider.framework.flow.load.SpringBeanUtils;
import cn.spider.framework.flow.util.AssertUtil;
import cn.spider.framework.param.sdk.data.QueryExpressionParam;
import cn.spider.framework.param.sdk.data.QueryExpressionResult;
import cn.spider.framework.param.sdk.interfaces.ParamInterface;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * @author dds
 */
public class ConditionExpressionImpl implements ConditionExpression {

    /**
     * 原表达式
     */
    private String expression;

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
        if(Objects.isNull(this.paramInterface)){
            this.paramInterface = SpringBeanUtils.getBean(ParamInterface.class);
        }
    }

    @Override
    public Future<Boolean> condition(StoryBus storyBus) {
        if (storyBus == null) {
            Future.succeededFuture(false);
        }
        AssertUtil.notBlank(this.expression);
        // 进行查询 param角色获取true,false
        Promise<Boolean> promise = Promise.promise();
        QueryExpressionParam queryExpressionParam = new QueryExpressionParam(this.expression,storyBus.queryRequestId());
        Future<JsonObject> future = paramInterface.getParamValue(JsonObject.mapFrom(queryExpressionParam));
        future.onSuccess(suss->{
            QueryExpressionResult expressionResult = suss.mapTo(QueryExpressionResult.class);
            promise.complete(expressionResult.getResult());
        }).onFailure(fail->{
            promise.fail(fail);
        });
        return promise.future();
    }

    @Override
    public boolean match(String expression) {
        return false;
    }

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

    public String getExpression() {
        return expression;
    }

    public String getConditionExpression() {
        return conditionExpression;
    }
}