package cn.spider.framework.domain.area.function.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * spider领域功能版本
 * </p>
 *
 * @author dds
 * @since 2024-10-13
 */
@TableName("spider_business_function_version")
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
    private String bpmnStatus;

    /**
     * 返回的字段信息
     */
    private String resultMapping;

    /**
     * 返回参数
     */
    private String runMapping;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    public String getRunMapping() {
        return runMapping;
    }

    public void setRunMapping(String runMapping) {
        this.runMapping = runMapping;
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

    public String getBpmnStatus() {
        return bpmnStatus;
    }

    public void setBpmnStatus(String bpmnStatus) {
        this.bpmnStatus = bpmnStatus;
    }

    public String getResultMapping() {
        return resultMapping;
    }

    public void setResultMapping(String resultMapping) {
        this.resultMapping = resultMapping;
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
        return "SpiderBusinessFunctionVersion{" +
            "id = " + id +
            ", functionName = " + functionName +
            ", desc = " + desc +
            ", version = " + version +
            ", functionId = " + functionId +
            ", bpmnUrl = " + bpmnUrl +
            ", startEventId = " + startEventId +
            ", bpmnName = " + bpmnName +
            ", bpmnStatus = " + bpmnStatus +
            ", resultMapping = " + resultMapping +
            ", status = " + status +
            ", createTime = " + createTime +
        "}";
    }
}
