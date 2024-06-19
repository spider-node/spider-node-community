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

    public void start(){
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
        switch (spiderConf.get("environment")){
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

        String[] roles = roleConfig.split(",");
        for (String role : roles) {
            switch (role) {
                case "gateway":
                    localMap.put("gateway", "1");
                    break;
                case "broker":
                    localMap.put("flow-node", "1");
                    localMap.put("scheduler", "1");
                    localMap.put("transaction", "1");
                    localMap.put("controller", "1");
                    localMap.put("area", "1");
                    localMap.put("param", "1");
                    localMap.put("refresh", "1");
                    break;
                case "ui":
                    localMap.put("ui", "1");
                    break;
                case "log":
                    localMap.put("log", "1");
                    break;
            }
        }
    }

    /**
     * 启动各个角色
     *
     * @param path
     * @param role
     * @param vertx
     * @param
     */
    private void startRole(String path, String role, Vertx vertx, DeploymentOptions deployOptions) {
        vertx.deployVerticle(path, deployOptions, res1 -> {
            if (res1.succeeded()) {
                log.info("角色 {} 启动成功", role);
            } else {
                log.info("角色 {} 启动失败,原因为 {}", role, ExceptionMessage.getStackTrace(res1.cause()));
            }
        });

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
                for (String role : localMap.keySet()) {
                    switch (role) {
                        case "ui":
                            String ui = "com.flow.cloud.start.ui.SpiderUiVerticle";
                            startRole(ui, role, vertx, deployOptions);
                            break;
                        case "gateway":
                            // 启动网关
                            String gateway = "cn.spider.framework.gateway.GatewayVerticle";
                            startRole(gateway, role, vertx, deployOptions);
                            break;
                        case "flow-node":
                            String flowNode = "cn.spider.framework.flow.SpiderCoreVerticle";
                            startRole(flowNode, role, vertx, deployOptions);
                            break;
                        case "scheduler":
                            String linkerServer = "cn.spider.framework.linker.server.LinkerMainVerticle";
                            startRole(linkerServer, role, vertx, deployOptions);
                            break;
                        case "transaction":
                            String transactionCore = "cn.spider.framework.transaction.server.TransactionServerVerticle";
                            startRole(transactionCore, role, vertx, deployOptions);
                            break;
                        case "log":
                            String logPath = "cn.spider.framework.spider.log.es.LogVerticle";
                            startRole(logPath, role, vertx, deployOptions);
                            break;
                        case "area":
                            String areaPatch = "cn.spider.framework.domain.area.AreaVerticle";
                            startRole(areaPatch, role, vertx, deployOptions);
                            break;
                        case "param":
                            String paramPatch = "cn.spider.framework.spider.param.ParamVerticle";
                            startRole(paramPatch, role, vertx, deployOptions);
                            break;
                        case "refresh":
                            String refresh = "cn.spider.framework.area.method.param.MainVerticle";
                            startRole(refresh, role, vertx, deployOptions);
                            break;
                    }
                }
            } else {
                log.info("启动失败");
            }
        });




    }
}