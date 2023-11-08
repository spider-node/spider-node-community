package cn.spider.framework.spider.log.es.config;

import cn.spider.framework.spider.log.es.dao.SpiderFlowElementExampleLogDao;
import cn.spider.framework.spider.log.es.dao.SpiderFlowExampleLogDao;
import cn.spider.framework.spider.log.es.service.impl.SpiderFlowElementExampleServiceEsImpl;
import cn.spider.framework.spider.log.es.service.impl.SpiderFlowExampleLogServiceEsImpl;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.spider.log.es.config
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-23  16:40
 * @Description: TODO
 * @Version: 1.0
 */
@ComponentScan("cn.spider.framework.spider.log.es.dao.*")
@EnableElasticsearchRepositories(basePackages = "cn.spider.framework.spider.log.es.dao")
@Import({CommonConfig.class})
@Configuration
public class EsLogConfig {

    @Bean
    public RestHighLevelClient buildEsClient(Vertx vertx) {
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");
        String esAddr = localMap.get("es-ip-addr");
        // 创建Client连接对象
        String[] ips = esAddr.split(",");
        HttpHost[] httpHosts = new HttpHost[ips.length];
        for (int i = 0; i < ips.length; i++) {
            httpHosts[i] = HttpHost.create(ips[i]);
        }
        RestClientBuilder builder = RestClient.builder(httpHosts);

        if (localMap.containsKey("es-username") && StringUtils.isNotEmpty(localMap.get("es-username"))) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(localMap.get("es-username"), localMap.get("es-password")));

            // 连接延时配置
            builder.setRequestConfigCallback(requestConfigBuilder -> {
                requestConfigBuilder.setConnectTimeout(30 * 1000);
                requestConfigBuilder.setSocketTimeout(20 * 1000);
                requestConfigBuilder.setConnectionRequestTimeout(15 * 1000);
                return requestConfigBuilder;
            });
            // 连接数配置
            builder.setHttpClientConfigCallback(httpClientBuilder -> {
                httpClientBuilder.setMaxConnTotal(20);
                httpClientBuilder.setMaxConnPerRoute(10);
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                return httpClientBuilder;
            });
        }
        return new RestHighLevelClient(builder);

    }

    @Bean("elasticsearchTemplate")
    public ElasticsearchRestTemplate elasticsearchRestTemplate(RestHighLevelClient client) {
        return new ElasticsearchRestTemplate(client);
    }

    @Bean
    public SpiderFlowExampleLogServiceEsImpl buildSpiderFlowExampleLogServiceEsImpl(ElasticsearchRestTemplate template, SpiderFlowExampleLogDao spiderFlowExampleLogDao) {
        return new SpiderFlowExampleLogServiceEsImpl(template,spiderFlowExampleLogDao);
    }

    @Bean
    public SpiderFlowElementExampleServiceEsImpl buildSpiderFlowElementExampleServiceEsImpl(ElasticsearchRestTemplate template, SpiderFlowElementExampleLogDao spiderFlowElementExampleLogDao){
        return new SpiderFlowElementExampleServiceEsImpl(template,spiderFlowElementExampleLogDao);
    }



}
