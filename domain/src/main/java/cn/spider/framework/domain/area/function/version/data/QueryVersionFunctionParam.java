package cn.spider.framework.domain.area.function.version.data;

import cn.spider.framework.domain.area.function.version.data.enums.VersionStatus;
import lombok.Data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.function.version.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-18  23:16
 * @Description: 查询-版本信息
 * @Version: 1.0
 */
@Data
public class QueryVersionFunctionParam {

    /**
     * 功能id
     */
    private String functionId;

    /**
     * 功能id
     */
    private String functionName;

    /**
     * 状态
     */
    private String status;

    /**
     * startId
     */
    private String startEventId;

    /**
     * 版本id
     */
    private String versionId;

    /**
     * 页数
     */
    private Integer page;

    /**
     * size
     */
    private Integer size;

}
