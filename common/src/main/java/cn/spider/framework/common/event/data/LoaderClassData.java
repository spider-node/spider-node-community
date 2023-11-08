package cn.spider.framework.common.event.data;

import cn.spider.framework.common.data.enums.JarStatus;
import lombok.Builder;
import lombok.Data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.container.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-28  01:12
 * @Description: 加载 jar包功能的请求参数类
 * @Version: 1.0
 */
@Data
public class LoaderClassData extends EventData {
    private String jarName;

    private String classPath;

    private JarStatus status;

    private String url;

    private String id;
}
