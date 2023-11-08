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

import cn.spider.framework.flow.bus.ExpressionBus;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.exception.ExpressionException;
import cn.spider.framework.flow.util.GlobalUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 *
 * @author lykan
 */
public class SpelConditionExpression extends ConditionExpressionImpl implements ConditionExpression {

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    public SpelConditionExpression() {
        super((scopeData, exp) -> {
            if (StringUtils.isBlank(exp) || scopeData == null) {
                return false;
            }
            Boolean value;
            try {
                value = PARSER.parseExpression(exp).getValue(new ExpressionBus(scopeData), Boolean.class);
            } catch (Throwable e) {
                throw new ExpressionException(ExceptionEnum.EXPRESSION_INVOKE_ERROR,
                        GlobalUtil.format("{} expression: {}", ExceptionEnum.EXPRESSION_INVOKE_ERROR.getDesc(), exp), e);
            }
            return BooleanUtils.isTrue(value);
        });
    }

    @Override
    public boolean isNeedParserExpression() {
        return true;
    }

    @Override
    public boolean match(String expression) {
        return true;
    }
}
