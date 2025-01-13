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
     * @param yml 不糊的yaml 文件地址
     * @throws IOException 读取文件异常
     * @throws ApiException api调用的异常
     */
    public void createDeployment(String yml) {
        // 通过http的yml 文件创建 deployment
        try {
            V1Deployment deployment = Yaml.loadAs(yml, V1Deployment.class);
            this.api.createNamespacedDeployment(namespace, deployment, null, null, null);
        } catch (Exception e) {
            // 抛异常
            log.error("创建deployment异常信息为 {}", ExceptionMessage.getStackTrace(e));
        }


        // 将YAML转换为Kubernetes资源对象

    }

    private InputStream getInputStream(String ymlUrl) throws IOException {
        try {
            // 创建URL对象
            URL url = new URL(ymlUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            // 确认服务器响应是HTTP OK
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 打开输入流来读取远程文件内容
                try (InputStream inputStream = httpConn.getInputStream()) {
                    return inputStream;
                }
            } else {
                throw new RuntimeException("获取文件异常");
            }
        } catch (Exception e) {
            // 抛异常
            throw new RuntimeException("获取文件异常", e);
        }
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
     * @param namespace 命令空间，一般写死为spider-vertx
     * @param name Deployment的name
     * @param replicas 副本数
     * @throws ApiException api的异常
     */
    public void scaleDeployment(String namespace,String name,int replicas) throws ApiException {
        // 获取deployment
        V1Deployment deployment = this.api.readNamespacedDeployment(name, namespace, null, null, null);
        // 设置副本数
        deployment.getSpec().setReplicas(replicas);
        // 更新deployment
        this.api.replaceNamespacedDeployment(name, namespace, deployment, null, null, null);
    }
}
