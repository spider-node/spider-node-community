package cn.spider.node.framework.code.agent.sdk.data;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 子域的基础类
 */
@Data
public class SonAreaCodeBase {

    /**
     * 子域id
     */
    private Integer id;

    /**
     * 数据源id
     */
    private Integer datasourceId;

    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 子域名称
     */
    private String sonAreaName;

    /**
     * 子域对象
     */
    private String domainObject;

    /**
     * 子域对象包
     */
    private String domainObjectPackage;

    /**
     * 领域对象名称
     */
    private String domainObjectEntityName;

    /**
     * 子域的service名称
     */
    private String domainObjectServiceName;

    /**
     * 子域service包名称
     */
    private String domainObjectServicePackage;

    /**
     * 子域service实现类 名称
     */
    private String domainObjectServiceImplName;

    /**
     * 子域对象service实现类的表包名
     */
    private String domainObjectServiceImplPackage;

    /**
     * 基础版本
     */
    private String version;


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
     * 领域id
     */
    private String areaName;

    /**
     * 创建时间。
     */
    private LocalDateTime createTime;
}
