package cn.spider.framework.linker.sdk.data;

import com.alibaba.fastjson.JSONObject;
import io.vertx.core.json.JsonObject;

/**
 * @program: spider-node
 * @description: 执行返回类
 * @author: dds
 * @create: 2023-03-02 13:20
 */
public class LinkerServerResponse {
    /**
     * 执行code
     */
    private ResultCode resultCode;
    /**
     * 返回参数
     */
    private JSONObject resultData;
    /**
     * 异常
     */
    private String exceptional;

    public ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public JSONObject getResultData() {
        return resultData;
    }

    public void setResultData(JSONObject resultData) {
        this.resultData = resultData;
    }

    public String getExceptional() {
        return exceptional;
    }

    public void setExceptional(String exceptional) {
        this.exceptional = exceptional;
    }
}
