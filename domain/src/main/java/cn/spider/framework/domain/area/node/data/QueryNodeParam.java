package cn.spider.framework.domain.area.node.data;

import cn.spider.framework.domain.area.node.data.enums.NodeStatus;
import lombok.Data;

import java.util.Set;

/**
 * 查询节点的参数
 */
@Data
public class QueryNodeParam {
    private String name;

    private String areaId;

    private String taskComponent;

    private String taskService;

    private String areaName;

    /**
     * 状态
     */
    private NodeStatus status;

    private Integer page;

    private Integer size;
}
