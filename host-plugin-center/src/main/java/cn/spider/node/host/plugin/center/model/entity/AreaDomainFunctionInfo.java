package cn.spider.node.host.plugin.center.model.entity;

import cn.spider.node.host.plugin.center.model.data.SonDomainModelInfo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * spider领域基础信息
 * </p>
 *
 * @author dds
 * @since 2024-08-06
 */
@Data
@TableName(value = "area_domain_function_info", autoResultMap = true)
public class AreaDomainFunctionInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 数据源id
     */
    private Integer datasourceId;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * 业务功能方法提供的类
     */
    private String areaFunctionClass;

    /**
     * 业务方法的入参
     */
    private String areaFunctionParamClass;

    /**
     * 业务方法的出参
     */
    private String areaFunctionResultClass;


    /**
     * 状态-init,init_fail,init_suss
     */
    private String status;

    /**
     * 版本
     */
    private String version;

    private LocalDateTime createTime;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 功能描述
     */
    private String functionDesc;

    /**
     * pom中的groupId
     */
    private String groupId;

    /**
     * pom中的artifactId
     */
    private String artifactId;

    /**
     * 领域id
     */
    private String areaId;

    /**
     * 领域名称
     */
    private String areaName;

    @TableField(value = "son_domain_info", typeHandler = FastjsonTypeHandler.class)
    private SonDomainModelInfo sonDomainModelInfo;

    /**
     * 组件
     */
    private String taskComponent;

    /**
     * 组件功能
     */
    private String taskService;

    /**
     * 文件 地址
     */
    private Integer instanceNum;

    private String bizUrl;
    /**
     * 领域功能版本id
     */
    private String domainFunctionVersionId;

    private Integer taskId;
}
