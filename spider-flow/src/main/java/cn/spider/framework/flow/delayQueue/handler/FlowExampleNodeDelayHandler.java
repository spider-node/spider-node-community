package cn.spider.framework.flow.delayQueue.handler;

import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.FlowExampleData;
import cn.spider.framework.flow.delayQueue.DelayHandler;
import cn.spider.framework.flow.timer.data.FlowDelayExample;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.delayQueue.handler
 * @Author: dengdongsheng
 * @CreateTime: 2023-07-05  14:54
 * @Description: 流程实例节点的处理handler
 * @Version: 1.0
 */
public class FlowExampleNodeDelayHandler implements DelayHandler<FlowDelayExample> {
    private EventManager eventManager;

    private EventType eventType;

    public FlowExampleNodeDelayHandler(EventManager eventManager) {
        this.eventManager = eventManager;
        this.eventType = EventType.FLOW_EXAMPLE_DELAY;
    }


    @Override
    public void execute(FlowDelayExample o) {
        FlowExampleData data = FlowExampleData.builder().exampleId(o.getExampleId()).brokerName(o.getBrokerName()).build();
        eventManager.sendMessage(eventType,data);
    }
}
