package cn.spider.framework.domain.area.task.data;

import com.alibaba.fastjson.JSONObject;

public class CaseSqlModel {
    private String sql;

    private JSONObject param;

    private String sceneCode;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public JSONObject getParam() {
        return param;
    }

    public void setParam(JSONObject param) {
        this.param = param;
    }

    public String getSceneCode() {
        return sceneCode;
    }

    public void setSceneCode(String sceneCode) {
        this.sceneCode = sceneCode;
    }
}
