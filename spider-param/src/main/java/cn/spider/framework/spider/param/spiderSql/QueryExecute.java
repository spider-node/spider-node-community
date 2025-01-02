package cn.spider.framework.spider.param.spiderSql;

import cn.spider.framework.spider.param.manager.ParamExampleManager;
import io.vertx.core.json.JsonObject;
import java.util.Map;

public class QueryExecute {

    private ParamExampleManager paramExampleManager;

    public JsonObject query(String sql, Map<String, String> tableExpression, String requestId) throws Exception {
        if (tableExpression.isEmpty()) {
            return null;
        }

        return null;
    }
}
