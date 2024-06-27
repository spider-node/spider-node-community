package cn.spider.framework.common.event.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class StartElementExampleData extends EventData {

    /**
     * 流程-节点name
     */
    private String flowElementName;

    /**
     * 流程节点id
     */
    private String flowElementId;

    /**
     * 请求的链路id
     */
    private String requestId;

    /**
     * 功能id
     */
    private String functionId;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 事务组id=xid
     */
    private String transactionGroupId;

    /**
     * 节点的事务id-》唯一
     */
    private String branchId;

    /**
     * 是否需要获取下一个节点
     */
    private Boolean isNext;
}
