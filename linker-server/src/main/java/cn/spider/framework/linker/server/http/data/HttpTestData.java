package cn.spider.framework.linker.server.http.data;

import io.vertx.core.json.JsonObject;
import lombok.Data;

import java.util.Map;
import java.util.Objects;

@Data
public class HttpTestData {
    private String httpUrl;

    private Map<String,String> httpHeader;

    private String httpType;

    private JsonObject param;

    private Boolean isHttps;

    public Boolean getHttps() {
        return isHttps;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        if (httpUrl.contains("https")) {
            this.isHttps = true;
        } else {
            this.isHttps = false;
        }
        this.httpUrl = httpUrl;
    }

    public Map<String, String> getHttpHeader() {
        return httpHeader;
    }

    public void setHttpHeader(Map<String, String> httpHeader) {
        this.httpHeader = httpHeader;
    }

    public String getHttpType() {
        return httpType;
    }

    public void setHttpType(String httpType) {
        this.httpType = httpType;
    }

    public JsonObject getParam() {
        return param;
    }

    public void setParam(Object param) {
        if (Objects.isNull(param)) {
            return;
        }
        this.param = JsonObject.mapFrom(param);
    }
}
