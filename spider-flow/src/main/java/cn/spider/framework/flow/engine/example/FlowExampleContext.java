package cn.spider.framework.flow.engine.example;

import cn.spider.framework.flow.engine.example.data.FlowExample;
import io.vertx.core.Promise;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.engine.example
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-29  20:03
 * @Description: 流程实例上下文对象
 * @Version: 1.0
 */
public class FlowExampleContext {
    private FlowExample flowExample;

    private Promise<Void> promise;


}
