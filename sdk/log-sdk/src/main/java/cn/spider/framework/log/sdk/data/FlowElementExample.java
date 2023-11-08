package cn.spider.framework.log.sdk.data;

import cn.spider.framework.common.utils.ExceptionMessage;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.log.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-18  19:08
 * @Description: TODO
 * @Version: 1.0
 */
public class FlowElementExample {

    /**
     * 请求id
     */
    private String id;

    /**
     * requestId
     */
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
     * 异常
     */
    private String exception;

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

    /**
     * 耗时
     */
    private Long takeTime;

    private Long finalEndTime;

    public Long getFinalEndTime() {
        return finalEndTime;
    }

    public void setFinalEndTime(Long finalEndTime) {
        this.finalEndTime = finalEndTime;
    }

    public Long getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(Long takeTime) {
        this.takeTime = takeTime;
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


    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
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

    public String getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(String requestParam) {
        this.requestParam = requestParam;
    }

    public String getReturnParam() {
        return returnParam;
    }

    public void setReturnParam(String returnParam) {
        this.returnParam = returnParam;
    }
}
