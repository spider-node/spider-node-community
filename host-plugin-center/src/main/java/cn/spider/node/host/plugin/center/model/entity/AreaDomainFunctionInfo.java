package cn.spider.node.host.plugin.center.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2024-09-03
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName("area_domain_function_info")
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
     * 功能名称
     */
    private String functionName;

    /**
     * 功能描述
     */
    private String functionDesc;

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
     * 使用的基础版本
     */
    private String baseVersion;

    /**
     * 状态-init,init_fail,init_suss
     */
    private String status;

    /**
     * 版本
     */
    private String version;

    /**
     * pom文件中的group_id
     */
    private String groupId;

    /**
     * pom文件中的artifact_id
     */
    private String artifactId;

    /**
     * 组件名称
     */
    private String taskComponent;

    /**
     * 组件方法
     */
    private String taskService;

    private LocalDateTime createTime;

    private String bizUrl;

    /**
     * 部署的实例数量
     */
    private Integer instanceNum;
}
