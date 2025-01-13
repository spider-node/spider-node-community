package cn.spider.framework.dev.ops.config;

import cn.spider.framework.dev.ops.K8sManager;
import cn.spider.framework.dev.ops.MainVerticle;
import cn.spider.framework.dev.ops.handler.DeleteDeployHandler;
import cn.spider.framework.dev.ops.handler.FunctionDeployHandler;
import cn.spider.framework.dev.ops.handler.ScaleUpHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.util.Config;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class K8sConfig {

    @Bean
    public Vertx buildVertx() {
        return MainVerticle.clusterVertx;
    }

    /**
     * 构造 AppsV1Api
     * @param vertx 用来获取spider中的配置信息
     * @return AppsV1Api
     * @throws IOException
     */
    @Bean
    public AppsV1Api buildAppsV1Api(Vertx vertx) throws IOException {
        LocalMap<String, String> localMap = vertx.sharedData().getLocalMap("config");
        String bseK8sUrl = System.getenv(K8sConstant.K8S_URL_KEY);
        String baseToken = System.getenv(K8sConstant.K8S_TOKEN_KEY);
        ApiClient client = Config.defaultClient();
        if(StringUtils.isNotEmpty(bseK8sUrl)){
            client.setBasePath(bseK8sUrl);
        }else {
            client.setBasePath(localMap.get(K8sConstant.K8S_URL_KEY));
        }
        if(StringUtils.isNotEmpty(baseToken)){
            client.setApiKey(baseToken);

        }else {
            client.setApiKey(localMap.get(K8sConstant.K8S_TOKEN_KEY));
        }
        client.setApiKeyPrefix("Bearer"); // 设置Token前缀
        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client);
        return new AppsV1Api(client);
    }

    @Bean
    public K8sManager buildK8sManager(AppsV1Api appsV1Api,Vertx vertx) {
        LocalMap<String, String> localMap = vertx.sharedData().getLocalMap("config");
        String namespace = System.getenv(K8sConstant.K8S_NAMESPACE);
        if(StringUtils.isEmpty(namespace)){
            namespace = localMap.get(K8sConstant.K8S_NAMESPACE);
        }
        return new K8sManager(appsV1Api,namespace);
    }

    @Bean
    public DeleteDeployHandler buildDeleteDeployHandler(K8sManager k8sManager, Vertx vertx) {
        return new DeleteDeployHandler(vertx.eventBus(), k8sManager);
    }

    @Bean
    public FunctionDeployHandler buildFunctionDeployHandler(K8sManager k8sManager, Vertx vertx) {
        return new FunctionDeployHandler(vertx.eventBus(), k8sManager);
    }

    @Bean
    public ScaleUpHandler buildScaleUpHandler(K8sManager k8sManager, Vertx vertx) {
        return new ScaleUpHandler(vertx.eventBus(), k8sManager);
    }
}
