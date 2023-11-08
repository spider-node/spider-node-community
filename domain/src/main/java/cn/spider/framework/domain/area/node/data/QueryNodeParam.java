package cn.spider.framework.domain.area.node.data;

import lombok.Data;

/**
 * 查询节点的参数
 */
@Data
public class QueryNodeParam {
    private String name;

    private String areaId;

    private String taskComponent;

    private String taskService;

    private Integer page;

    private Integer size;
}
