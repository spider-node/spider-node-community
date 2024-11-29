package cn.spider.node.host.plugin.center.model.entity;

import cn.spider.node.host.plugin.center.model.entity.enums.PluginStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 宿主应用
 * </p>
 *
 * @author dds
 * @since 2024-09-03
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName("spider_plugin_deploy_info")
public class SpiderPluginDeployInfo {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * ip
     */
    private String ip;

    /**
     * 功能id
     */
    private Integer functionId;

    /**
     * 组件名称
     */
    private String taskComponent;

    /**
     * 组件方法
     */
    private String taskService;

    /**
     * 版本
     */
    private String version;

    /**
     * 状态
     */
    private PluginStatus status;

    /**
     * 领域功能的版本id
     */
    private String domainFunctionVersionId;
}
