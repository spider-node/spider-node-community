package com.flow.cloud.start.role;

import cn.spider.framework.dev.ops.config.K8sConstant;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.exception.KstryException;
import com.flow.cloud.start.config.EvnConstant;
import com.flow.cloud.start.util.ExceptionMessage;
import com.flow.cloud.start.util.PropertyReader;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: com.flow.cloud.start.role
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-20  13:11
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
public class RoleManager {
    private Vertx vertx;

    public RoleManager(Vertx vertx) {
        this.vertx = vertx;
    }

    public void start() {
        loadConfig(this.vertx);
        SharedData sharedData = this.vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");
        // 当为空的情况下，抛出异常推出程序
        if (localMap.isEmpty()) {
            System.exit(1);
        }
        startRole(this.vertx);
    }

    private void loadConfig(Vertx vertx) {
        Map<String, String> spiderConf = PropertyReader.GetAllProperties("spiderConf.properties");
        switch (spiderConf.get("environment")) {
            case "dev":
                spiderConf.putAll(PropertyReader.GetAllProperties("spiderConf-dev.properties"));
                break;
            case "qa":
                spiderConf.putAll(PropertyReader.GetAllProperties("spiderConf-qa.properties"));
                break;
            case "prod":
                spiderConf.putAll(PropertyReader.GetAllProperties("spiderConf-prod.properties"));
                break;
            case "local":
                spiderConf.putAll(PropertyReader.GetAllProperties("spiderConf-local.properties"));


        }
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");
        localMap.putAll(spiderConf);

        String spiderAgentUrlHost = System.getenv(EvnConstant.SPIDER_AGENT_URL_HOST);
        if (StringUtils.isNotEmpty(spiderAgentUrlHost)) {
            localMap.put("spider_agent_url_host", spiderAgentUrlHost);
        }

        String spiderCodeAiUrl = System.getenv(EvnConstant.SPIDER_CODE_AI_URL);
        if (StringUtils.isNotEmpty(spiderCodeAiUrl)) {
            localMap.put("spider_code_ai_url", spiderCodeAiUrl);
        }

        String role = System.getenv(EvnConstant.ROLE);
        if (StringUtils.isNotEmpty(role)) {
            localMap.put("role", role);
        }

        String zkUrl = System.getenv(EvnConstant.ZK_ADDR);
        if (StringUtils.isNotEmpty(zkUrl)) {
            localMap.put("zk-addr", zkUrl);
        }

        String logType = System.getenv(EvnConstant.LOG_TYPE);
        if (StringUtils.isNotEmpty(logType)) {
            localMap.put("log_type", logType);
        }

        String esClusterName = System.getenv(EvnConstant.ES_CLUSTER_NAME);
        if (StringUtils.isNotEmpty(esClusterName)) {
            localMap.put("es_cluster_name", esClusterName);
        }

        // es-ip-addr
        String esIpAddr = System.getenv(EvnConstant.ES_IP_ADDR);
        if (StringUtils.isNotEmpty(esIpAddr)) {
            localMap.put("es_ip_addr", esIpAddr);
        }
        // https_credit
        String httpsCredit = System.getenv(EvnConstant.HTTPS_CREDIT);
        if (StringUtils.isNotEmpty(httpsCredit)) {
            localMap.put("es_https_credit", httpsCredit);
        }

        // es-username
        String esUsername = System.getenv(EvnConstant.ES_USERNAME);
        if (StringUtils.isNotEmpty(esUsername)) {
            localMap.put("es_username", esUsername);
        }

        // es-password
        String esPassword = System.getenv(EvnConstant.ES_PASSWORD);
        if (StringUtils.isNotEmpty(esPassword)) {
            localMap.put("es_password", esPassword);
        }
        // file_server_type
        String fileServerType = System.getenv(EvnConstant.FILE_SERVER_TYPE);
        if (StringUtils.isNotEmpty(fileServerType)) {
            localMap.put("file_server_type", fileServerType);
        }
        // minio_url
        String minioUrl = System.getenv(EvnConstant.MINIO_URL);
        if (StringUtils.isNotEmpty(minioUrl)) {
            localMap.put("minio_url", minioUrl);
        }

        // minio_access_key
        String minioAccessKey = System.getenv(EvnConstant.MINIO_ACCESS_KEY);
        if (StringUtils.isNotEmpty(minioAccessKey)) {
            localMap.put("minio_access_key", minioAccessKey);
        }

        // minio_secret_key
        String minioSecretKey = System.getenv(EvnConstant.MINIO_SECRET_KEY);
        if (StringUtils.isNotEmpty(minioSecretKey)) {
            localMap.put("minio_secret_key", minioSecretKey);
        }

        // minio_bucket_name
        String minioBucketName = System.getenv(EvnConstant.MINIO_BUCKET_NAME);
        if (StringUtils.isNotEmpty(minioBucketName)) {
            localMap.put("minio_bucket_name", minioBucketName);
        }

        // oss_endpoint
        String ossEndpoint = System.getenv(EvnConstant.OSS_ENDPOINT);
        if (StringUtils.isNotEmpty(ossEndpoint)) {
            localMap.put("oss_endpoint", ossEndpoint);
        }

        // oss_keyId
        String ossKeyId = System.getenv(EvnConstant.OSS_KEYID);
        if (StringUtils.isNotEmpty(ossKeyId)) {
            localMap.put("oss_key_id", ossKeyId);
        }

        // oss_keySecret
        String ossKeySecret = System.getenv(EvnConstant.OSS_KEYSECRET);
        if (StringUtils.isNotEmpty(ossKeySecret)) {
            localMap.put("oss_key_secret", ossKeySecret);
        }

        // oss_bucketName
        String ossBucketName = System.getenv(EvnConstant.OSS_BUCKETNAME);
        if (StringUtils.isNotEmpty(ossBucketName)) {
            localMap.put("oss_bucket_name", ossBucketName);
        }

        // bpmn_path
        String bpmnPath = System.getenv(EvnConstant.BPMN_PATH);
        if (StringUtils.isNotEmpty(bpmnPath)) {
            localMap.put("bpmn_path", bpmnPath);
        }

        // sdk_path
        String sdkPath = System.getenv(EvnConstant.SDK_PATH);
        if (StringUtils.isNotEmpty(sdkPath)) {
            localMap.put("sdk_path", sdkPath);
        }

        // function-port
        String functionPort = System.getenv(EvnConstant.FUNCTION_PORT);
        if (StringUtils.isNotEmpty(functionPort)) {
            localMap.put("function-port", functionPort);
        }

        String roleConfig = localMap.get("role");
        if (StringUtils.isEmpty(roleConfig)) {
            throw new KstryException(ExceptionEnum.SYSTEM_ROLE_ERROR);
        }

        // mysql-host
        String mysqlHost = System.getenv(EvnConstant.MYSQL_HOST);
        if (StringUtils.isNotEmpty(mysqlHost)) {
            localMap.put("mysql_host", mysqlHost);
        }

        // mysql-password
        String mysqlPassword = System.getenv(EvnConstant.MYSQL_PASSWORD);
        if (StringUtils.isNotEmpty(mysqlPassword)) {
            localMap.put("mysql_password", mysqlPassword);
        }

        // mysql-user
        String mysqlUser = System.getenv(EvnConstant.MYSQL_USER);
        if (StringUtils.isNotEmpty(mysqlUser)) {
            localMap.put("mysql_user", mysqlUser);
        }

        // mysql-port
        String mysqlPort = System.getenv(EvnConstant.MYSQL_PORT);
        if (StringUtils.isNotEmpty(mysqlPort)) {
            localMap.put("mysql_port", mysqlPort);
        }

        // mysql-database
        String mysqlDatabase = System.getenv(EvnConstant.MYSQL_DATABASE);
        if (StringUtils.isNotEmpty(mysqlDatabase)) {
            localMap.put("mysql_database", mysqlDatabase);
        }
        // mysql-url
        String mysqlUrl = System.getenv(EvnConstant.MYSQL_URL);
        if (StringUtils.isNotEmpty(mysqlUrl)) {
            localMap.put("mysql_url", mysqlUrl);
        }

        // mysql-driver-class-name
        String mysqlDriverClassName = System.getenv(EvnConstant.MYSQL_DRIVER_CLASS_NAME);
        if (StringUtils.isNotEmpty(mysqlDriverClassName)) {
            localMap.put("mysql_driver_class_name", mysqlDriverClassName);
        }

        // mysql-init-size
        String mysqlInitSize = System.getenv(EvnConstant.MYSQL_INIT_SIZE);
        if (StringUtils.isNotEmpty(mysqlInitSize)) {
            localMap.put("mysql_init_size", mysqlInitSize);
        }
        // mysql-min-idle
        String mysqlMinIdle = System.getenv(EvnConstant.MYSQL_MIN_IDLE);
        if (StringUtils.isNotEmpty(mysqlMinIdle)) {
            localMap.put("mysql_min_idle", mysqlMinIdle);
        }

        // limitation-interval
        String limitationInterval = System.getenv(EvnConstant.LIMITATION_INTERVAL);
        if (StringUtils.isNotEmpty(limitationInterval)) {
            localMap.put("limitation-interval", limitationInterval);
        }
        // limitation-number
        String limitationNumber = System.getenv(EvnConstant.LIMITATION_NUMBER);
        if (StringUtils.isNotEmpty(limitationNumber)) {
            localMap.put("limitation-number", limitationNumber);
        }

        // host_application_port
        String hostApplicationPort = System.getenv(EvnConstant.HOST_APPLICATION_PORT);
        if (StringUtils.isNotEmpty(hostApplicationPort)) {
            localMap.put("host_application_port", hostApplicationPort);
        }

        // K8S_BASE_URL
        String k8sBaseUrl = System.getenv(EvnConstant.K8S_BASE_URL);
        if (StringUtils.isNotEmpty(k8sBaseUrl)) {
            localMap.put("k8s_base_url", k8sBaseUrl);
        }
        // K8S_TOKEN_KEY
        String k8sTokenKey = System.getenv(EvnConstant.K8S_TOKEN_KEY);
        if (StringUtils.isNotEmpty(k8sTokenKey)) {
            localMap.put("k8s_token_key", k8sTokenKey);
        }

        // K8S_NAMESPACE
        String k8sNamespace = System.getenv(EvnConstant.K8S_NAMESPACE);
        if (StringUtils.isNotEmpty(k8sNamespace)) {
            localMap.put("k8s_namespace", k8sNamespace);
        }


        if (!localMap.containsKey("broker-ip")) {
            try {
                InetAddress address = InetAddress.getLocalHost();
                localMap.put("broker-ip", address.getHostAddress());
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }

        if (!localMap.containsKey("broker-name")) {
            try {
                InetAddress address = InetAddress.getLocalHost();
                localMap.put("broker-name", address.getHostName());

            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("broker-name {} ip {}", localMap.get("broker-name"), localMap.get("broker-ip"));
    }

    private void startRole(Vertx vertx) {
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");
        Integer instanceNum = Integer.parseInt(localMap.get("instance-number"));

        DeploymentOptions deployOptions = new DeploymentOptions()
                // verticle模式
                .setWorker(true)
                // 是否高可用
                .setHa(true)
                .setInstances(instanceNum);
        log.info("start-role");
        String controllerPath = "cn.spider.framework.controller.ControllerVerticle";
        String ui = "com.flow.cloud.start.ui.SpiderUiVerticle";
        vertx.deployVerticle(ui, deployOptions, res -> {
            if (res.succeeded()) {
                log.info("ui启动成功");
            } else {
                log.info("启动失败 {}",ExceptionMessage.getStackTrace(res.cause()));
            }
        });
        vertx.deployVerticle(controllerPath, deployOptions, res1 -> {
            if (res1.succeeded()) {
                log.info("controller启动成功");
            } else {
                log.info("启动失败 {}",ExceptionMessage.getStackTrace(res1.cause()));
            }
        });


    }
}