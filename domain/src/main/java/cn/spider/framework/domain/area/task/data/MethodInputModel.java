package cn.spider.framework.domain.area.task.data;

import com.alibaba.fastjson.JSONObject;

public class MethodInputModel {
    private JSONObject inputParam;

    private Boolean resultIsException;
    /**
     * 场景
     */
    private String scene;

    /**
     * 场景
     */
    private String sceneCode;

    public JSONObject getInputParam() {
        return inputParam;
    }

    public void setInputParam(JSONObject inputParam) {
        this.inputParam = inputParam;
    }

    public Boolean getResultIsException() {
        return resultIsException;
    }

    public void setResultIsException(Boolean resultIsException) {
        this.resultIsException = resultIsException;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getSceneCode() {
        return sceneCode;
    }

    public void setSceneCode(String sceneCode) {
        this.sceneCode = sceneCode;
    }
}
