package cn.spider.framework.flow.delayQueue.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum DelayQueueEnum {
    FLOW_DELAY("FLOW_DELAY","flow任务延迟队列", "flow_delay_example"),

    FLOW_REMOVE_DELAY("FLOW_REMOVE_DELAY","实例移除", "flow_delete");

    /**
     * 延迟队列 Redis Key
     */
    private String code;

    /**
     * 中文描述
     */
    private String name;

    /**
     * 延迟队列具体业务实现的 Bean
     * 可通过 Spring 的上下文获取
     */
    private String beanId;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getBeanId() {
        return beanId;
    }
}
