package cn.spider.node.framework.code.agent.sdk.data;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据源配置
 */
@Data
public class DatasourceInfo {

    /**
     * 数据源id
     */
    private Integer id;

    /**
     * 数据库连接地址
     */
    private String url;

    /**
     * 数据库用户名
     */
    private String name;

    /**
     * 密码
     */
    private String password;

    /**
     * 数据库名称
     */
    private String datasource;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
