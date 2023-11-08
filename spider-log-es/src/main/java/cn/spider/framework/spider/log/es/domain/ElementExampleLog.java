package cn.spider.framework.spider.log.es.domain;

import cn.spider.framework.log.sdk.enums.ExampleType;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.log.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-18  21:59
 * @Description: TODO
 * @Version: 1.0
 */
@Builder
@Data
public class ElementExampleLog {
    /**
     * 实例类型
     */
    private ExampleType exampleType;


    private SpiderLog exampleLog;

}
