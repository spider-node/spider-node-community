package cn.spider.framework.domain.area.function.entity;

import cn.spider.framework.domain.area.data.enums.BpmnStatus;
import cn.spider.framework.domain.area.function.version.data.enums.VersionStatus;
import cn.spider.framework.domain.sdk.data.FunctionParamInput;
import cn.spider.framework.domain.sdk.data.FunctionParamOutput;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * spider领域功能版本
 * </p>
 *
 * @author dds
 * @since 2024-10-13
 */
@TableName(value = "spider_business_function_version",autoResultMap = true)
public class SpiderBusinessFunctionVersion implements Serializable {

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
     * 领域描述
     */
    @TableField("`desc`")
    private String desc;

    /**
     * 功能版本
     */
    private String version;

    /**
     * 功能id
     */
    private String functionId;

    /**
     * bpmn-url
     */
    private String bpmnUrl;

    /**
     * 功能启动id
     */
    private String startEventId;

    /**
     * 模型名称
     */
    private String bpmnName;

    /**
     * bpmn_状态
     */
    private BpmnStatus bpmnStatus;

    /**
     * 返回的字段信息
     */
    @TableField(value = "result_Mapping", typeHandler = FastjsonTypeHandler.class)
    private FunctionParamOutput resultMapping;

    /**
     * 返回参数
     */
    @TableField(value = "run_mapping", typeHandler = FastjsonTypeHandler.class)
    private FunctionParamInput runMapping;

    /**
     * 状态
     */
    private VersionStatus status;

    /**
     * 命中规则
     */
    private String rule;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 数据流id
     */
    private Integer dataFlowId;

    /**
     * 数据流名称
     */
    private String dataFlowName;

    public String getDataFlowName() {
        return dataFlowName;
    }

    public void setDataFlowName(String dataFlowName) {
        this.dataFlowName = dataFlowName;
    }

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public String getBpmnUrl() {
        return bpmnUrl;
    }

    public void setBpmnUrl(String bpmnUrl) {
        this.bpmnUrl = bpmnUrl;
    }

    public String getStartEventId() {
        return startEventId;
    }

    public void setStartEventId(String startEventId) {
        this.startEventId = startEventId;
    }

    public String getBpmnName() {
        return bpmnName;
    }

    public void setBpmnName(String bpmnName) {
        this.bpmnName = bpmnName;
    }

    public FunctionParamOutput getResultMapping() {
        return resultMapping;
    }

    public void setResultMapping(FunctionParamOutput resultMapping) {
        this.resultMapping = resultMapping;
    }

    public FunctionParamInput getRunMapping() {
        return runMapping;
    }

    public void setRunMapping(FunctionParamInput runMapping) {
        this.runMapping = runMapping;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public BpmnStatus getBpmnStatus() {
        return bpmnStatus;
    }

    public void setBpmnStatus(BpmnStatus bpmnStatus) {
        this.bpmnStatus = bpmnStatus;
    }

    public VersionStatus getStatus() {
        return status;
    }

    public void setStatus(VersionStatus status) {
        this.status = status;
    }

    public Integer getDataFlowId() {
        return dataFlowId;
    }

    public void setDataFlowId(Integer dataFlowId) {
        this.dataFlowId = dataFlowId;
    }
}
