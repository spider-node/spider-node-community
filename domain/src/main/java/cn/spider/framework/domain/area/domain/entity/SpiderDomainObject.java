package cn.spider.framework.domain.area.domain.entity;

import cn.spider.framework.domain.area.function.data.FunctionParamConfigModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 领域对象
 * </p>
 *
 * @author dds
 * @since 2025-04-22
 */
@Data
@TableName(value = "spider_domain_object", autoResultMap = true)
public class SpiderDomainObject implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String objectDesc;

    /**
     * 领域对象
     */
    private String domainObjectName;

    /**
     * 子域数组
     */
    @TableField(value = "domain_object_field", typeHandler = FastjsonTypeHandler.class)
    private List<FunctionParamConfigModel> domainObjectField;
}
