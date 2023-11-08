package cn.spider.framework.spider.log.es.common;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.spider.log.es.common
 * @Author: dengdongsheng
 * @CreateTime: 2023-10-06  18:55
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
public class StarRocksWrite {

    private String srLoadUrl;

    private String srUserName;

    private String srPasswd;

    private String srDb;

    public StarRocksWrite(String srLoadUrl, String srUserName, String srPasswd, String srDb) {
        this.srLoadUrl = srLoadUrl;
        this.srUserName = srUserName;
        this.srPasswd = srPasswd;
        this.srDb = srDb;
    }

    public void sendData(String srTable, String content,String jsonpaths,String columns) throws Exception {
        final String loadUrl = String.format("http://%s/api/%s/%s/_stream_load",
                srLoadUrl,
                srDb,
                srTable);

        final HttpClientBuilder httpClientBuilder = HttpClients
                .custom()
                .setRedirectStrategy(new DefaultRedirectStrategy() {
                    @Override
                    protected boolean isRedirectable(String method) {
                        return true;
                    }
                });

        try (CloseableHttpClient client = httpClientBuilder.build()) {
            HttpPut put = new HttpPut(loadUrl);
            StringEntity entity = new StringEntity(content, "UTF-8");
            put.setHeader(HttpHeaders.EXPECT, "100-continue");
            put.setHeader(HttpHeaders.AUTHORIZATION, basicAuthHeader(srUserName, srPasswd));
            put.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            put.setHeader("jsonpaths",jsonpaths);
            put.setHeader("columns",columns);
            put.setHeader("strip_outer_array", "true");
            put.setHeader("format", "json");
            put.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(put)) {
                String loadResult = "";
                if (response.getEntity() != null) {
                    loadResult = EntityUtils.toString(response.getEntity());
                }
                final int statusCode = response.getStatusLine().getStatusCode();
                // statusCode 200 just indicates that starrocks be service is ok, not stream load
                // you should see the output content to find whether stream load is success
                if (statusCode != 200) {
                    throw new IOException(
                            String.format("Stream load failed, statusCode=%s load result=%s", statusCode, loadResult));
                }

                log.info(loadResult);
            }
        }
    }

    private String basicAuthHeader(String username, String password) {
        final String tobeEncode = username + ":" + password;
        byte[] encoded = Base64.encodeBase64(tobeEncode.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encoded);
    }
}
