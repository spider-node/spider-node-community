package cn.spider.framework.flow.business.data;

import cn.spider.framework.flow.business.enums.FunctionStatus;
import lombok.Data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.business
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-11  23:12
 * @Description: 功能开关类
 * @Version: 1.0
 */
@Data
public class DerailFunctionVersion {
    /**
     * 功能名称
     */
    private String functionId;
    /**
     * 版本
     */
    private String version;
    /**
     * 状态
     */
    private FunctionStatus functionStatus;
}
