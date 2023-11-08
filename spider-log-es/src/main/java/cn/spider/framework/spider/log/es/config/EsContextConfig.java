package cn.spider.framework.spider.log.es.config;

import cn.spider.framework.spider.log.es.esx.EsContext;
import cn.spider.framework.spider.log.es.service.impl.SpiderFlowElementExampleServiceEsXImpl;
import cn.spider.framework.spider.log.es.service.impl.SpiderFlowExampleLogServiceEsXImpl;
import cn.spider.framework.spider.log.es.util.OkHttpUtil;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Import({CommonConfig.class})
@Configuration
public class EsContextConfig {
    @Bean
    public EsContext buildEsContext(Vertx vertx) throws NoSuchAlgorithmException, KeyManagementException {
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");

        String esAddr = localMap.get("es-ip-addr");
        EsContext esx = null;
        if (localMap.containsKey("es-username")) {
            String esName = localMap.get("es-username");
            String esPassword= localMap.get("es-password");
            // 校验是否跳过https的信任
            OkHttpClient client = null;
            if(localMap.containsKey("https_credit") && localMap.get("https_credit").equals("true")){
                client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .sslSocketFactory(OkHttpUtil.getIgnoreInitedSslContext().getSocketFactory(),OkHttpUtil.IGNORE_SSL_TRUST_MANAGER_X509)
                        .hostnameVerifier(OkHttpUtil.getIgnoreSslHostnameVerifier())
                        .build();
            }
            esx = new EsContext(esAddr,esName,esPassword,client);
        } else {
            esx = new EsContext(esAddr);
        }
        return esx;
    }

    @Bean
    public SpiderFlowElementExampleServiceEsXImpl buildSpiderFlowElementExampleServiceEsXImpl(EsContext esContext){
        return new SpiderFlowElementExampleServiceEsXImpl(esContext);
    }

    @Bean
    public SpiderFlowExampleLogServiceEsXImpl buildSpiderFlowExampleLogServiceEsXImpl(EsContext esContext){
        return new SpiderFlowExampleLogServiceEsXImpl(esContext);
    }
}
