package cn.spider.framework.flow.consumer.system;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.DestroyClassData;
import cn.spider.framework.common.event.data.LoaderClassData;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.flow.load.loader.ClassLoaderManager;
import com.alibaba.fastjson.JSON;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


public class AsyncDestroyClassHandler {

    private EventBus eventBus;

    private Vertx vertx;

    private ClassLoaderManager classLoaderManager;

    private EventType eventType = EventType.DESTROY_JAR;

    private String localBrokerName;

    public AsyncDestroyClassHandler(EventBus eventBus, Vertx vertx, ClassLoaderManager classLoaderManager) {
        this.eventBus = eventBus;
        this.vertx = vertx;
        this.classLoaderManager = classLoaderManager;
        registerConsumer();
        this.localBrokerName = BrokerInfoUtil.queryBrokerName(vertx);
    }

    public void registerConsumer(){
        MessageConsumer<String> consumer = eventBus.consumer(eventType.queryAddr());
        consumer.handler(message -> {
            MultiMap multiMap = message.headers();
            String brokerName = multiMap.get(Constant.BROKER_NAME);
            // 自身的事件不进行消费
            if(StringUtils.equals(brokerName,localBrokerName)){
                return;
            }
            DestroyClassData destroyClassData = JSON.parseObject(message.body(),DestroyClassData.class);
            classLoaderManager.unloadJar(destroyClassData.getJarName());
        });
    }
}
