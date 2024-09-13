package cn.spider.node.framework.code.agent.sdk.data;

import lombok.Data;

import java.util.Set;

@Data
public class CreateProjectResult {
    /**
     * 部署code
     */
    private String status;

    /**
     * 部署过程异常
     */
    private String errorStackTrace;

    /**
     * mvn install后的异常信息
     */
    private String mvnInstallFailStackTrace;

    /**
     * 插件名称
     */
    private String bizName;

    /**
     * 插件版本
     */
    private String bizVersion;

    /**
     * 插件的url地址
     */
    private String bizUrl;

    /**
     * 上一个版本号
     */
    private String beforeVersion;

    /**
     * 上一个版本部署的ip信息
     */
    private Set<String> beforeIps;

    /**
     * 实例数量
     */
    private Integer instancesNum;

    /**
     * 组件
     */
    private String taskComponent;

    /**
     * 组件功能
     */
    private String taskService;

    /**
     * 功能id
     */
    private Integer id;
}
