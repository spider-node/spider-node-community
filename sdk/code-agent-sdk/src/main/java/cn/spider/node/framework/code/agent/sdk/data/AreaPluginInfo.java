package cn.spider.node.framework.code.agent.sdk.data;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 领域功能的插件机器
 */
@Data
public class AreaPluginInfo {

    /**
     * id
     */
    private Integer id;

    /**
     * 数据源id
     */
    private Integer datasourceId;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * 业务功能方法提供的类
     */
    private String areaFunctionClass;

    /**
     * 业务方法的入参
     */
    private String areaFunctionParamClass;

    /**
     * 业务方法的出参
     */
    private String areaFunctionResultClass;

    /**
     * 使用的基础版本
     */
    private String baseVersion;

    /**
     * 状态-init,init_fail,init_suss
     */
    private String status;

    /**
     * 版本
     */
    private String version;

    private LocalDateTime createTime;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 功能描述
     */
    private String functionDesc;

    /**
     * pom中的groupId
     */
    private String groupId;

    /**
     * pom中的artifactId
     */
    private String artifactId;

    /**
     * 领域id
     */
    private String areaId;

    /**
     * 领域名称
     */
    private String areaName;

    /**
     * 子域id
     */
    private Integer sonAreaId;

    /**
     * 子域名称
     */
    private String sonAreaName;
}
