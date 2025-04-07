package cn.spider.framework.dev.ops;

import cn.spider.framework.common.utils.ExceptionMessage;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1LabelSelectorRequirement;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class K8sManager {

    private AppsV1Api api;
    private CoreV1Api coreApi; // 新增CoreV1Api实例

    private String namespace;

    public K8sManager(AppsV1Api api, CoreV1Api coreApi, String namespace) { // 修改构造函数，增加CoreV1Api参数
        this.api = api;
        this.coreApi = coreApi; // 初始化CoreV1Api实例
        this.namespace = namespace;
    }

    /**
     * 创建deployment
     *
     * @param yml 不糊的yaml 文件地址
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

    /**
     * 获取pod信息
     * @param deploymentName
     * @return
     * @throws ApiException
     */
    public List<Map<String, String>> getDeploymentPods(String deploymentName) throws ApiException {
        // 获取Deployment并检查空值
        V1Deployment deployment = this.api.readNamespacedDeployment(deploymentName, namespace, null, null, null);
        if (deployment == null || deployment.getSpec() == null) {
            throw new ApiException("Deployment not found or invalid spec");
        }

        // 获取LabelSelector并构建选择器字符串
        V1LabelSelector labelSelector = deployment.getSpec().getSelector();
        String labelSelectorStr = null;
        if (labelSelector != null) {
            try {
                labelSelectorStr = labelSelector.toString(); // 自动转换为标准格式
            } catch (Exception e) {
                throw new ApiException("Failed to parse label selector: " + e.getMessage());
            }
        }

        // 正确传递参数（labelSelector在第6个位置）
        V1PodList podList = this.coreApi.listNamespacedPod(
                namespace,
                null,   // pretty
                null,   // allowWatchBookmarks
                null,   // _continue
                null,   // fieldSelector
                labelSelectorStr,  // 正确位置：labelSelector
                null,   // limit
                null,   // resourceVersion
                null,   // resourceVersionMatch
                null,   // timeoutSeconds
                null    // watch
        );

        // 提取Pod信息
        return podList.getItems().stream()
                .map(pod -> {
                    Map<String, String> info = new HashMap<>();
                    info.put("name", pod.getMetadata().getName());
                    info.put("ip", pod.getStatus().getPodIP());
                    return info;
                })
                .collect(Collectors.toList());
    }
}
