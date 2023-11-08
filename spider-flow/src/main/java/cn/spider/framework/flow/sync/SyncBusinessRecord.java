package cn.spider.framework.flow.sync;

import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.container.sdk.interfaces.BusinessService;
import cn.spider.framework.container.sdk.interfaces.ContainerService;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.sync
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-12  15:46
 * @Description: 推送事件
 * @Version: 1.0
 */
@Slf4j
public class SyncBusinessRecord {
    private EventBus eventBus;

    private String brokerRole;

    private final String BROKER_LEADER = "leader";

    private BusinessService businessService;

    private ContainerService containerService;

    public SyncBusinessRecord(Vertx vertx,BusinessService businessService,ContainerService containerService) {
        this.eventBus = vertx.eventBus();
        this.brokerRole = BrokerInfoUtil.queryBrokerName(vertx);
        this.businessService = businessService;
        this.containerService = containerService;
        if(StringUtils.equals(brokerRole, BROKER_LEADER)){
            return;
        }
        // 只有-follower 才会进行监听
        syncRegisterBusiness();
        syncBusinessDerail();
        syncConfigureWeight();
        syncDeployBpmn();
        syncLoaderClass();
    }

    /**
     * 同步-注册业务功能同步
     */
    public void syncRegisterBusiness() {
        MessageConsumer<String> consumer = eventBus.consumer(Constant.BUSINESS_REGISTER_FUNCTION);
        consumer.handler(message -> {
            // 同步注册功能
            JsonObject param = new JsonObject(message.body());
            Future<JsonObject> registerFuture = businessService.registerFunction(param);
            registerFuture.onSuccess(suss -> {
                // 同步成功
                log.info("同步成功的消息体 {}",message.body());
            }).onFailure(fail -> {
                // 同步失败
                log.error("同步失败的消息体为 {} 异常为 {}",message.body(), ExceptionMessage.getStackTrace(fail));
            });
        });
    }

    /**
     * 同步-配置开关
     */
    public void syncBusinessDerail(){
        MessageConsumer<String> consumer = eventBus.consumer(Constant.BUSINESS_CONFIGURE_DERAIL);
        consumer.handler(message -> {

            // 同步注册功能
            JsonObject param = new JsonObject(message.body());
            Future<Void> configureDerailFuture = businessService.configureDerail(param);
            configureDerailFuture.onSuccess(suss -> {
                // 同步成功
                log.info("同步成功的消息体 {}",message.body());
            }).onFailure(fail -> {
                // 同步失败
                log.error("同步失败的消息体为 {} 异常为 {}",message.body(), ExceptionMessage.getStackTrace(fail));
            });
        });
    }

    /**
     * 同步配置权重
     */
    public void syncConfigureWeight(){
        MessageConsumer<String> consumer = eventBus.consumer(Constant.BUSINESS_CONFIGURE_WEIGHT);
        consumer.handler(message -> {
            // 同步注册功能
            JsonObject param = new JsonObject(message.body());
            Future<Void> configureWeightFuture = businessService.configureWeight(param);
            configureWeightFuture.onSuccess(suss -> {
                // 同步成功
                log.info("同步成功的消息体 {}",message.body());
            }).onFailure(fail -> {
                // 同步失败
                log.error("同步失败的消息体为 {} 异常为 {}",message.body(), ExceptionMessage.getStackTrace(fail));
            });
        });
    }

    /**
     * 同步 部署的bpmn
     */
    public void syncDeployBpmn(){
        MessageConsumer<String> consumer = eventBus.consumer(Constant.DEPLOY_BPMN);
        consumer.handler(message -> {
            // 同步注册功能
            JsonObject param = new JsonObject(message.body());
            Future<Void> deployBpmnFuture = containerService.deployBpmn(param);
            deployBpmnFuture.onSuccess(suss -> {
                // 同步成功
                log.info("同步成功的消息体 {}",message.body());
            }).onFailure(fail -> {
                // 同步失败
                log.error("同步失败的消息体为 {} 异常为 {}",message.body(), ExceptionMessage.getStackTrace(fail));
            });
        });
    }

    /**
     * 同步加载类class
     */
    public void syncLoaderClass(){
        MessageConsumer<String> consumer = eventBus.consumer(Constant.LOADER_JAR);
        consumer.handler(message -> {
            // 同步注册功能
            JsonObject param = new JsonObject(message.body());
            Future<Void> loaderClassFuture = containerService.loaderClass(param);
            loaderClassFuture.onSuccess(suss -> {
                // 同步成功
                log.info("同步成功的消息体 {}",message.body());
            }).onFailure(fail -> {
                // 同步失败
                log.error("同步失败的消息体为 {} 异常为 {}",message.body(), ExceptionMessage.getStackTrace(fail));
            });
        });
    }



}
