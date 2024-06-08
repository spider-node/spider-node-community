package cn.spider.framework.linker.client.okhttp;

import cn.spider.framework.common.utils.ExceptionMessage;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * @BelongsProject: bms_middle_platform
 * @BelongsPackage: com.hope.saas.bms.middle.platform.spider
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-13  23:06
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
public class SpiderClient {
    private WebClient webClient;

    private String spiderServerAddr;

    private Integer spiderServerPort;

    private OkHttpClient httpClient;

    private String spiderServerFinishAddr;

    public SpiderClient(WebClient webClient,
                        String spiderServerAddr,
                        Integer spiderServerPort) {
        this.webClient = webClient;
        this.spiderServerAddr = spiderServerAddr;
        this.spiderServerPort = spiderServerPort;
        httpClient = new OkHttpClient.Builder()
                // 设置连接超时时间
                .connectTimeout(Duration.ofSeconds(30))
                // 设置读超时时间
                .readTimeout(Duration.ofSeconds(60))
                // 设置写超时时间
                .writeTimeout(Duration.ofSeconds(60))
                // 设置完整请求超时时间
                .callTimeout(Duration.ofSeconds(120))
                // 添加一个拦截器
                .eventListener(new EventListener() {
                    @Override
                    public void callEnd(Call call) {
                        super.callEnd(call);
                    }
                })
                // 添加一个拦截器
                // 注册事件监听器
                .build();
        this.spiderServerFinishAddr = new StringBuilder("http://").append(this.spiderServerAddr).append(":").append(this.spiderServerPort).append("/start/function").toString();
    }

    public JsonObject startFunctionOkhttp(JsonObject param) {
        MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
        // 构造请求数据
        RequestBody requestBody = RequestBody.create(mediaType, param.toString());
        Request request = new Request.Builder()
                // 标识为 GET 请求
                .post(requestBody)
                // 设置请求路径
                .url(spiderServerFinishAddr)
                // 添加头信息
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("执行失败" + response);

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
            } else {
                throw new RuntimeException("请求报错 {}" + result.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用vertx异步执行
     * @param param
     * @return
     */
    public Future<JsonObject> starFunction(JsonObject param) {
        Promise<JsonObject> promise = Promise.promise();

        webClient.post(spiderServerPort, spiderServerAddr, "/start/function")
                .putHeader("Content-Type", "application/json")

                .sendJsonObject(param)
                .onSuccess(res -> {
                    JsonObject result = null;
                    try {
                        result = res.bodyAsJsonObject();
                        if (result.getInteger("code") == 0) {
                            log.info("请求成功的参数为 {}", param.toString());
                        } else {
                            log.info("执行失败的异常数据 {}", res.bodyAsJsonObject().toString());
                            promise.fail("执行失败" + res.bodyAsJsonObject().toString());
                        }
                    } catch (Exception e) {
                        result = new JsonObject();
                    }
                    promise.complete(result);
                })
                .onFailure(fail -> {
                    log.error("请求的功能参数为 {} 执行失败的信息为 {}", param.toString(), ExceptionMessage.getStackTrace(fail));
                    promise.fail(fail);
                });
        return promise.future();
    }
}
