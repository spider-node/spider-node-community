package cn.spider.framework.dev.ops;

import cn.spider.framework.dev.ops.config.K8sConstant;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class K8sManager {

    private AppsV1Api api;

    private String namespace;

    public K8sManager(AppsV1Api api) {
        this.namespace = System.getenv(K8sConstant.K8S_NAMESPACE);
        this.api = api;
    }

    /**
     * 创建deployment
     * @param ymlUrl 不糊的yaml 文件地址
     * @throws IOException 读取文件异常
     * @throws ApiException api调用的异常
     */
    public void createDeployment(String ymlUrl) throws IOException, ApiException {
        // 通过http的yml 文件创建 deployment
        InputStream inputStream = Files.newInputStream(Paths.get(ymlUrl));
        org.yaml.snakeyaml.Yaml snakeYaml = new  org.yaml.snakeyaml.Yaml();
        Object obj = snakeYaml.load(inputStream);
        // 将YAML转换为Kubernetes资源对象
        V1Deployment deployment = Yaml.loadAs(obj.toString(), V1Deployment.class);
        this.api.createNamespacedDeployment(namespace, deployment, null, null, null,null);
    }

    /**
     * 删除
     * @param name deployment 名称
     * @throws ApiException api的异常
     */
    public void deleteDeployment(String name) throws ApiException {
        this.api.deleteNamespacedDeployment(name, namespace, null, null, null, null, null, null);
    }

    /**
     * 调整副本数
     * @param name Deployment的name
     * @param replicas 副本数
     * @throws ApiException api的异常
     */
    public void scaleDeployment(String name,int replicas) throws ApiException {
        // 获取deployment
        V1Deployment deployment = this.api.readNamespacedDeployment(name, namespace, null);
        // 设置副本数
        deployment.getSpec().setReplicas(replicas);
        // 更新deployment
        this.api.replaceNamespacedDeployment(name, namespace, deployment, null, null, null,null);
    }
}
