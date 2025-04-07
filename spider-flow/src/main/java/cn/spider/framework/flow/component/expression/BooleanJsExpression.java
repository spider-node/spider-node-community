package cn.spider.framework.flow.component.expression;

public class BooleanJsExpression extends ConditionExpressionImpl implements ConditionExpression {

    public BooleanJsExpression() {
        super((scopeData, exp) -> true);
    }
}
