package cn.spider.framework.log.sdk.data;
import java.time.LocalDateTime;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.log.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-18  15:26
 * @Description: TODO
 * @Version: 1.0
 */

public class QueryFlowExample {

    /**
     * requestId
     */
    private String id;

    /**
     * 业务参数
     */
    private String businessParam;

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

    private String brokerName;

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

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

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

    public String getBusinessParam() {
        return businessParam;
    }

    public void setBusinessParam(String businessParam) {
        this.businessParam = businessParam;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
