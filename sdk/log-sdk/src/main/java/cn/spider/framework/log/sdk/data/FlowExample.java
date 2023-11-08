package cn.spider.framework.log.sdk.data;

import cn.spider.framework.common.utils.ExceptionMessage;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.log.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-18  16:50
 * @Description: TODO
 * @Version: 1.0
 */
public class FlowExample {
    /**
     * 请求的requestId-当作id存储
     */
    private String id;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 返回参数
     */
    private String returnParam;

    /**
     * 对应的-执行的broker
     */
    private String brokerName;

    /**
     * 执行状态
     */
    private String status;

    /**
     * 异常信息
     */
    private String exception;

    /**
     * 事务状态
     */
    private String transactionStatus;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 功能id
     */
    private String functionId;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    private Long takeTime;


    public Long getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(Long takeTime) {
        this.takeTime = takeTime;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
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

    public String getReturnParam() {
        return returnParam;
    }

    public void setReturnParam(String returnParam) {
        this.returnParam = returnParam;
    }
}
