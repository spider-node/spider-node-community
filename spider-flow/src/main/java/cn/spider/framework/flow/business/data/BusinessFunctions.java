package cn.spider.framework.flow.business.data;

import cn.spider.framework.flow.business.enums.FunctionStatus;
import cn.spider.framework.flow.business.enums.IsAsync;
import cn.spider.framework.flow.business.enums.IsRetry;
import java.time.LocalDateTime;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.business
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-11  16:34
 * @Description: TODO
 * @Version: 1.0
 */
public class BusinessFunctions {
    /**
     * 功能名称
     */
    private String name;

    /**
     *
     */
    private String id;

    /**
     * 功能版本
     */
    private String version;

    /**
     * 启动流程的startId
     */
    private String startId;

    /**
     * 业务功能状态
     */
    private FunctionStatus status;

    /**
     * 描述
     */
    private String desc;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * bpmn的文件名称
     */
    private String bpmnName;

    /**
     * 是否异步
     */
    private IsAsync isAsync;


    private Integer retryCount;

    private IsRetry isRetry;

    private String requestClass;

    private String resultMapping;

    public String getResultMapping() {
        return resultMapping;
    }

    public void setResultMapping(String resultMapping) {
        this.resultMapping = resultMapping;
    }

    public String getRequestClass() {
        return requestClass;
    }

    public void setRequestClass(String requestClass) {
        this.requestClass = requestClass;
    }

    public IsRetry getIsRetry() {
        return isRetry;
    }

    public void setIsRetry(IsRetry isRetry) {
        this.isRetry = isRetry;
    }

    public IsAsync getIsAsync() {
        return isAsync;
    }

    public void setIsAsync(IsAsync isAsync) {
        this.isAsync = isAsync;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStartId() {
        return startId;
    }

    public void setStartId(String startId) {
        this.startId = startId;
    }

    public FunctionStatus getStatus() {
        return status;
    }

    public void setStatus(FunctionStatus status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getBpmnName() {
        return bpmnName;
    }

    public void setBpmnName(String bpmnName) {
        this.bpmnName = bpmnName;
    }

    public void retry(){
        this.retryCount --;
    }
}
