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
 * @CreateTime: 2023-04-17  22:31
 * @Description: spider-flow-实例的日志实体
 * @Version: 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "bms-spider-log-v1", shards = 10, replicas = 0)
@Data
public class SpiderFlowExampleLog extends SpiderLog implements Serializable {

    /**
     * 请求的requestId-当作id存储
     */
    @Id
    private String id;

    /**
     * 请求参数
     */
    @Field(name = "requestParam",type = FieldType.Text)
    private String requestParam;

    @Field(name = "returnParam",type = FieldType.Text)
    private String returnParam;

    /**
     * 对应的-执行的broker
     */
    @Field(name = "brokerName",type = FieldType.Keyword)
    private String brokerName;

    /**
     * 执行状态
     */
    @Field(name = "status",type = FieldType.Keyword)
    private String status;

    /**
     * 异常信息
     */
    @Field(name = "exception",type = FieldType.Keyword)
    private String exception;

    /**
     * 事务状态
     */
    @Field(name = "transactionStatus",type = FieldType.Keyword)
    private String transactionStatus;

    /**
     * 功能名称
     */
    @Field(name = "functionName",type = FieldType.Keyword)
    private String functionName;

    /**
     * 功能id
     */
    @Field(name = "functionId",type = FieldType.Keyword)
    private String functionId;

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

    // 耗时
    private Long takeTime;
}
