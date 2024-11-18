package cn.spider.framework.domain.area.sondomain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * <p>
 * spider领域基础信息
 * </p>
 *
 * @author dds
 * @since 2024-09-20
 */
@TableName("area_domain_base_info")
public class AreaDomainBaseInfo {
    @TableId(value = "id", type = IdType.AUTO)
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
     * 域对象
     */
    private String domainObject;

    /**
     * 域对象包名称
     */
    private String domainObjectPackage;

    /**
     * 域对象实体名称
     */
    private String domainObjectEntityName;

    /**
     * 领域基础操作的接口名称
     */
    private String domainObjectServiceName;

    /**
     * 领域基础操作的接口包名称
     */
    private String domainObjectServicePackage;

    /**
     * 领域基础操作的接口实现类名称
     */
    private String domainObjectServiceImplName;

    /**
     * 领域基础操作的接口实现类包名
     */
    private String domainObjectServiceImplPackage;

    /**
     * 版本
     */
    private String version;

    /**
     * pom文件中的group_id
     */
    private String groupId;

    /**
     * pom文件中的artifact_id
     */
    private String artifactId;

    private LocalDateTime createTime;

    /**
     * 子域名称
     */
    private String sonAreaName;

    /**
     * 子域id
     */
    private Integer sonAreaId;

    /**
     * 领域id
     */
    private String areaId;

    /**
     * 主领域名称
     */
    private String areaName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Integer datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public String getDomainObject() {
        return domainObject;
    }

    public void setDomainObject(String domainObject) {
        this.domainObject = domainObject;
    }

    public String getDomainObjectPackage() {
        return domainObjectPackage;
    }

    public void setDomainObjectPackage(String domainObjectPackage) {
        this.domainObjectPackage = domainObjectPackage;
    }

    public String getDomainObjectEntityName() {
        return domainObjectEntityName;
    }

    public void setDomainObjectEntityName(String domainObjectEntityName) {
        this.domainObjectEntityName = domainObjectEntityName;
    }

    public String getDomainObjectServiceName() {
        return domainObjectServiceName;
    }

    public void setDomainObjectServiceName(String domainObjectServiceName) {
        this.domainObjectServiceName = domainObjectServiceName;
    }

    public String getDomainObjectServicePackage() {
        return domainObjectServicePackage;
    }

    public void setDomainObjectServicePackage(String domainObjectServicePackage) {
        this.domainObjectServicePackage = domainObjectServicePackage;
    }

    public String getDomainObjectServiceImplName() {
        return domainObjectServiceImplName;
    }

    public void setDomainObjectServiceImplName(String domainObjectServiceImplName) {
        this.domainObjectServiceImplName = domainObjectServiceImplName;
    }

    public String getDomainObjectServiceImplPackage() {
        return domainObjectServiceImplPackage;
    }

    public void setDomainObjectServiceImplPackage(String domainObjectServiceImplPackage) {
        this.domainObjectServiceImplPackage = domainObjectServiceImplPackage;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getSonAreaName() {
        return sonAreaName;
    }

    public void setSonAreaName(String sonAreaName) {
        this.sonAreaName = sonAreaName;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Integer getSonAreaId() {
        return sonAreaId;
    }

    public void setSonAreaId(Integer somAreaId) {
        this.sonAreaId = somAreaId;
    }
}
