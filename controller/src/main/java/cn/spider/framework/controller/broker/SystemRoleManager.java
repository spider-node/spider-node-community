package cn.spider.framework.controller.broker;

import cn.spider.framework.common.utils.ExceptionMessage;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: com.flow.cloud.start.role
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-20  13:11
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
public class SystemRoleManager {
    private Vertx vertx;

    private final String flowNode = "cn.spider.framework.flow.SpiderCoreVerticle";

    private final String gateway = "cn.spider.framework.gateway.GatewayVerticle";

    private final String linkerServer = "cn.spider.framework.linker.server.LinkerMainVerticle";

    private final String transactionCore = "cn.spider.framework.transaction.server.TransactionServerVerticle";

    private final String logPath = "cn.spider.framework.spider.log.es.LogVerticle";

    private String domain = "cn.spider.framework.domain.area.AreaVerticle";

    private final String paramPatch = "cn.spider.framework.spider.param.ParamVerticle";

    private final String hostApplication = "cn.spider.node.host.plugin.center.MainVerticle";

    private final String deploy = "cn.spider.framework.dev.ops.MainVerticle";

    private String hostApplicationDeployId;

    private String linkerServerDeployId;

    private String domainDeployId;

    private String logDeployId;

    public SystemRoleManager(Vertx vertx) {
        this.vertx = vertx;
    }


    public void startBaseSystemRole() {
        DeploymentOptions deployOptions = new DeploymentOptions()
                // verticle模式
                .setWorker(true)
                // 是否高可用
                .setHa(true)
                .setInstances(1);
        log.info("start-base-system-role");
        startRole(this.flowNode, "flow", deployOptions);
        startRole(this.gateway, "gateway", deployOptions);
        startRole(this.transactionCore, "transactionCore", deployOptions);
        startRole(this.logPath, "log", deployOptions);
        DeploymentOptions deployOptions1 = new DeploymentOptions()
                // verticle模式
                .setWorker(true)
                // 是否高可用
                .setHa(true)
                .setInstances(6);
        startRole(this.paramPatch, "paramPatch", deployOptions1);

    }

    // 卸载base能力
    public void destroyBaseSystemRole() {
        destroyRole(this.flowNode, "flow");
        destroyRole(this.gateway, "gateway");
        destroyRole(this.transactionCore, "transactionCore");
        destroyRole(this.paramPatch, "paramPatch");
    }

    public void StartLeaderSystemRole() {
        DeploymentOptions deployOptions = new DeploymentOptions()
                // verticle模式
                .setWorker(true)
                // 是否高可用
                .setHa(false)
                .setInstances(1);
        startRole(this.linkerServer, "scheduler", deployOptions);
        startRole(this.domain, "domain", deployOptions);
        startRole(this.hostApplication, "hostApplication", deployOptions);
        startRole(this.deploy, "devOps", deployOptions);
        //destroyBaseSystemRole();
    }

    private void startRole(String path, String role, DeploymentOptions deployOptions) {
        this.vertx.deployVerticle(path, deployOptions, res1 -> {
            if (res1.succeeded()) {
                log.info("角色 {} 启动成功", role);
                setUpLeaderRoleId(role, res1.result());
            } else {
                log.info("角色 {} 启动失败,原因为 {}", role, ExceptionMessage.getStackTrace(res1.cause()));
            }
        });
    }

    private void setUpLeaderRoleId(String role, String deployId) {
        switch (role) {
            case "hostApplication":
                this.hostApplicationDeployId = deployId;
                break;
            case "scheduler":
                this.linkerServerDeployId = deployId;
                break;
            case "domain":
                this.domainDeployId = deployId;
                break;
            case "log":
                this.logDeployId = deployId;
        }
    }

    public void destroySystemLeaderRole() {
        if (StringUtils.isEmpty(this.hostApplicationDeployId)) {
            destroyRole(this.hostApplicationDeployId, "hostApplication");
        }
        if (StringUtils.isNotEmpty(this.linkerServerDeployId)) {
            destroyRole(this.linkerServerDeployId, "scheduler");
        }
        if (StringUtils.isNotEmpty(this.domainDeployId)) {
            destroyRole(this.domainDeployId, "domain");
        }
        if (StringUtils.isNotEmpty(this.logDeployId)) {
            destroyRole(this.logDeployId, "logPath");
        }
    }

    private void destroyRole(String deploymentID, String role) {
        this.vertx.undeploy(deploymentID, res -> {
            if (res.succeeded()) {
                log.info("角色 {} 销毁成功", role);
            } else {
                log.info("角色 {} 销毁失败,原因为 {}", role, ExceptionMessage.getStackTrace(res.cause()));
            }
        });
    }
}