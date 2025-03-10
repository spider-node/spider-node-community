package cn.spider.framework.dev.ops;

import cn.spider.framework.common.utils.ExceptionMessage;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class K8sManager {

    private AppsV1Api api;

    private String namespace;

    public K8sManager(AppsV1Api api, String namespace) {
        this.api = api;
        this.namespace = namespace;
    }

    /**
     * 创建deployment
     *
     * @param yml 不糊的yaml 文件地址
     * @throws IOException  读取文件异常
     * @throws ApiException api调用的异常
     */
    public void createDeployment(String yml) {
        // 通过http的yml 文件创建 deployment
        try {
            V1Deployment deployment = Yaml.loadAs(yml, V1Deployment.class);
            // 校验在k8s中，这个deployment是否已经存在
            try {
                this.api.readNamespacedDeployment(deployment.getMetadata().getName(), namespace, null, null, null);
                // 如果存在，就选择升级
                this.updateDeployment(yml);
            } catch (ApiException e) {
                if (e.getCode() == 404) {
                    // 如果Deployment不存在，则创建新的Deployment
                    this.api.createNamespacedDeployment(namespace, deployment, null, null, null);
                } else {
                    // 其他ApiException异常，抛出异常
                    throw e;
                }
            }
        } catch (Exception e) {
            // 抛异常
            log.error("创建deployment异常信息为 {}", ExceptionMessage.getStackTrace(e));
        }
    }

    /**
     * 删除
     *
     * @param name deployment 名称
     * @throws ApiException api的异常
     */
    public void deleteDeployment(String name) throws ApiException {
        this.api.deleteNamespacedDeployment(name, namespace, null, null, null, null, null, null);
    }

    /**
     * 升级
     *
     * @param ymlUrl 需要升级的yml文件地址
     * @throws ApiException api的异常
     */
    public void updateDeployment(String ymlUrl) throws ApiException {
        V1Deployment deployment = Yaml.loadAs(ymlUrl, V1Deployment.class);
        this.api.replaceNamespacedDeployment(deployment.getMetadata().getName(), namespace, deployment, null, null, null);
    }

    /**
     * 调整副本数
     *
     * @param namespace 命令空间，一般写死为spider-vertx
     * @param yaml      部署的yaml
     * @param replicas  副本数
     * @throws ApiException api的异常
     */
    public void scaleDeployment(String namespace, String yaml, int replicas) throws ApiException {
        // 获取deployment
        V1Deployment deployment = Yaml.loadAs(yaml, V1Deployment.class);
        // 校验k8s中是否存在
        try {
            this.api.readNamespacedDeployment(deployment.getMetadata().getName(), namespace, null, null, null);
        } catch (ApiException e) {
            if (e.getCode() == 404) {
                // 如果Deployment不存在，则创建新的Deployment
                this.api.createNamespacedDeployment(namespace, deployment, null, null, null);
            } else {
                // 其他ApiException异常，抛出异常
                throw e;
            }
            return;
        }
        // 设置副本数
        deployment.getSpec().setReplicas(replicas);
        // 更新deployment
        this.api.replaceNamespacedDeployment(deployment.getMetadata().getName(), namespace, deployment, null, null, null);
    }
}
