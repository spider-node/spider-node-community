package cn.spider.framework.domain.area.sondomain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

/**
 * <p>
 * 子域
 * </p>
 *
 * @author dds
 * @since 2024-09-20
 */
@TableName("spider_son_area")
public class SpiderSonArea {

    @TableId(value = "id", type = IdType.AUTO)
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
     * 创建时间
     */
    private Date createTime;

    /**
     * 描述
     */
    private String SonAreaDesc;

    public String getSonAreaDesc() {
        return SonAreaDesc;
    }

    public void setSonAreaDesc(String sonAreaDesc) {
        SonAreaDesc = sonAreaDesc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getSonAreaName() {
        return sonAreaName;
    }

    public void setSonAreaName(String sonAreaName) {
        this.sonAreaName = sonAreaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
