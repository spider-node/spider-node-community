package cn.spider.framework.domain.area.function.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.function.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-09-03  17:41
 * @Description: 获取执行
 * @Version: 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ExecuteFunctionInfo {

    /**
     * 功能id
     */
    private String functionId;

    /**
     * bpmn的起始id
     */
    private String startId;

    /**
     * 版本id
     */
    private String versionId;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 功能版本执行的请求参数
     */
    private String requestClass;

    /**
     * 返回参数的隐射
     */
    private String resultMapping;
}
