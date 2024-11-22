package com.flow.cloud.start.role;

import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.exception.KstryException;
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
        String roleConfig = localMap.get("role");
        if (StringUtils.isEmpty(roleConfig)) {
            throw new KstryException(ExceptionEnum.SYSTEM_ROLE_ERROR);
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
        vertx.deployVerticle(controllerPath, deployOptions, res1 -> {
            if (res1.succeeded()) {
                log.info("controller启动成功");
            } else {
                log.info("启动失败 {}",ExceptionMessage.getStackTrace(res1.cause()));
            }
        });


    }
}