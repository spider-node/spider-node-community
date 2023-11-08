package cn.spider.framework.flow.delayQueue.handler;

import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.FlowExampleRemoveData;
import cn.spider.framework.flow.delayQueue.DelayHandler;
import cn.spider.framework.flow.timer.data.FlowDelayExample;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.delayQueue.handler
 * @Author: dengdongsheng
 * @CreateTime: 2023-07-05  15:09
 * @Description: TODO
 * @Version: 1.0
 */
public class FlowExampleRemoveHandler implements DelayHandler<FlowDelayExample> {

    private EventManager eventManager;

    private EventType eventType;

    public FlowExampleRemoveHandler(EventManager eventManager) {
        this.eventManager = eventManager;
        this.eventType = EventType.FLOW_EXAMPLE_REMOVE_DELAY;

    }

    @Override
    public void execute(FlowDelayExample flowDelayExample) {
        FlowExampleRemoveData data = FlowExampleRemoveData.builder().exampleId(flowDelayExample.getExampleId()).brokerName(flowDelayExample.getBrokerName()).build();
        eventManager.sendMessage(eventType,data);
    }
}
