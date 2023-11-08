package cn.spider.framework.log.sdk.data;

import java.time.LocalDateTime;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.log.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-18  19:13
 * @Description: TODO
 * @Version: 1.0
 */
public class QueryFlowElementExample {
    /**
     * 请求id
     */
    private String id;

    private String requestId;

    /**
     * 节点名称
     */
    private String flowElementName;

    /**
     * 节点id
     */
    private String flowElementId;

    /**
     * 功能id
     */
    private String functionId;

    /**
     * 节点执行参数
     */
    private String requestParam;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 该节点返回参数
     */
    private String returnParam;

    /**
     * 执行状态
     */
    private String status;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    private int page;

    private int size;

    /**
     * 耗时
     */
    private Long gtTakeTime;

    /**
     * 耗时
     */
    private Long ltTakeTime;

    public Long getGtTakeTime() {
        return gtTakeTime;
    }

    public void setGtTakeTime(Long gtTakeTime) {
        this.gtTakeTime = gtTakeTime;
    }

    public Long getLtTakeTime() {
        return ltTakeTime;
    }

    public void setLtTakeTime(Long ltTakeTime) {
        this.ltTakeTime = ltTakeTime;
    }


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getFlowElementName() {
        return flowElementName;
    }

    public void setFlowElementName(String flowElementName) {
        this.flowElementName = flowElementName;
    }

    public String getFlowElementId() {
        return flowElementId;
    }

    public void setFlowElementId(String flowElementId) {
        this.flowElementId = flowElementId;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public String getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(String requestParam) {
        this.requestParam = requestParam;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getReturnParam() {
        return returnParam;
    }

    public void setReturnParam(String returnParam) {
        this.returnParam = returnParam;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
}
