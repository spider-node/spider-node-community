package cn.spider.framework.domain.area.flowdata.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 数据流与业务功能版本
 * </p>
 *
 * @author dds
 * @since 2024-09-19
 */
@TableName("spider_data_flow_area_function")
public class SpiderDataFlowAreaFunction implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 数据流id
     */
    private Integer dataFlowId;

    /**
     * 功能id
     */
    private String functionId;

    /**
     * 版本id
     */
    private String functionVersionId;

    /**
     * INIT初始化/ENABLE启用
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDataFlowId() {
        return dataFlowId;
    }

    public void setDataFlowId(Integer dataFlowId) {
        this.dataFlowId = dataFlowId;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public String getFunctionVersionId() {
        return functionVersionId;
    }

    public void setFunctionVersionId(String functionVersionId) {
        this.functionVersionId = functionVersionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "SpiderDataFlowAreaFunction{" +
            "id = " + id +
            ", dataFlowId = " + dataFlowId +
            ", functionId = " + functionId +
            ", functionVersionId = " + functionVersionId +
            ", status = " + status +
            ", createTime = " + createTime +
        "}";
    }
}
