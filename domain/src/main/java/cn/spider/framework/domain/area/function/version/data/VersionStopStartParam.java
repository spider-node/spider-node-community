package cn.spider.framework.domain.area.function.version.data;

import cn.spider.framework.domain.area.function.version.data.enums.VersionStatus;
import lombok.Data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.function.version.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-17  22:57
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class VersionStopStartParam {
    /**
     * 功能id
     */
    private String versionId;

    /**
     * 状态
     */
    private VersionStatus status;
}
