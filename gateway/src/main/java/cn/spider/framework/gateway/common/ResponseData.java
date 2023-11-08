package cn.spider.framework.gateway.common;

import cn.spider.framework.common.utils.ExceptionMessage;
import com.alibaba.fastjson.JSON;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author spider-man
 * function:
 * @Date: 2022/02/07/ 14:40
 * @Description
 */
@Slf4j
public class ResponseData {
    private String msg;

    private Object data;

    private int code;

    public ResponseData(String msg, Object data, int code) {
        this.msg = msg;
        this.data = data;
        this.code = code;
    }

    public ResponseData(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    public static String suss(JsonObject result){
        JsonObject data = new JsonObject();
        data.put("code",0);
        data.put("msg","成功");
        data.put("data",result);
        return data.toString();
    }

    public static String sussJson(JsonObject result){
        JsonObject data = new JsonObject();
        data.put("code",0);
        data.put("msg","成功");
        data.put("data",result.getJsonArray("functions"));
        return data.toString();
    }

    public static String sussJsonArry(JsonArray result){
        JsonObject data = new JsonObject();
        data.put("code",0);
        data.put("msg","成功");
        data.put("data",result);
        return data.toString();
    }

    public static String sussObject(Object object){
        JsonObject data = new JsonObject();
        data.put("code",0);
        data.put("msg","成功");
        data.put("data",object);
        return data.toString();
    }

    public static String suss(){
        return JSON.toJSONString(new ResponseData("suss",0));
    }



    public static String fail(Throwable throwable){
        return JSON.toJSONString(new ResponseData(ExceptionMessage.getStackTrace(throwable),null,400));
    }

    public static String fail(String fail){
        return JSON.toJSONString(new ResponseData(fail,null,400));
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
