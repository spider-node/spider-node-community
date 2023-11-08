package cn.spider.framework.flow.business.data;

import lombok.Data;

import java.util.Map;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.business
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-11  18:09
 * @Description: 功能灰度权重
 * @Version: 1.0
 */
@Data
public class FunctionWeight {
    private String functionId;

    private Map<String,Integer> weightConfig;

}
