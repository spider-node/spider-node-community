package cn.spider.framework.common.event.data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StartFlowExampleEventData extends EventData {

    /**
     * 流程的其实号
     */
    private String startId;

    /**
     * 链路唯一的请求id
     */
    private String requestId;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 功能id
     */
    private String functionId;

    /**
     * 请求参数
     */
    private Object requestParam;

    /**
     * 请求参数的class路径
     */
    private String requestClassType;

}
