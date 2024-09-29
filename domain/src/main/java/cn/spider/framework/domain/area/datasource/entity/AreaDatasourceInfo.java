package cn.spider.framework.domain.area.datasource.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author dds
 * @since 2024-09-28
 */
@TableName("area_datasource_info")
public class AreaDatasourceInfo {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 数据源名称
     */
    private String datasource;

    /**
     * 数据库的连接地址
     */
    private String url;

    /**
     * 数据库的用户名
     */
    private String name;

    /**
     * 数据库密码
     */
    private String password;

    /**
     * 数据库的连接初始化值
     */
    private Integer initialSize;

    /**
     * 数据库连接最小值
     */
    private Integer minIdle;

    /**
     * 数据库连接最大值
     */
    private Integer maxIdle;

    /**
     * 驱动名称
     */
    private String driverClassName;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(Integer initialSize) {
        this.initialSize = initialSize;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
