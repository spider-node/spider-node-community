package cn.spider.framework.domain.area.http.entity;

import cn.spider.framework.domain.area.function.data.FunctionParamConfigModel;
import cn.spider.framework.domain.area.http.data.HttpHeader;
import cn.spider.framework.domain.area.http.enums.HttpType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * spider_http_工具
 * </p>
 *
 * @author dds
 * @since 2025-04-12
 */
@Data
@TableName(value = "spider_tool_http", autoResultMap = true)
public class SpiderToolHttp implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 功能名称
     */
    private String httpFunctionName;

    /**
     * http类型
     */
    private HttpType httpType;

    /**
     * 功能描述
     */
    private String httpFunctionDesc;

    /**
     * 功能url
     */
    private String functionUrl;

    /**
     * 入参对象配置
     */
    @TableField(value = "http_function_param_object", typeHandler = FastjsonTypeHandler.class)
    private Map<String, List<FunctionParamConfigModel>> httpFunctionParamObject;

    /**
     * 出参对象配置
     */
    @TableField(value = "http_function_result_object", typeHandler = FastjsonTypeHandler.class)
    private Map<String, List<FunctionParamConfigModel>> httpFunctionResultObject;


    /**
     * 入参class配置
     */
    @TableField(value = "http_function_param_class", typeHandler = FastjsonTypeHandler.class)
    private List<String> httpFunctionParamClass;

    /**
     * 出参class配置
     */
    @TableField(value = "http_function_result_class", typeHandler = FastjsonTypeHandler.class)
    private List<String> httpFunctionResultClass;

    /**
     * header配置
     */
    @TableField(value = "http_header", typeHandler = FastjsonTypeHandler.class)
    private List<HttpHeader> httpHeader;

    /**
     * 创建时间
     */
    private Date createTime;
}
