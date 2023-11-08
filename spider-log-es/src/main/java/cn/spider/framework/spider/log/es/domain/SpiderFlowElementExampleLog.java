package cn.spider.framework.spider.log.es.domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.spider.log.es.domain
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-18  17:33
 * @Description: 流程节点日志-实体
 * @Version: 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "bms-spider-element-v1", shards = 10, replicas = 0)
@Data
public class SpiderFlowElementExampleLog extends SpiderLog implements Serializable {
    /**
     * 请求id
     */
    @Id
    private String id;

    /**
     * requestId
     */
    @Field(name = "requestId",type = FieldType.Keyword)
    private String requestId;

    /**
     * 节点名称
     */
    @Field(name = "flowElementName",type = FieldType.Keyword)
    private String flowElementName;

    /**
     * 节点id
     */
    @Field(name = "flowElementId",type = FieldType.Keyword)
    private String flowElementId;

    /**
     * 功能id
     */
    @Field(name = "functionId",type = FieldType.Keyword)
    private String functionId;

    /**
     * 节点执行参数
     */
    @Field(name = "requestParam",type = FieldType.Text)
    private String requestParam;

    /**
     * 功能名称
     */
    @Field(name = "functionName",type = FieldType.Keyword)
    private String functionName;

    /**
     * 该节点返回参数
     */
    @Field(name = "returnParam",type = FieldType.Text)
    private String returnParam;

    /**
     * 异常
     */
    @Field(name = "exception",type = FieldType.Keyword)
    private String exception;

    /**
     * 执行状态
     */
    @Field(name = "status",type = FieldType.Keyword)
    private String status;

    /**
     * 开始时间
     */
    @Field(name = "startTime",type = FieldType.Long)
    private Long startTime;

    /**
     * 结束时间
     */
    @Field(name = "endTime",type = FieldType.Long)
    private Long endTime;

    /**
     * 完成时间
     */
    @Field(name = "finalEndTime",type = FieldType.Long)
    private Long finalEndTime;

    /**
     * 事务组
     */
    @Field(name = "transactionGroupId",type = FieldType.Keyword)
    private String transactionGroupId;

    /**
     * 单个事务id
     */
    @Field(name = "branchId",type = FieldType.Keyword)
    private String branchId;

    /**
     * 事务id
     */
    @Field(name = "transactionStatus",type = FieldType.Keyword)
    private String transactionStatus;

    /**
     * 事务操作
     */
    @Field(name = "transactionOperate",type = FieldType.Keyword)
    private String transactionOperate;
}
