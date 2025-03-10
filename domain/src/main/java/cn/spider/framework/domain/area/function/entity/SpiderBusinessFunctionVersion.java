package cn.spider.framework.domain.area.function.entity;

import cn.spider.framework.domain.area.data.NodeInfos;
import cn.spider.framework.domain.area.data.enums.BpmnStatus;
import cn.spider.framework.domain.area.function.data.FunctionParamConfigModel;
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
import java.util.List;
import java.util.Map;

/**
 * <p>
 * spider领域功能版本
 * </p>
 *
 * @author dds
 * @since 2024-10-13
 */
@TableName(value = "spider_business_function_version", autoResultMap = true)
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

    private String bpmnXml;

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

    @TableField(value = "node_info", typeHandler = FastjsonTypeHandler.class)
    private NodeInfos nodeInfos;

    /**
     * 返回的字段信息
     */
    @TableField(value = "result_Mapping", typeHandler = FastjsonTypeHandler.class)
    private FunctionParamOutput resultMapping;

    private String runClass;

    private String resultClass;

    @TableField(value = "run_object_config", typeHandler = FastjsonTypeHandler.class)
    private Map<String, List<FunctionParamConfigModel>> runObjectConfig;

    @TableField(value = "result_object_config", typeHandler = FastjsonTypeHandler.class)
    private Map<String, List<FunctionParamConfigModel>> resultObjectConfig;

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

    @TableField(value = "input_param_java_class", typeHandler = FastjsonTypeHandler.class)
    private List<String> inputParamJavaClass;

    @TableField(value = "out_param_java_class", typeHandler = FastjsonTypeHandler.class)
    private List<String> outputParamJavaClass;

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

    public String getRunClass() {
        return runClass;
    }

    public void setRunClass(String runClass) {
        this.runClass = runClass;
    }

    public String getResultClass() {
        return resultClass;
    }

    public void setResultClass(String resultClass) {
        this.resultClass = resultClass;
    }

    public Map<String, List<FunctionParamConfigModel>> getRunObjectConfig() {
        return runObjectConfig;
    }

    public void setRunObjectConfig(Map<String, List<FunctionParamConfigModel>> runObjectConfig) {
        this.runObjectConfig = runObjectConfig;
    }

    public Map<String, List<FunctionParamConfigModel>> getResultObjectConfig() {
        return resultObjectConfig;
    }

    public void setResultObjectConfig(Map<String, List<FunctionParamConfigModel>> resultObjectConfig) {
        this.resultObjectConfig = resultObjectConfig;
    }

    public String getBpmnXml() {
        return bpmnXml;
    }

    public void setBpmnXml(String bpmnXml) {
        this.bpmnXml = bpmnXml;
    }

    public NodeInfos getNodeInfos() {
        return nodeInfos;
    }

    public void setNodeInfos(NodeInfos nodeInfos) {
        this.nodeInfos = nodeInfos;
    }

    public List<String> getInputParamJavaClass() {
        return inputParamJavaClass;
    }

    public void setInputParamJavaClass(List<String> inputParamJavaClass) {
        this.inputParamJavaClass = inputParamJavaClass;
    }

    public List<String> getOutputParamJavaClass() {
        return outputParamJavaClass;
    }

    public void setOutputParamJavaClass(List<String> outputParamJavaClass) {
        this.outputParamJavaClass = outputParamJavaClass;
    }
}
