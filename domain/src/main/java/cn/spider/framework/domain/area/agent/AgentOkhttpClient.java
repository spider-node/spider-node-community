package cn.spider.framework.domain.area.agent;

import com.google.common.base.Preconditions;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
public class AgentOkhttpClient {
    private OkHttpClient okHttpClient;

    private String querySonAreaInfoUrl;

    private String deployPluginUrl;

    private MediaType mediaType;

    public AgentOkhttpClient(OkHttpClient okHttpClient, String agentPrefix) {
        this.okHttpClient = okHttpClient;
        this.querySonAreaInfoUrl = agentPrefix + "/areaDomain/query_son_area_info";
        this.deployPluginUrl = agentPrefix + "/code_agent/deploy";
        this.mediaType = MediaType.parse("application/json; charset=UTF-8");
    }

    public JsonObject querySonArea(JsonObject param) {
        try {
            return sendAgent(param,this.querySonAreaInfoUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonObject deployPlugin(JsonObject param){
        try {
            return sendAgent(param,this.deployPluginUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private JsonObject sendAgent(JsonObject param, String url) throws IOException {
        // 构造请求数据
        RequestBody requestBody = RequestBody.create(this.mediaType, param.toString());
        Request request = new Request.Builder()
                // 标识为 GET 请求
                .post(requestBody)
                // 设置请求路径
                .url(url)
                // 添加头信息
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = okHttpClient.newCall(request).execute();

        ResponseBody responseBody = response.body();
        okio.BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        okio.Buffer buffer = source.buffer();

        Charset charset = StandardCharsets.UTF_8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(StandardCharsets.UTF_8);
        }
        JsonObject result = new JsonObject(buffer.clone().readString(charset));

        if (result.getInteger("code") == 0) {
            return result.getJsonObject("data");
        }
        String error = result.toString();
        log.error("run_spider_param {} error {}", param.toString(), error);
        Preconditions.checkArgument(false, error);
        return null;
    }
}
