package cn.spider.framework.domain.area.function.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 领域业务功能
 * </p>
 *
 * @author dds
 * @since 2024-10-13
 */
@TableName("spider_business_function")
public class SpiderBusinessFunction implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private String id;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 领域描述
     */
    private String desc;

    /**
     * 负责人
     */
    private String director;

    /**
     * 状态
     */
    private String status;

    /**
     * 领域id
     */
    private String areaId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "SpiderBusinessFunction{" +
            "id = " + id +
            ", functionName = " + functionName +
            ", serviceName = " + serviceName +
            ", desc = " + desc +
            ", director = " + director +
            ", status = " + status +
            ", areaId = " + areaId +
            ", createTime = " + createTime +
        "}";
    }
}
