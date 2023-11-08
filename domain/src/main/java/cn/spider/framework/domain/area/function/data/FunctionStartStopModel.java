package cn.spider.framework.domain.area.function.data;

import lombok.Data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.function.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-15  22:13
 * @Description: 功能启停
 * @Version: 1.0
 */
@Data
public class FunctionStartStopModel {

    /**
     * 功能id
     */
    private String functionId;

    /**
     * 启停状态
     */
    private FunctionStatus status;
}
