package cn.spider.framework.domain.area.flowdata.entity;

import cn.spider.framework.domain.area.flowdata.data.FlowDataDesc;
import cn.spider.framework.domain.sdk.data.DataFlowAnalysisModel;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;

import java.util.Date;

/**
 * <p>
 * 数据流
 * </p>
 *
 * @author dds
 * @since 2024-09-19
 */
@TableName(value = "spider_data_flow", autoResultMap = true)
public class SpiderDataFlow {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 数据流
     */
    @TableField(value = "data", typeHandler = FastjsonTypeHandler.class)
    private JSONObject data;

    /**
     * 子域数组
     */
    private String sonAreaIds;

    /**
     * INIT初始化/ENABLE启用
     */
    private String status;

    /**
     * 数据流名称
     */
    private String flowDataName;

    /**
     * 描述
     */
    @TableField(value = "flow_data_desc", typeHandler = FastjsonTypeHandler.class)
    private FlowDataDesc flowDataDesc;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(value = "data_flow_analysis_model", typeHandler = FastjsonTypeHandler.class)
    private DataFlowAnalysisModel dataFlowAnalysisModel;

    public FlowDataDesc getFlowDataDesc() {
        return flowDataDesc;
    }

    public void setFlowDataDesc(FlowDataDesc flowDataDesc) {
        this.flowDataDesc = flowDataDesc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }


    public String getSonAreaIds() {
        return sonAreaIds;
    }

    public void setSonAreaIds(String sonAreaIds) {
        this.sonAreaIds = sonAreaIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFlowDataName() {
        return flowDataName;
    }

    public void setFlowDataName(String flowDataName) {
        this.flowDataName = flowDataName;
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public DataFlowAnalysisModel getDataFlowAnalysisModel() {
        return dataFlowAnalysisModel;
    }

    public void setDataFlowAnalysisModel(DataFlowAnalysisModel dataFlowAnalysisModel) {
        this.dataFlowAnalysisModel = dataFlowAnalysisModel;
    }
}
