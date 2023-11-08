package cn.spider.framework.common.event;
import cn.spider.framework.common.role.EventTypeRole;
import cn.spider.framework.common.role.SystemRole;

public enum EventType {
    START_FLOW_EXAMPLE(SystemRole.FLOW_EXAMPLE,"start_flow_example","启动新流程",EventTypeRole.BUSINESS),

    END_FLOW_EXAMPLE(SystemRole.FLOW_EXAMPLE,"end_flow_example","流程执行结束",EventTypeRole.BUSINESS),

    ELEMENT_START(SystemRole.ELEMENT_EXAMPLE,"start_element_example","开始执行节点",EventTypeRole.BUSINESS),

    ELEMENT_END(SystemRole.ELEMENT_EXAMPLE,"end_element_example","执行节点结束",EventTypeRole.BUSINESS),

    START_TRANSACTION(SystemRole.TRANSACTION,"run_transaction","开始执行事务",EventTypeRole.BUSINESS),

    END_TRANSACTION(SystemRole.TRANSACTION,"end_transaction","事务执行结束",EventTypeRole.BUSINESS),

    REGISTER_TRANSACTION(SystemRole.TRANSACTION,"register_transaction","注册事务",EventTypeRole.BUSINESS),

    //generate
    LEADER_GENERATE(SystemRole.CONTROLLER,"leader_generate","通知follower,leader已经被创建了",EventTypeRole.SYSTEM),
    //death
    FOLLOWER_DEATH(SystemRole.CONTROLLER,"leader_generate","通知集群所有节点,某节点挂了",EventTypeRole.SYSTEM),

    TRANSCRIPT_CHANGE(SystemRole.CONTROLLER,"transcript_change","副本变化事件",EventTypeRole.SYSTEM),
    // replace
    LEADER_REPLACE_CHANGE(SystemRole.CONTROLLER,"leader_replace_change","副本替换具体的leader",EventTypeRole.SYSTEM),

    BUSINESS_REGISTER_FUNCTION(SystemRole.FLOW_EXAMPLE,"business_register_function","注册功能",EventTypeRole.SYSTEM),

    BUSINESS_CONFIGURE_DERAIL(SystemRole.FLOW_EXAMPLE,"business_config_derail","配置开关",EventTypeRole.SYSTEM),

    BUSINESS_CONFIGURE_WEIGHT(SystemRole.FLOW_EXAMPLE,"business_configure_weight","业务功能版本权重",EventTypeRole.SYSTEM),

    LOADER_JAR(SystemRole.FLOW_EXAMPLE,"loader_jar","部署jar包的class",EventTypeRole.SYSTEM),

    DESTROY_JAR(SystemRole.FLOW_EXAMPLE,"destroy_jar","卸载",EventTypeRole.SYSTEM),

    DEPLOY_BPMN(SystemRole.FLOW_EXAMPLE,"deploy_bpmn","部署bpmn",EventTypeRole.SYSTEM),

    DESTROY_BPMN(SystemRole.FLOW_EXAMPLE,"destroy_bpmn","卸载bpmn",EventTypeRole.SYSTEM),
    //Cerebral fissure
    LEADER_CEREBRAL_FISSURE(SystemRole.CONTROLLER,"leader_cerebral_fissure","leader-脑裂",EventTypeRole.SYSTEM),

    BROKER_ASYNC_INFO(SystemRole.CONTROLLER,"broker_async_info","同步broker自身的信息",EventTypeRole.SYSTEM),

    STOP_FUNCTION(SystemRole.CONTROLLER,"stop_function","停止该功能的调用",EventTypeRole.SYSTEM),

    FLOW_EXAMPLE_DELAY(SystemRole.FLOW_EXAMPLE,"flow_example_delay","流程延迟",EventTypeRole.SYSTEM),

    FLOW_EXAMPLE_REMOVE_DELAY(SystemRole.FLOW_EXAMPLE,"flow_example_remove_delay","延迟移除流程实例",EventTypeRole.SYSTEM),

    FUNCTION_START_STOP(SystemRole.CONTROLLER,"function_start_stop","功能启停",EventTypeRole.SYSTEM),
    ;
    private SystemRole role;

    private String name;

    private String desc;

    private EventTypeRole eventTypeRole;

    EventType(SystemRole role, String name, String desc,EventTypeRole eventTypeRole) {
        this.role = role;
        this.name = name;
        this.desc = desc;
        this.eventTypeRole = eventTypeRole;
    }

    public String queryAddr(){
        return this.role.name()+this.name;
    }

    public SystemRole getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public EventTypeRole getEventTypeRole() {
        return eventTypeRole;
    }
}
