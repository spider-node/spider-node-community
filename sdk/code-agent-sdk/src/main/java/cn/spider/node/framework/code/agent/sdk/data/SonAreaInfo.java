package cn.spider.node.framework.code.agent.sdk.data;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 子域详情
 */
@Data
public class SonAreaInfo {

    private Integer id;

    /**
     * 领域id
     */
    private String areaId;

    /**
     * 主域名称
     */
    private String areaName;

    /**
     * 领域名称
     */
    private String sonAreaName;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 数据源名称
     */
    private String datasource;

    /**
     * 创建事件
     */
    private LocalDateTime createTime;
}
