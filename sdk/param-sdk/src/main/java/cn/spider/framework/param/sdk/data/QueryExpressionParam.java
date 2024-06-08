package cn.spider.framework.param.sdk.data;

public class QueryExpressionParam {
    private String expression;

    private String requestId;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public QueryExpressionParam(String expression, String requestId) {
        this.expression = expression;
        this.requestId = requestId;
    }
}
